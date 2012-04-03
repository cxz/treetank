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

package org.treetank.access;

import javax.xml.namespace.QName;

import org.treetank.api.INodeReadTransaction;
import org.treetank.api.IPageReadTransaction;
import org.treetank.exception.AbsTTException;
import org.treetank.exception.TTIOException;
import org.treetank.node.ENode;
import org.treetank.node.ElementNode;
import org.treetank.node.interfaces.INameNode;
import org.treetank.node.interfaces.INode;
import org.treetank.node.interfaces.IValNode;

/**
 * <h1>NodeReadTransaction</h1>
 * 
 * <p>
 * Read-only transaction wiht single-threaded cursor semantics. Each read-only transaction works on a given
 * revision key.
 * </p>
 */
public class NodeReadTransaction implements INodeReadTransaction {

    public static final long ROOT_NODE = 0;

    public static final long NULL_NODE = -1;

    /** ID of transaction. */
    private final long mId;

    /** Session state this write transaction is bound to. */
    protected final Session mSession;

    /** State of transaction including all cached stuff. */
    protected IPageReadTransaction mPageReadTransaction;

    /** Strong reference to currently selected node. */
    private INode mCurrentNode;

    /** Tracks whether the transaction is closed. */
    private boolean mClosed;

    /**
     * Constructor.
     * 
     * @param paramSession
     *            state of the session
     * @param paramTransactionID
     *            ID of transaction.
     * 
     * @param paramTransactionState
     *            Transaction state to work with.
     * @throws TTIOException
     *             if something odd happens within the creation process.
     */
    protected NodeReadTransaction(final Session paramSession, final long paramTransactionID,
        final IPageReadTransaction paramTransactionState) throws TTIOException {
        mSession = paramSession;
        mId = paramTransactionID;
        mPageReadTransaction = paramTransactionState;
        mCurrentNode = mPageReadTransaction.getNode(ROOT_NODE);
        mClosed = false;
    }

    /**
     * {@inheritDoc}
     */
    public final long getTransactionID() {
        return mId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final long getRevisionNumber() throws TTIOException {
        assertNotClosed();
        return mPageReadTransaction.getActualRevisionRootPage().getRevision();
    }

    /**
     * {@inheritDoc}
     * 
     * @throws TTIOException
     */
    @Override
    public final boolean moveTo(final long paramNodeKey) {
        assertNotClosed();
        if (paramNodeKey == NULL_NODE) {
            return false;
        } else {
            // Remember old node and fetch new one.
            final INode oldNode = mCurrentNode;
            try {
                mCurrentNode = mPageReadTransaction.getNode(paramNodeKey);
            } catch (final TTIOException exc) {
                mCurrentNode = null;
            }

            if (mCurrentNode == null) {
                mCurrentNode = oldNode;
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean moveToAttribute(final int mIndex) {
        if (mCurrentNode.getKind() == ENode.ELEMENT_KIND) {
            return moveTo(((ElementNode)mCurrentNode).getAttributeKey(mIndex));
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean moveToNamespace(final int mIndex) {
        if (mCurrentNode.getKind() == ENode.ELEMENT_KIND) {
            return moveTo(((ElementNode)mCurrentNode).getNamespaceKey(mIndex));
        } else {
            return false;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getValueOfCurrentNode() {
        assertNotClosed();
        String returnVal;
        if (mCurrentNode instanceof IValNode) {
            returnVal = new String(((IValNode)mCurrentNode).getRawValue());
        } else {
            returnVal = "";
        }
        return returnVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final QName getQNameOfCurrentNode() {
        assertNotClosed();
        String name = "";
        String uri = "";
        if (mCurrentNode instanceof INameNode) {
            name = mPageReadTransaction.getName(((INameNode)mCurrentNode).getNameKey());
            uri = mPageReadTransaction.getName(((INameNode)mCurrentNode).getURIKey());
        }
        return buildQName(uri, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getTypeOfCurrentNode() {
        assertNotClosed();
        return mPageReadTransaction.getName(mCurrentNode.getTypeKey());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String nameForKey(final int mKey) {
        assertNotClosed();
        return mPageReadTransaction.getName(mKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] rawNameForKey(final int paramKey) {
        assertNotClosed();
        return mPageReadTransaction.getRawName(paramKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws AbsTTException {
        if (!mClosed) {
            // Close own state.
            mPageReadTransaction.close();

            // Callback on session to make sure everything is cleaned up.
            mSession.closeReadTransaction(mId);

            // Immediately release all references.
            mPageReadTransaction = null;
            mCurrentNode = null;

            mClosed = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        assertNotClosed();
        final StringBuilder builder = new StringBuilder();
        if (getNode().getKind() == ENode.ATTRIBUTE_KIND || getNode().getKind() == ENode.ELEMENT_KIND) {
            builder.append("Name of Node: ");
            builder.append(getQNameOfCurrentNode().toString());
            builder.append("\n");
        }
        if (getNode().getKind() == ENode.ATTRIBUTE_KIND || getNode().getKind() == ENode.TEXT_KIND) {
            builder.append("Value of Node: ");
            builder.append(getValueOfCurrentNode());
            builder.append("\n");
        }
        if (getNode().getKind() == ENode.ROOT_KIND) {
            builder.append("Node is DocumentRoot");
            builder.append("\n");
        }
        builder.append(getNode().toString());

        return builder.toString();
    }

    /**
     * Set state to closed.
     */
    protected final void setClosed() {
        mClosed = true;
    }

    /**
     * Is the transaction closed?
     * 
     * @return True if the transaction was closed.
     */
    public final boolean isClosed() {
        return mClosed;
    }

    /**
     * Make sure that the session is not yet closed when calling this method.
     */
    protected final void assertNotClosed() {
        if (mClosed) {
            throw new IllegalStateException("Transaction is already closed.");
        }
    }

    /**
     * Replace the state of the transaction.
     * 
     * @param paramTransactionState
     *            State of transaction.
     */
    protected final void setPageTransaction(final IPageReadTransaction paramTransactionState) {
        mPageReadTransaction = paramTransactionState;
    }

    /**
     * Getter for superclasses.
     * 
     * @return The current node.
     */
    protected final INode getCurrentNode() {
        return mCurrentNode;
    }

    /**
     * Setter for superclasses.
     * 
     * @param paramCurrentNode
     *            The current node to set.
     */
    protected final void setCurrentNode(final INode paramCurrentNode) {
        mCurrentNode = paramCurrentNode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final INode getNode() {
        return mCurrentNode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final long getMaxNodeKey() throws TTIOException {
        return mPageReadTransaction.getActualRevisionRootPage().getMaxNodeKey();
    }

    /**
     * Building QName out of uri and name. The name can have the prefix denoted
     * with ":";
     * 
     * @param paramUri
     *            the namespaceuri
     * @param paramName
     *            the name including a possible prefix
     * @return the QName obj
     */
    public static final QName buildQName(final String paramUri, final String paramName) {
        QName qname;
        if (paramName.contains(":")) {
            qname = new QName(paramUri, paramName.split(":")[1], paramName.split(":")[0]);
        } else {
            qname = new QName(paramUri, paramName);
        }
        return qname;
    }
}
