/**
 * Copyright (c) 2010, Distributed Systems Group, University of Konstanz
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED AS IS AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 */
package com.treetank.saxon.wrapper;

import javax.xml.namespace.QName;

import com.treetank.api.IDatabase;
import com.treetank.api.IWriteTransaction;
import com.treetank.axis.AbsAxis;
import com.treetank.axis.ChildAxis;
import com.treetank.exception.TTException;
import com.treetank.node.ENodes;
import com.treetank.node.ElementNode;

import net.sf.saxon.event.Builder;
import net.sf.saxon.om.MutableNodeInfo;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.NodeInfo;

/**
 * <h1>MutableNodeWrapper</h1>
 * 
 * <p>
 * Implements all methods which are needed to create a modifiable Saxon internal node. Therefore it wraps
 * Treetank's nodes into the appropriate format.
 * </p>
 * 
 * <p>
 * <strong>Currently not used.</strong> For use with XQuery Update and requires a "commercial" Saxon license.
 * Furthermore as of now not stable and doesn't support third party applications. Needs to be fully
 * implemented and tested.
 * </p>
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public class MutableNodeWrapper extends NodeWrapper implements MutableNodeInfo {

    /** Treetank write transaction. */
    private final IWriteTransaction mWTX;

    /**
     * Constructor.
     * 
     * @param database
     *            Treetank database.
     * @param wtx
     *            Treetank write transaction.
     * @throws TreetankException
     *             in case of something went wrong.
     */
    protected MutableNodeWrapper(final IDatabase database, final IWriteTransaction wtx) throws TTException {
        super(database, 0);
        mWTX = database.getSession().beginWriteTransaction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAttribute(final int nameCode, final int typeCode, final CharSequence value,
        final int properties) {
        if (mWTX.getNode().getKind() == ENodes.ELEMENT_KIND) {
            String uri = "";
            String local = "";

            for (int i = 0; i < ((ElementNode)mWTX.getNode()).getAttributeCount(); i++) {
                mWTX.moveToAttribute(i);

                NamePool pool = mDocWrapper.getNamePool();
                uri = pool.getURI(nameCode);
                local = pool.getLocalName(nameCode);

                if (uri.equals(mWTX.getQNameOfCurrentNode().getNamespaceURI())
                    && local.equals(getLocalPart())) {
                    throw new IllegalStateException("Attribute with the given name already exists!");
                }

                mWTX.moveTo(mKey);
            }

            try {
                mWTX.insertAttribute(mWTX.getQNameOfCurrentNode(), (String)value);
            } catch (TTException e) {
                LOGGER.error("Couldn't insert Attribute: " + e.getMessage(), e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNamespace(int nscode, boolean inherit) {
        final NamePool pool = mDocWrapper.getNamePool();
        final String uri = pool.getURI(nscode);
        final String prefix = pool.getPrefix(nscode);

        // Not present in name pool.
        if (uri == null || prefix == null) {
            throw new IllegalArgumentException("Namespace code is not present in the name pool!");
        }

        // Insert Namespace.
        if (mWTX.getQNameOfCurrentNode().getNamespaceURI() != uri && getPrefix() != prefix) {
            try {
                mWTX.insertNamespace(uri, prefix);

                // Add namespace to child nodes if prefix
                if (inherit) {
                    final AbsAxis axis = new ChildAxis(mWTX);
                    while (axis.hasNext()) {
                        if (getPrefix() != prefix) {
                            mWTX.insertNamespace(uri, prefix);
                        }
                        axis.next();
                    }
                }
            } catch (TTException e) {
                LOGGER.error("Insert Namespace failed: " + e.getMessage(), e);
            }
            // Already bound.
        } else if (mWTX.getQNameOfCurrentNode().getNamespaceURI() != uri && getPrefix() == prefix) {
            throw new IllegalArgumentException("An URI is already bound to this prefix!");
        }

        // Do nothing is uri and prefix already are bound.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete() {
        try {
            mWTX.remove();
        } catch (TTException e) {
            LOGGER.error("Removing current node failed: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertChildren(NodeInfo[] source, boolean atStart, boolean inherit) {
        switch (mWTX.getNode().getKind()) {
        case ROOT_KIND:
        case ELEMENT_KIND:
            boolean first = true;
            for (final NodeInfo node : source) {
                try {
                    if (first) {
                        mWTX.insertElementAsFirstChild(new QName(node.getURI(), node.getLocalPart(), node
                            .getPrefix()));
                        first = false;
                    } else {
                        mWTX.insertElementAsRightSibling(new QName(node.getURI(), node.getLocalPart(), node
                            .getPrefix()));
                    }
                } catch (TTException e) {
                    LOGGER.error("Insertion of element failed: " + e.getMessage(), e);
                }
            }

            mWTX.moveTo(mKey);
            break;
        default:
            throw new IllegalStateException("");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertSiblings(NodeInfo[] source, boolean before, boolean inherit) {
        if (before) {
            mWTX.moveToParent();

            final String uri = mWTX.getQNameOfCurrentNode().getNamespaceURI();
            final String prefix = getPrefix();

            for (final NodeInfo node : source) {
                try {
                    mWTX.insertElementAsFirstChild(new QName(node.getURI(), node.getLocalPart(), node
                        .getPrefix()));

                    if (inherit) {
                        mWTX.insertNamespace(uri, prefix);
                    }

                    mWTX.moveToParent();
                } catch (TTException e) {
                    LOGGER.error("Inserting element failed: " + e.getMessage(), e);
                }
            }

            mWTX.moveTo(mKey);
        } else {
            // Get URI and prefix of parent node.
            final long key = mWTX.getNode().getNodeKey();
            mWTX.moveToParent();
            final String uri = mWTX.getQNameOfCurrentNode().getNamespaceURI();
            final String prefix = getPrefix();
            mWTX.moveTo(key);

            for (final NodeInfo node : source) {
                try {
                    mWTX.insertElementAsRightSibling(new QName(node.getDisplayName(), node.getURI()));

                    if (inherit) {
                        mWTX.insertNamespace(uri, prefix);
                    }
                } catch (TTException e) {
                    LOGGER.error("Inserting element failed: " + e.getMessage(), e);
                }
            }
        }

        mWTX.moveTo(mKey);
    }

    @Override
    public boolean isDeleted() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Builder newBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeAttribute(final NodeInfo attribute) {
        if (mWTX.getNode().getKind() == ENodes.ELEMENT_KIND) {
            for (int i = 0, attCount = ((ElementNode)mWTX.getNode()).getAttributeCount(); i < attCount; i++) {
                mWTX.moveToAttribute(i);
                try {
                    if (mWTX.getQNameOfCurrentNode().equals(attribute.getDisplayName())) {
                        mWTX.remove();
                    }
                } catch (TTException e) {
                    LOGGER.error("Removing attribute failed: " + e.getMessage(), e);
                }
                mWTX.moveTo(mKey);
            }
        }
    }

    @Override
    public void removeTypeAnnotation() {
        // TODO Auto-generated method stub

    }

    @Override
    public void rename(final int newNameCode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void replace(final NodeInfo[] replacement, final boolean inherit) {
        // TODO Auto-generated method stub

    }

    @Override
    public void replaceStringValue(final CharSequence stringValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTypeAnnotation(final int typeCode) {
        // TODO Auto-generated method stub

    }
}