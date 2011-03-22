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

package org.treetank.service.xml.xpath.expr;

import org.treetank.api.IReadTransaction;
import org.treetank.axis.AbsAxis;
import org.treetank.exception.TTXPathException;
import org.treetank.service.xml.xpath.AtomicValue;
import org.treetank.service.xml.xpath.functions.Function;
import org.treetank.utils.TypedValue;

/**
 * <h1>Logical And Expression</h1>
 * <p>
 * The logical and expression performs a logical conjunction of the boolean values of two input sequences. If
 * a logical expression does not raise an error, its value is always one of the boolean values true or false.
 * </p>
 * <p>
 * The value of an and-expression is determined by the effective boolean values of its operands, as shown in
 * the following table:
 * <table>
 * <tr>
 * <th>AND</th>
 * <th>EBV2 = true</th>
 * <th>EBV2 = false</th>
 * <th>error in EBV2</th>
 * </tr>
 * <tr>
 * <th>EBV1 = true</th>
 * <th>true</th>
 * <th>false</th>
 * <th>error</th>
 * </tr>
 * <tr>
 * <th>EBV1 = false</th>
 * <th>false</th>
 * <th>false</th>
 * <th>false</th>
 * </tr>
 * <tr>
 * <th>error in EBV1</th>
 * <th>error</th>
 * <th>error</th>
 * <th>error</th>
 * </tr>
 * </table>
 */
public class AndExpr extends AbsExpression {

    /** First operand of the logical expression. */
    private final AbsAxis mOp1;

    /** Second operand of the logical expression. */
    private final AbsAxis mOp2;

    /**
     * Constructor. Initializes the internal state.
     * 
     * @param rtx
     *            Exclusive (immutable) transaction to iterate with.
     * @param mOperand1
     *            First operand
     * @param mOperand2
     *            Second operand
     */
    public AndExpr(final IReadTransaction rtx, final AbsAxis mOperand1, final AbsAxis mOperand2) {

        super(rtx);
        mOp1 = mOperand1;
        mOp2 = mOperand2;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset(final long mNodeKey) {

        super.reset(mNodeKey);
        if (mOp1 != null) {
            mOp1.reset(mNodeKey);
        }
        if (mOp2 != null) {
            mOp2.reset(mNodeKey);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @throws TTXPathException
     */
    @Override
    public void evaluate() throws TTXPathException {

        // first find the effective boolean values of the two operands, then
        // determine value of the and-expression and store it in an item
        final boolean result = Function.ebv(mOp1) && Function.ebv(mOp2);
        // note: the error handling is implicitly done by the fnBoolean()
        // function.

        // add result item to list and set the item as the current item
        final int mItemKey =
            getTransaction().getItemList().addItem(
                new AtomicValue(TypedValue.getBytes(Boolean.toString(result)), getTransaction().keyForName(
                    "xs:boolean")));
        getTransaction().moveTo(mItemKey);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void setTransaction(final IReadTransaction rtx) {
        super.setTransaction(rtx);
        mOp1.setTransaction(rtx);
        mOp2.setTransaction(rtx);
    }

}
