/**
 * Copyright (c) 2011, University of Konstanz, Distributed Systems Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Konstanz nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
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

package org.treetank.diff;

import java.util.Set;

import org.treetank.api.IDatabase;
import org.treetank.api.IReadTransaction;
import org.treetank.diff.DiffFactory.EDiffKind;
import org.treetank.exception.AbsTTException;
import org.treetank.node.ENodes;
import org.treetank.node.ElementNode;

/**
 * Full diff including attributes and namespaces. Note that this class is thread safe.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
final class FullDiff extends AbsDiff {

    /**
     * Constructor.
     * 
     * @param paramDb
     *            {@link IDatabase} instance
     * @param paramKey
     *            key of (sub)tree to check
     * @param paramNewRev
     *            new revision key
     * @param paramOldRev
     *            old revision key
     * @param paramDiffKind
     *            kind of diff (optimized or not)
     * @param paramObservers
     *            {@link Set} of Observers, which listen for the kinds of diff between two nodes
     * @throws AbsTTException
     *             if retrieving session fails
     */
    FullDiff(final IDatabase paramDb, final long paramKey, final long paramNewRev, final long paramOldRev,
        final EDiffKind paramDiffKind, final Set<IDiffObserver> paramObservers) throws AbsTTException {
        super(paramDb, paramKey, paramNewRev, paramOldRev, paramDiffKind, paramObservers);
    }

    /** {@inheritDoc} */
    @Override
    boolean checkNodes(final IReadTransaction paramFirstRtx, final IReadTransaction paramSecondRtx) {
        assert paramFirstRtx != null;
        assert paramSecondRtx != null;

        boolean found = false;

        if (paramFirstRtx.getNode().getNodeKey() == paramSecondRtx.getNode().getNodeKey()
            && paramFirstRtx.getNode().equals(paramSecondRtx.getNode())) {
            final long nodeKey = paramFirstRtx.getNode().getNodeKey();

            if (paramFirstRtx.getNode().getKind() == ENodes.ELEMENT_KIND) {
                if (((ElementNode)paramFirstRtx.getNode()).getNamespaceCount() == 0
                    && ((ElementNode)paramFirstRtx.getNode()).getAttributeCount() == 0
                    && ((ElementNode)paramSecondRtx.getNode()).getAttributeCount() == 0
                    && ((ElementNode)paramSecondRtx.getNode()).getNamespaceCount() == 0) {
                    found = true;
                } else {
                    if (((ElementNode)paramFirstRtx.getNode()).getNamespaceCount() == 0) {
                        found = true;
                    } else {
                        for (int i = 0; i < ((ElementNode)paramFirstRtx.getNode()).getNamespaceCount(); i++) {
                            paramFirstRtx.moveToNamespace(i);
                            for (int j = 0; j < ((ElementNode)paramSecondRtx.getNode()).getNamespaceCount(); j++) {
                                paramSecondRtx.moveToNamespace(i);

                                if (paramFirstRtx.getNode().equals(paramSecondRtx.getNode())) {
                                    found = true;
                                    break;
                                }
                            }
                            paramFirstRtx.moveTo(nodeKey);
                            paramSecondRtx.moveTo(nodeKey);
                        }
                    }

                    if (found) {
                        for (int i = 0; i < ((ElementNode)paramFirstRtx.getNode()).getAttributeCount(); i++) {
                            paramFirstRtx.moveToAttribute(i);
                            for (int j = 0; j < ((ElementNode)paramSecondRtx.getNode()).getAttributeCount(); j++) {
                                paramSecondRtx.moveToAttribute(i);

                                if (paramFirstRtx.getNode().equals(paramSecondRtx.getNode())) {
                                    found = true;
                                    break;
                                }
                            }
                            paramFirstRtx.moveTo(nodeKey);
                            paramSecondRtx.moveTo(nodeKey);
                        }
                    }
                }
            } else {
                found = true;
            }
        }

        return found;
    }
}