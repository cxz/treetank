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

package org.treetank.saxon.wrapper;

import static org.treetank.node.IConstants.ATTRIBUTE;
import static org.treetank.node.IConstants.COMMENT;
import static org.treetank.node.IConstants.ELEMENT;
import static org.treetank.node.IConstants.NAMESPACE;
import static org.treetank.node.IConstants.PROCESSING;
import static org.treetank.node.IConstants.ROOT;
import static org.treetank.node.IConstants.TEXT;

import javax.xml.namespace.QName;

import net.sf.saxon.Configuration;
import net.sf.saxon.event.Receiver;
import net.sf.saxon.om.Axis;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.NamespaceBinding;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StandardNames;
import net.sf.saxon.pattern.AnyNodeTest;
import net.sf.saxon.pattern.NameTest;
import net.sf.saxon.pattern.NodeTest;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.iter.AxisIterator;
import net.sf.saxon.tree.iter.EmptyAxisIterator;
import net.sf.saxon.tree.iter.SingletonIterator;
import net.sf.saxon.tree.util.FastStringBuffer;
import net.sf.saxon.tree.util.NamespaceIterator;
import net.sf.saxon.tree.util.Navigator;
import net.sf.saxon.tree.wrapper.SiblingCountingNode;
import net.sf.saxon.tree.wrapper.VirtualNode;
import net.sf.saxon.type.AnySimpleType;
import net.sf.saxon.type.AnyType;
import net.sf.saxon.type.SchemaType;
import net.sf.saxon.type.SimpleType;
import net.sf.saxon.type.Type;
import net.sf.saxon.value.AtomicValue;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.UntypedAtomicValue;
import net.sf.saxon.value.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treetank.access.NodeReadTrx;
import org.treetank.api.INodeReadTrx;
import org.treetank.axis.AbsAxis;
import org.treetank.axis.AncestorAxis;
import org.treetank.axis.AttributeAxis;
import org.treetank.axis.ChildAxis;
import org.treetank.axis.DescendantAxis;
import org.treetank.axis.FilterAxis;
import org.treetank.axis.FollowingAxis;
import org.treetank.axis.FollowingSiblingAxis;
import org.treetank.axis.ParentAxis;
import org.treetank.axis.PrecedingAxis;
import org.treetank.axis.PrecedingSiblingAxis;
import org.treetank.axis.filter.TextFilter;
import org.treetank.exception.TTException;
import org.treetank.node.ElementNode;
import org.treetank.node.IConstants;
import org.treetank.node.interfaces.INode;
import org.treetank.node.interfaces.IStructNode;

/**
 * <h1>NodeWrapper</h1>
 * 
 * <p>
 * Wraps a Treetank node into Saxon's internal representation of a node. It therefore implements Saxon's core
 * interface NodeInfo as well as two others:
 * </p>
 * 
 * <dl>
 * <dt>NodeInfo</dt>
 * <dd>The NodeInfo interface represents a node in Saxon's implementation of the XPath 2.0 data model.</dd>
 * <dt>VirtualNode</dt>
 * <dd>This interface is implemented by NodeInfo implementations that act as wrappers on some underlying tree.
 * It provides a method to access the real node underlying the virtual node, for use by applications that need
 * to drill down to the underlying data.</dd>
 * <dt>SiblingCountingNode</dt>
 * <dd>Interface that extends NodeInfo by providing a method to get the position of a node relative to its
 * siblings.</dd>
 * </dl>
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public class NodeWrapper implements NodeInfo, VirtualNode, SiblingCountingNode {

    /** Kind of current node. */
    protected transient final int nodeKind;

    /** Document wrapper. */
    protected transient DocumentWrapper mDocWrapper;

    /**
     * Log wrapper for better output.
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(NodeWrapper.class);

    /** Key of node. */
    protected transient final long mKey;

    /** Treetank node. */
    protected transient final INode node;

    /** QName of current node. */
    protected transient final QName qName;

    /**
     * A node in the XML parse tree. Wrap a Treetank node.
     * 
     * @param pDocWrapper
     *            Document wrapper
     * @param pNodekeyToStart
     *            NodeKey to move to.
     * @throws TTException
     */
    protected NodeWrapper(final DocumentWrapper pDocWrapper, final long pNodekeyToStart) throws TTException {

        this.mDocWrapper = pDocWrapper;

        final INodeReadTrx rtx =
            new NodeReadTrx(mDocWrapper.mSession.beginBucketRtx(mDocWrapper.mSession.getMostRecentVersion()));
        rtx.moveTo(pNodekeyToStart);
        this.nodeKind = rtx.getNode().getKind();
        this.mKey = rtx.getNode().getDataKey();
        this.node = rtx.getNode();

        if (nodeKind == ELEMENT || nodeKind == ATTRIBUTE) {
            this.qName = rtx.getQNameOfCurrentNode();
        } else {
            this.qName = null;
        }
        rtx.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Value atomize() throws XPathException {
        Value value = null;

        switch (nodeKind) {
        case COMMENT:
        case PROCESSING:
            // The content as an instance of the xs:string data type.
            value = new StringValue(getStringValueCS());
            break;
        default:
            // The content as an instance of the xdt:untypedAtomic data type.
            value = new UntypedAtomicValue(getStringValueCS());
        }

        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareOrder(final NodeInfo node) {
        int retVal;

        // Should be in the same document.
        if (getDocumentNumber() != node.getDocumentNumber()) {
            retVal = -2;
        }

        // FIXME fix the key order, this can result in errors related to
        // different version of a file.
        else if (((NodeWrapper)node).mKey > mKey) {
            retVal = -1;
        } else if (((NodeWrapper)node).mKey == mKey) {
            retVal = 0;
        } else {
            retVal = 1;
        }

        return retVal;
    }

    /**
     * Copy this node to a given outputter (deep copy).
     * 
     * @see net.sf.saxon.om.NodeInfo#copy(Receiver, int, int)
     */
    public void copy(final Receiver out, final int copyOption, final int locationId) throws XPathException {
        Navigator.copy(this, out, copyOption, locationId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateId(final FastStringBuffer buf) {
        buf.append(String.valueOf(mKey));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAttributeValue(final int fingerprint) {
        String attVal = null;

        final NameTest test = new NameTest(Type.ATTRIBUTE, fingerprint, getNamePool());
        final AxisIterator iterator = iterateAxis(Axis.ATTRIBUTE, test);
        final NodeInfo attribute = (NodeInfo)iterator.next();

        if (attribute != null) {
            attVal = attribute.getStringValue();
        }

        return attVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBaseURI() {
        String baseURI = null;

        NodeInfo node = this;

        while (node != null) {
            baseURI = node.getAttributeValue(StandardNames.XML_BASE);

            if (baseURI == null) {
                // Search for baseURI in parent node (xml:base="").
                node = node.getParent();
            } else {
                break;
            }
        }

        if (baseURI == null) {
            baseURI = mDocWrapper.getBaseURI();
        }

        return baseURI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnNumber() {
        throw new UnsupportedOperationException("Not supported by TreeTank.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration getConfiguration() {
        return mDocWrapper.getConfiguration();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayName() {
        String dName = "";

        switch (nodeKind) {
        case ELEMENT:
        case ATTRIBUTE:
            String prefix = getPrefix();
            if (prefix.isEmpty()) {
                dName = getLocalPart();
            } else {
                dName = prefix + ":" + getLocalPart();
            }
            break;
        case NAMESPACE:
        case PROCESSING:
            dName = getLocalPart();
            break;
        default:
            // Do nothing.
        }

        return dName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getDocumentNumber() {
        return mDocWrapper.getBaseURI().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DocumentInfo getDocumentRoot() {
        return mDocWrapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFingerprint() {
        int retVal;

        final int nameCount = getNameCode();
        if (nameCount == -1) {
            retVal = -1;
        } else {
            retVal = nameCount & 0xfffff;
        }

        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLineNumber() {
        throw new UnsupportedOperationException("Not supported by TreeTank.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocalPart() {
        String localPart = "";

        switch (nodeKind) {
        case ELEMENT:
        case ATTRIBUTE:
            localPart = qName.getLocalPart();
            break;
        default:
            // Do nothing.
        }

        return localPart;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNameCode() {
        int nameCode = -1;

        switch (nodeKind) {
        case ELEMENT:
        case ATTRIBUTE:
        case PROCESSING:
            // case NAMESPACE_KIND:
            nameCode = mDocWrapper.getNamePool().allocate(getPrefix(), getURI(), getLocalPart());
            break;
        default:
            // text, comment, document and namespace nodes.
        }

        return nameCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NamePool getNamePool() {
        return mDocWrapper.getNamePool();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNodeKind() {
        return nodeKind;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeInfo getParent() {
        try {
            NodeInfo parent = null;
            final INodeReadTrx rtx = createRtxAndMove();
            if (rtx.getNode().hasParent()) {
                // Parent transaction.
                parent = new NodeWrapper(mDocWrapper, rtx.getNode().getParentKey());
            }
            rtx.close();
            return parent;
        } catch (final TTException exc) {
            LOGGER.error(exc.toString());
            return null;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPrefix() {
        String prefix = "";

        switch (nodeKind) {
        case ELEMENT:
        case ATTRIBUTE:
            prefix = qName.getPrefix();
            break;
        default:
            /*
             * Change nothing, return empty String in case of a node which isn't
             * an element or attribute.
             */
        }

        return prefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeInfo getRoot() {
        return (NodeInfo)mDocWrapper;
    }

    /**
     * getStringValue() just calls getStringValueCS().
     * 
     */
    @Override
    public final String getStringValue() {
        return getStringValueCS().toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final CharSequence getStringValueCS() {
        String mValue = "";
        try {
            final INodeReadTrx rtx = createRtxAndMove();

            switch (nodeKind) {
            case ROOT:
            case ELEMENT:
                mValue = expandString();
                break;
            case ATTRIBUTE:
                mValue = emptyIfNull(rtx.getValueOfCurrentNode());
                break;
            case TEXT:
                mValue = rtx.getValueOfCurrentNode();
                break;
            case COMMENT:
            case PROCESSING:
                mValue = emptyIfNull(rtx.getValueOfCurrentNode());
                break;
            default:
                mValue = "";
            }
        } catch (final TTException exc) {
            LOGGER.error(exc.toString());
        }

        return mValue;
    }

    /**
     * Filter text nodes.
     * 
     * @return concatenated String of text node values.
     */
    private String expandString() {
        final FastStringBuffer fsb = new FastStringBuffer(FastStringBuffer.SMALL);
        try {
            final INodeReadTrx rtx = createRtxAndMove();
            final FilterAxis axis = new FilterAxis(new DescendantAxis(rtx), rtx, new TextFilter(rtx));

            while (axis.hasNext()) {
                if (rtx.getNode().getKind() == TEXT) {
                    fsb.append(rtx.getValueOfCurrentNode());
                }
                axis.next();
            }
            rtx.close();
        } catch (final TTException exc) {
            LOGGER.error(exc.toString());
        }
        return fsb.condense().toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSystemId() {
        return mDocWrapper.getBaseURI();
    }

    /**
     * Get the type annotation.
     * 
     * @return UNTYPED or UNTYPED_ATOMIC.
     */
    public int getTypeAnnotation() {
        int type = 0;
        if (nodeKind == ATTRIBUTE) {
            type = StandardNames.XS_UNTYPED_ATOMIC;
        } else {
            type = StandardNames.XS_UNTYPED;
        }
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getURI() {
        String URI = "";

        switch (nodeKind) {
        case ELEMENT:
        case ATTRIBUTE:
        case NAMESPACE:
            if (!"".equals(qName.getPrefix())) {
                URI = qName.getNamespaceURI();
            }
            break;
        default:
            // Do nothing.
        }

        // Return URI or empty string.
        return URI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildNodes() {
        boolean hasChildNodes = false;
        try {
            final INodeReadTrx rtx = createRtxAndMove();
            if (((IStructNode)rtx.getNode()).getChildCount() > 0) {
                hasChildNodes = true;
            }
            rtx.close();
        } catch (final TTException exc) {
            LOGGER.error(exc.toString());
        }
        return hasChildNodes;
    }

    /**
     * Not supported.
     */
    @Override
    public boolean isId() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isIdref() {
        throw new UnsupportedOperationException("Currently not supported by Treetank!");
        // return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNilled() {
        throw new UnsupportedOperationException("Currently not supported by Treetank!");
        // return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSameNodeInfo(final NodeInfo other) {
        boolean retVal;

        if (!(other instanceof NodeInfo)) {
            retVal = false;
        } else {
            retVal = ((NodeWrapper)other).mKey == mKey;
        }

        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AxisIterator iterateAxis(final byte axisNumber) {
        return iterateAxis(axisNumber, AnyNodeTest.getInstance());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AxisIterator iterateAxis(final byte axisNumber, final NodeTest nodeTest) {
        AxisIterator returnVal = null;
        try {
            final INodeReadTrx rtx = createRtxAndMove();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("NODE TEST: " + nodeTest);
            }

            switch (axisNumber) {
            case Axis.ANCESTOR:
                if (getNodeKind() == IConstants.ROOT) {
                    returnVal = EmptyAxisIterator.emptyAxisIterator();
                } else {
                    returnVal =
                        new Navigator.AxisFilter(new SaxonEnumeration(new AncestorAxis(rtx)), nodeTest);
                }
                break;
            case Axis.ANCESTOR_OR_SELF:
                if (getNodeKind() == IConstants.ROOT) {
                    returnVal = Navigator.filteredSingleton(this, nodeTest);
                } else {
                    returnVal =
                        new Navigator.AxisFilter(new SaxonEnumeration(new AncestorAxis(rtx, true)), nodeTest);
                }
                break;
            case Axis.ATTRIBUTE:
                if (getNodeKind() != IConstants.ELEMENT) {
                    returnVal = EmptyAxisIterator.emptyAxisIterator();
                } else {
                    returnVal =
                        new Navigator.AxisFilter(new SaxonEnumeration(new AttributeAxis(rtx)), nodeTest);
                }
                break;
            case Axis.CHILD:
                if (hasChildNodes()) {
                    returnVal = new Navigator.AxisFilter(new SaxonEnumeration(new ChildAxis(rtx)), nodeTest);
                } else {
                    returnVal = EmptyAxisIterator.emptyAxisIterator();
                }
                break;
            case Axis.DESCENDANT:
                if (hasChildNodes()) {
                    returnVal =
                        new Navigator.AxisFilter(new SaxonEnumeration(new DescendantAxis(rtx)), nodeTest);
                } else {
                    returnVal = EmptyAxisIterator.emptyAxisIterator();
                }
                break;
            case Axis.DESCENDANT_OR_SELF:
                returnVal =
                    new Navigator.AxisFilter(new SaxonEnumeration(new DescendantAxis(rtx, true)), nodeTest);
                break;
            case Axis.FOLLOWING:
                returnVal = new Navigator.AxisFilter(new SaxonEnumeration(new FollowingAxis(rtx)), nodeTest);
                break;
            case Axis.FOLLOWING_SIBLING:
                switch (nodeKind) {
                case ROOT:
                case ATTRIBUTE:
                case NAMESPACE:
                    returnVal = EmptyAxisIterator.emptyAxisIterator();
                    break;
                default:
                    returnVal =
                        new Navigator.AxisFilter(new SaxonEnumeration(new FollowingSiblingAxis(rtx)),
                            nodeTest);
                    break;
                }

            case Axis.NAMESPACE:
                // if (getNodeKind() != IConstants.ELEMENT) {
                returnVal = EmptyAxisIterator.emptyAxisIterator();
                // } else {
                // returnVal = NamespaceIterator.iterateNamespaces(this);
                // }
                break;
            case Axis.PARENT:
                if (rtx.getNode().getParentKey() == IConstants.ROOT) {
                    returnVal = EmptyAxisIterator.emptyAxisIterator();
                } else {
                    returnVal = new Navigator.AxisFilter(new SaxonEnumeration(new ParentAxis(rtx)), nodeTest);
                }
            case Axis.PRECEDING:
                returnVal = new Navigator.AxisFilter(new SaxonEnumeration(new PrecedingAxis(rtx)), nodeTest);
                break;
            case Axis.PRECEDING_SIBLING:
                switch (nodeKind) {
                case ROOT:
                case ATTRIBUTE:
                case NAMESPACE:
                    returnVal = EmptyAxisIterator.emptyAxisIterator();
                    break;
                default:
                    returnVal =
                        new Navigator.AxisFilter(new SaxonEnumeration(new PrecedingSiblingAxis(rtx)),
                            nodeTest);
                    break;
                }

            case Axis.SELF:
                returnVal = Navigator.filteredSingleton(this, nodeTest);
                break;

            case Axis.PRECEDING_OR_ANCESTOR:
                returnVal =
                    new Navigator.AxisFilter(new Navigator.PrecedingEnumeration(this, true), nodeTest);
                break;
            default:
                throw new IllegalArgumentException("Unknown axis number " + axisNumber);
            }
        } catch (final TTException exc) {
            LOGGER.error(exc.toString());
        }
        return returnVal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSystemId(final String systemId) {
        mDocWrapper.setBaseURI(systemId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceIterator getTypedValue() throws XPathException {
        return SingletonIterator.makeIterator((AtomicValue)atomize());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getRealNode() {
        return getUnderlyingNode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getUnderlyingNode() {
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSiblingPosition() {
        int index = 0;
        try {
            final INodeReadTrx rtx = createRtxAndMove();
            while (((IStructNode)rtx.getNode()).hasLeftSibling()) {
                rtx.moveTo(((IStructNode)rtx.getNode()).getLeftSiblingKey());
                index++;
            }
            rtx.close();
        } catch (final TTException exc) {
            LOGGER.error(exc.toString());
        }
        return index;
    }

    private final INodeReadTrx createRtxAndMove() throws TTException {
        final INodeReadTrx rtx =
            new NodeReadTrx(mDocWrapper.mSession.beginBucketRtx(mDocWrapper.mSession.getMostRecentVersion()));
        rtx.moveTo(mKey);
        return rtx;
    }

    /**
     * Treat a node value of null as an empty string.
     * 
     * @param s
     *            The node value.
     * @return a zero-length string if s is null, otherwise s.
     */
    private static String emptyIfNull(final String s) {
        return (s == null ? "" : s);
    }

    /**
     * <h1>SaxonEnumeration</h1>
     * 
     * <p>
     * Saxon adaptor for axis iterations.
     * </p>
     */
    public final class SaxonEnumeration extends Navigator.BaseEnumeration {

        /** Treetank axis iterator. */
        private final AbsAxis mAxis;

        /**
         * Constructor.
         * 
         * @param paramAxis
         *            TreeTank axis iterator.
         */
        public SaxonEnumeration(final AbsAxis paramAxis) {
            mAxis = paramAxis;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void advance() {
            if (mAxis.hasNext()) {
                final long nextKey = mAxis.next();
                try {
                    current = new NodeWrapper(mDocWrapper, nextKey);
                } catch (final TTException exc) {
                    current = null;
                }
            } else {
                try {
                    mAxis.close();
                } catch (TTException exc) {
                    LOGGER.error(exc.toString());
                }
                current = null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public AxisIterator<NodeInfo> getAnother() {
            return new SaxonEnumeration(mAxis);
        }
    }

    @Override
    public String getAttributeValue(String arg0, String arg1) {
        if (nodeKind == ELEMENT) {
            final int count = ((ElementNode)node).getAttributeCount();

            if (count == 0) {
                return null;
            } else {

                try {
                    final INodeReadTrx rtx = createRtxAndMove();
                    for (int i = 0; i < count; i++) {
                        rtx.moveTo(((ElementNode)node).getAttributeKey(i));
                        QName name = rtx.getQNameOfCurrentNode();
                        if (name.getNamespaceURI().equals(arg0) && name.getLocalPart().equals(arg1)) {
                            rtx.close();
                            return rtx.getValueOfCurrentNode();
                        }
                    }
                    rtx.close();
                } catch (final TTException exc) {
                    LOGGER.error(exc.toString());
                }
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public NamespaceBinding[] getDeclaredNamespaces(NamespaceBinding[] arg0) {

        if (nodeKind == ELEMENT) {
            final int count = ((ElementNode)node).getNamespaceCount();

            if (count == 0) {
                return new NamespaceBinding[0];
            } else {
                final NamespaceBinding[] returnVal = new NamespaceBinding[count];

                try {
                    final INodeReadTrx rtx = createRtxAndMove();
                    for (int i = 0; i < count; i++) {
                        rtx.moveTo(((ElementNode)node).getNamespaceKey(i));
                        final String prefix = getPrefix();
                        final String uri = getURI();
                        rtx.moveTo(mKey);
                        returnVal[i] = new NamespaceBinding(prefix, uri);
                    }
                    rtx.close();
                } catch (final TTException exc) {
                    LOGGER.error(exc.toString());
                }
                if (arg0 != null && returnVal.length < arg0.length) {
                    System.arraycopy(returnVal, 0, arg0, 0, returnVal.length);
                }
                return returnVal;
            }
        } else {
            return null;
        }
    }

    @Override
    public SchemaType getSchemaType() {
        return AnySimpleType.getInstance();
    }

}
