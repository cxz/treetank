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

package org.treetank.service.xml.xpath.functions;

import java.util.List;

import org.treetank.api.INodeReadTrx;
import org.treetank.axis.AbsAxis;
import org.treetank.axis.DescendantAxis;
import org.treetank.axis.FilterAxis;
import org.treetank.axis.filter.TextFilter;
import org.treetank.exception.TTXPathException;
import org.treetank.node.IConstants;
import org.treetank.node.interfaces.IValNode;
import org.treetank.utils.TypedValue;

/**
 * <h1>FNString</h1>
 * <p>
 * IAxis that represents the function fn:count specified in <a href="http://www.w3.org/TR/xquery-operators/">
 * XQuery 1.0 and XPath 2.0 Functions and Operators</a>.
 * </p>
 * <p>
 * The function returns the string value of the current node or the argument nodes.
 * </p>
 */
public class FNString extends AbsFunction {

    /** Readtrans, stored over here since needed for internal axis. */
    private final INodeReadTrx pRtx;

    /**
     * Constructor. Initializes internal state and do a statical analysis
     * concerning the function's arguments.
     * 
     * @param rtx
     *            Transaction to operate on
     * @param args
     *            List of function arguments
     * @param min
     *            min number of allowed function arguments
     * @param max
     *            max number of allowed function arguments
     * @param returnType
     *            the type that the function's result will have
     * @throws TTXPathException
     *             if function check fails
     */
    public FNString(final INodeReadTrx rtx, final List<AbsAxis> args, final int min, final int max,
        final int returnType) throws TTXPathException {

        super(rtx, args, min, max, returnType);
        pRtx = rtx;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected byte[] computeResult() {

        String value;

        if (getArgs().size() == 0) {
            value = getStrValue();
        } else {
            final AbsAxis axis = getArgs().get(0);
            final StringBuilder val = new StringBuilder();
            while (axis.hasNext()) {
                String nodeValue = getStrValue();
                if (!nodeValue.equals("")) {
                    if (val.length() > 0) {
                        val.append(" ");
                    }
                    val.append(nodeValue);
                }
            }
            value = val.toString();
        }

        return TypedValue.getBytes(value);

    }

    /**
     * Returns the string value of an item. If the item is the empty sequence,
     * the zero-length string is returned. If the item is a node, the function
     * returns the string-value of the node, as obtained using the
     * dm:string-value accessor defined in the <a
     * href="http://www.w3.org/TR/xpath-datamodel/#dm-string-value">Section 5.13
     * string-value AccessorDM</a>. If the item is an atomic value, then the
     * function returns the same string as is returned by the expression " $arg
     * cast as xs:string " (see 17 Casting).
     * 
     * @return the context item's string value.
     */
    private String getStrValue() {

        final StringBuilder value = new StringBuilder();

        if (getNode().getDataKey() >= 0) { // is node
            if (getNode().getKind() == IConstants.ATTRIBUTE || getNode().getKind() == IConstants.TEXT) {
                value.append(new String(((IValNode)getNode()).getRawValue()));
            } else if (getNode().getKind() == IConstants.ROOT || getNode().getKind() == IConstants.ELEMENT) {
                final AbsAxis axis = new FilterAxis(new DescendantAxis(pRtx), pRtx, new TextFilter(pRtx));
                while (axis.hasNext()) {
                    if (value.length() > 0) {
                        value.append(" ");
                    }
                    value.append(new String(((IValNode)getNode()).getRawValue()));

                }
            } else {
                throw new IllegalStateException();
            }

        } else {
            value.append(new String(((IValNode)getNode()).getRawValue()));
        }

        return value.toString();
    }
}
