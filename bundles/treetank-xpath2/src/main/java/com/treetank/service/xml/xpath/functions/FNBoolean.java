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

package com.treetank.service.xml.xpath.functions;

import java.util.List;

import com.treetank.api.IAxis;
import com.treetank.api.IReadTransaction;
import com.treetank.service.xml.xpath.functions.XPathError.ErrorType;
import com.treetank.service.xml.xpath.types.Type;
import com.treetank.utils.TypedValue;

/**
 * <h1>FNBooleean</h1>
 * <p>
 * IAxis that represents the function fn:boolean specified in <a
 * href="http://www.w3.org/TR/xquery-operators/"> XQuery 1.0 and XPath 2.0 Functions and Operators</a>.
 * </p>
 * <p>
 * The function returns the effective boolean value of given arguments.
 * </p>
 */
public class FNBoolean extends AbstractFunction {

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
     */
    public FNBoolean(final IReadTransaction rtx, final List<IAxis> args, final int min, final int max,
        final int returnType) {

        super(rtx, args, min, max, returnType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected synchronized byte[] computeResult() {

        final IAxis axis = getArgs().get(0);
        boolean value = false;

        if (axis.hasNext()) {

            final IReadTransaction rtx = axis.getTransaction();

            if (rtx.getNode().getNodeKey() >= 0) { // first item is a node ->
                // true
                value = true;
            } else {

                final Type type = Type.getType(rtx.getNode().getTypeKey());

                if (type.derivesFrom(Type.BOOLEAN)) {
                    value = Boolean.parseBoolean(TypedValue.parseString(rtx.getNode().getRawValue()));
                    // value = TypedValue.parseBoolean(rtx.getRawValue());
                } else if (type.derivesFrom(Type.STRING) || type.derivesFrom(Type.ANY_URI)
                    || type.derivesFrom(Type.UNTYPED_ATOMIC)) {
                    // if length = 0 -> false
                    value = (TypedValue.parseString(rtx.getNode().getRawValue()).length() > 0);
                } else if (type.isNumericType()) {
                    final double dValue = TypedValue.parseDouble(rtx.getNode().getRawValue());
                    value = !(Double.isNaN(dValue) || dValue == 0.0d);
                } else {
                    // for all other types throw error FORG0006
                    throw new XPathError(ErrorType.FORG0006);
                }

                // if is not a singleton
                if (axis.hasNext()) {
                    throw new XPathError(ErrorType.FORG0006);
                }
            }

        } else {
            // expression is an empty sequence -> false
            value = false;
        }

        return TypedValue.getBytes(Boolean.toString(value));

    }

}
