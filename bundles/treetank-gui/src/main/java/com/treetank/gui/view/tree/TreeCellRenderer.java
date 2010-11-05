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
package com.treetank.gui.view.tree;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.xml.namespace.QName;

import com.treetank.api.IItem;
import com.treetank.api.IReadTransaction;
import com.treetank.gui.ReadDB;
import com.treetank.node.ElementNode;

/**
 * <h1TreetankTreeCellRenderer</h1>
 * 
 * <p>
 * Customized tree cell renderer to render nodes nicely.
 * </p>
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public final class TreeCellRenderer extends DefaultTreeCellRenderer {

    /**
     * Generated UID.
     */
    private static final long serialVersionUID = -6242168246410260644L;

    /** Document root color. */
    private static final Color DOC_COLOR = new Color(128, 0, 0);
    
    /** Element color. */
    private static final Color ELEMENT_COLOR = new Color(0, 0, 128);

    /** Attribute color. */
    private static final Color ATTRIBUTE_COLOR = new Color(0, 128, 0);

    /** Treetant reading transaction {@link IReadTransaction}. */
    private final IReadTransaction mRTX;

    /** Path to file. */
    private final String mPATH;

    /**
     * Constructor.
     * 
     * @param paramReadDB
     *            {@link ReadDB}.
     */
    public TreeCellRenderer(final ReadDB paramReadDB) {
        setOpenIcon(null);
        setClosedIcon(null);
        setLeafIcon(null);
        setBackgroundNonSelectionColor(null);
        setTextSelectionColor(Color.red);

        mRTX = paramReadDB.getRtx();
        mPATH = paramReadDB.getDatabase().getFile().getName();
    }

    @Override
    public Component getTreeCellRendererComponent(final JTree paramTree, Object paramValue,
        final boolean paramSel, final boolean paramExpanded, final boolean paramLeaf, final int paramRow,
        final boolean paramHasFocus) {
        final IItem node = (IItem)paramValue;

        final long key = node.getNodeKey();

        switch (node.getKind()) {
        case ELEMENT_KIND:
            mRTX.moveTo(node.getNodeKey());
            final String prefix = mRTX.getQNameOfCurrentNode().getPrefix();
            final QName qName = mRTX.getQNameOfCurrentNode();

            if (prefix == null || prefix.equals("")) {
                final String localPart = qName.getLocalPart();

                if (((ElementNode)mRTX.getNode()).hasFirstChild()) {
                    paramValue = '<' + localPart + '>';
                } else {
                    paramValue = '<' + localPart + "/>";
                }
            } else {
                paramValue = '<' + prefix + ":" + qName.getLocalPart() + '>';
            }

            break;
        case ATTRIBUTE_KIND:
            // Move transaction to parent of the attribute node.
            mRTX.moveTo(node.getParentKey());
            final long aNodeKey = node.getNodeKey();
            for (int i = 0, attsCount = ((ElementNode)mRTX.getNode()).getAttributeCount(); 
                i < attsCount; i++) {
                mRTX.moveToAttribute(i);
                if (mRTX.getNode().equals(node)) {
                    break;
                }
                mRTX.moveTo(aNodeKey);
            }

            // Display value.
            final String attPrefix = mRTX.getQNameOfCurrentNode().getPrefix();
            final QName attQName = mRTX.getQNameOfCurrentNode();

            if (attPrefix == null || attPrefix.equals("")) {
                paramValue = '@' + attQName.getLocalPart() + "='" + mRTX.getValueOfCurrentNode() + "'";
            } else {
                paramValue =
                    '@' + attPrefix + ":" + attQName.getLocalPart() + "='" + mRTX.getValueOfCurrentNode()
                        + "'";
            }

            break;
        case NAMESPACE_KIND:
            // Move transaction to parent the namespace node.
            mRTX.moveTo(node.getParentKey());
            final long nNodeKey = node.getNodeKey();
            for (int i = 0, namespCount = ((ElementNode)mRTX.getNode()).getNamespaceCount(); 
                i < namespCount; i++) {
                mRTX.moveToNamespace(i);
                if (mRTX.getNode().equals(node)) {
                    break;
                }
                mRTX.moveTo(nNodeKey);
            }

            if (mRTX.nameForKey(mRTX.getNode().getNameKey()).length() == 0) {
                paramValue = "xmlns='" + mRTX.nameForKey(mRTX.getNode().getURIKey()) + "'";
            } else {
                paramValue =
                    "xmlns:" + mRTX.nameForKey(mRTX.getNode().getNameKey()) + "='"
                        + mRTX.nameForKey(mRTX.getNode().getURIKey()) + "'";
            }
            break;
        case TEXT_KIND:
            mRTX.moveTo(node.getNodeKey());
            paramValue = mRTX.getValueOfCurrentNode();
            break;
        case COMMENT_KIND:
            mRTX.moveTo(node.getNodeKey());
            paramValue = "<!-- " + mRTX.getValueOfCurrentNode() + " -->";
            break;
        case PROCESSING_KIND:
            mRTX.moveTo(node.getNodeKey());
            paramValue = "<? " + mRTX.getValueOfCurrentNode() + " ?>";
            break;
        case ROOT_KIND:
            paramValue = "Doc: " + mPATH;
            break;
        case WHITESPACE_KIND:
            break;
        default:
            new IllegalStateException("Node kind not known!");
        }

        paramValue = paramValue + " [" + key + "]";

        super.getTreeCellRendererComponent(paramTree, paramValue, paramSel, paramExpanded, paramLeaf,
            paramRow, paramHasFocus);
        if (!selected) {
            switch (node.getKind()) {
            case ROOT_KIND:
                setForeground(DOC_COLOR);
                break;
            case ELEMENT_KIND:
                setForeground(ELEMENT_COLOR);
                break;
            case ATTRIBUTE_KIND:
                setForeground(ATTRIBUTE_COLOR);
                break;
            default:
                // Do nothing.
            }
        }

        return this;
    }

    @Override
    public Color getBackground() {
        return null;
    }
}
