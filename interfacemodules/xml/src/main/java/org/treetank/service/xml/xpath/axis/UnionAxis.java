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

package org.treetank.service.xml.xpath.axis;

import org.treetank.api.INodeReadTrx;
import org.treetank.axis.AbsAxis;
import org.treetank.service.xml.xpath.XPathError;
import org.treetank.service.xml.xpath.XPathError.ErrorType;

/**
 * <h1>UnionAxis</h1>
 * <p>
 * Returns an union of two operands. This axis takes two node sequences as operands and returns a sequence
 * containing all the items that occur in either of the operands. A union of two sequences may lead to a
 * sequence containing duplicates. These duplicates can be removed by wrapping the UnionAxis with a
 * DupFilterAxis. The resulting sequence may also be out of document order.
 * </p>
 */
public class UnionAxis extends AbsAxis {

    /** First operand sequence. */
    private final AbsAxis mOp1;

    /** Second operand sequence. */
    private final AbsAxis mOp2;

    /**
     * Constructor. Initializes the internal state.
     * 
     * @param rtx
     *            Exclusive (immutable) trx to iterate with.
     * @param mOperand1
     *            First operand
     * @param mOperand2
     *            Second operand
     */
    public UnionAxis(final INodeReadTrx rtx, final AbsAxis mOperand1, final AbsAxis mOperand2) {

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
     */
    @Override
    public boolean hasNext() {

        // first return all values of the first operand
        while (mOp1.hasNext()) {
            mOp1.next();

            if (getNode().getDataKey() < 0) { // only nodes are
                // allowed
                throw new XPathError(ErrorType.XPTY0004);
            }
            return true;
        }

        // then all values of the second operand.
        while (mOp2.hasNext()) {
            mOp2.next();

            if (getNode().getDataKey() < 0) { // only nodes are
                // allowed
                throw new XPathError(ErrorType.XPTY0004);
            }
            return true;
        }

        return false;
    }

}
