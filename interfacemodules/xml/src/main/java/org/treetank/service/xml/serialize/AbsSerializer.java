/**
 * Copyright (c) 2011, University of Konstanz, Distributed Systems Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the University of Konstanz nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.treetank.service.xml.serialize;

import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.Callable;

import org.treetank.access.NodeReadTrx;
import org.treetank.api.INodeReadTrx;
import org.treetank.api.ISession;
import org.treetank.axis.AbsAxis;
import org.treetank.axis.DescendantAxis;
import org.treetank.data.IConstants;
import org.treetank.data.interfaces.ITreeStructData;
import org.treetank.exception.TTException;
import org.treetank.exception.TTIOException;

/**
 * Class implements main serialization algorithm. Other classes can extend it.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
abstract class AbsSerializer implements Callable<Void> {

    /** Treetank session {@link ISession}. */
    protected final ISession mSession;

    /** Stack for reading end element. */
    protected final Stack<Long> mStack;

    /** Array with versions to print. */
    protected final long[] mVersions;

    /** Root node key of subtree to shredder. */
    protected final long mNodeKey;

    /**
     * Constructor.
     * 
     * @param paramSession
     *            {@link ISession}.
     * @param paramVersions
     *            versions which should be serialized: -
     */
    public AbsSerializer(final ISession paramSession, final long... paramVersions) {
        this(paramSession, 0, paramVersions);
    }

    /**
     * Constructor.
     * 
     * @param paramSession
     *            {@link ISession}.
     * @param paramKey
     *            Key of root node from which to shredder the subtree.
     * @param paramVersions
     *            versions which should be serialized: -
     */
    public AbsSerializer(final ISession paramSession, final long paramKey, final long... paramVersions) {
        mStack = new Stack<Long>();
        mVersions = paramVersions;
        mSession = paramSession;
        mNodeKey = paramKey;
    }

    /**
     * Serialize the storage.
     * 
     * @return null.
     * @throws TTException
     *             if can't call serailzer
     */
    public Void call() throws TTException {
        emitStartDocument();

        long[] versionsToUse;

        // if there are no versions specified, take the last one, of not version==0
        if (mVersions.length == 0) {
            if (mSession.getMostRecentVersion() > 0) {
                versionsToUse = new long[] {
                    mSession.getMostRecentVersion()
                };
            }// revision 0 = bootstrapped page
            else {
                versionsToUse = new long[0];
            }
        } // if there is any negative number specified, take the entire range of versions, otherwise take the
          // parameter
        else {
            // sort the versions first
            Arrays.sort(mVersions);
            if (mVersions[0] < 0) {
                versionsToUse = new long[(int)mSession.getMostRecentVersion() - 1];

                for (int i = 0; i < versionsToUse.length; i++) {
                    versionsToUse[i] = i + 1;
                }

            }// otherwise take the versions as requested as params
            else {
                int index = Arrays.binarySearch(mVersions, 1);
                versionsToUse = Arrays.copyOfRange(mVersions, index, mVersions.length);
            }
        }

        for (int i = 0; i < versionsToUse.length; i++) {

            INodeReadTrx rtx = new NodeReadTrx(mSession.beginBucketRtx(versionsToUse[i]));

            if (versionsToUse == null || mVersions.length > 1) {
                emitStartManualElement(i);
            }

            rtx.moveTo(mNodeKey);

            final AbsAxis descAxis = new DescendantAxis(rtx, true);

            // Setup primitives.
            boolean closeElements = false;
            long key = rtx.getNode().getDataKey();

            // Iterate over all nodes of the subtree including self.
            while (descAxis.hasNext()) {
                key = descAxis.next();
                ITreeStructData currentStruc = (ITreeStructData)rtx.getNode();

                // Emit all pending end elements.
                if (closeElements) {
                    while (!mStack.empty() && mStack.peek() != currentStruc.getLeftSiblingKey()) {
                        rtx.moveTo(mStack.pop());
                        emitEndElement(rtx);
                        rtx.moveTo(key);
                    }
                    if (!mStack.empty()) {
                        rtx.moveTo(mStack.pop());
                        emitEndElement(rtx);
                    }
                    rtx.moveTo(key);
                    closeElements = false;
                }
                // Emit node.
                emitStartElement(rtx);
                // Push end element to stack if we are a start element with
                // children.
                if (currentStruc.getKind() == IConstants.ELEMENT && currentStruc.hasFirstChild()) {
                    mStack.push(rtx.getNode().getDataKey());
                }

                // Remember to emit all pending end elements from stack if
                // required.
                if (!currentStruc.hasFirstChild() && !currentStruc.hasRightSibling()) {
                    closeElements = true;
                }

            }

            // Finally emit all pending end elements.
            while (!mStack.empty()) {
                rtx.moveTo(mStack.pop());
                emitEndElement(rtx);
            }

            if (versionsToUse == null || mVersions.length > 1) {
                emitEndManualElement(i);
            }
        }
        emitEndDocument();

        return null;
    }

    /** Emit start document. */
    protected abstract void emitStartDocument();

    /**
     * Emit start tag.
     * 
     * @param pRTX
     *            Treetank reading transaction {@link INodeReadTrx}.
     */
    protected abstract void emitStartElement(final INodeReadTrx pRTX) throws TTIOException;

    /**
     * Emit end tag.
     * 
     * @param paramRTX
     *            Treetank reading transaction {@link INodeReadTrx}.
     */
    protected abstract void emitEndElement(final INodeReadTrx paramRTX);

    /**
     * Emit a start tag, which specifies a revision.
     * 
     * @param paramVersion
     *            The revision to serialize.
     */
    protected abstract void emitStartManualElement(final long paramVersion);

    /**
     * Emit an end tag, which specifies a revision.
     * 
     * @param paramVersion
     *            The revision to serialize.
     */
    protected abstract void emitEndManualElement(final long paramVersion);

    /** Emit end document. */
    protected abstract void emitEndDocument();
}
