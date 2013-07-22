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

package org.treetank.service.xml.xpath.filter;

import org.treetank.api.INodeReadTrx;
import org.treetank.axis.AbsAxis;
import org.treetank.node.interfaces.IValNode;
import org.treetank.utils.NamePageHash;

/**
 * <h1>PredicateFilterAxis</h1>
 * <p>
 * The PredicateAxis evaluates a predicate (in the form of an axis) and returns true, if the predicates has a
 * value (axis.hasNext == true) and this value if not the boolean value false. Otherwise false is returned.
 * Since a predicate is a kind of filter, the transaction that has been altered by means of the predicate's
 * evaluation has to be reset to the key that it was set to before the evaluation.
 * </p>
 */
public class PredicateFilterAxis extends AbsAxis {

    private boolean mIsFirst;

    private final AbsAxis mPredicate;

    /**
     * Constructor. Initializes the internal state.
     * 
     * @param rtx
     *            Exclusive (immutable) trx to iterate with.
     * @param predicate
     *            predicate expression
     */
    public PredicateFilterAxis(final INodeReadTrx rtx, final AbsAxis predicate) {

        super(rtx);
        mIsFirst = true;
        mPredicate = predicate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void reset(final long mNodeKey) {

        super.reset(mNodeKey);
        if (mPredicate != null) {
            mPredicate.reset(mNodeKey);
        }
        mIsFirst = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean hasNext() {

        resetToLastKey();

        // a predicate has to evaluate to true only once.
        if (mIsFirst) {
            mIsFirst = false;

            if (mPredicate.hasNext()) {

                if (isBooleanFalse()) {
                    resetToStartKey();
                    return false;
                }

                // reset is needed, because a predicate works more like a
                // filter. It
                // does
                // not change the current transaction.
                resetToLastKey();
                return true;
            }

        }

        resetToStartKey();
        return false;

    }

    /**
     * Tests whether current Item is an atomic value with boolean value "false".
     * 
     * @return true, if Item is boolean typed atomic value with type "false".
     */
    private boolean isBooleanFalse() {

        if (getNode().getDataKey() >= 0) {
            return false;
        } else { // is AtomicValue
            if (getNode().getTypeKey() == NamePageHash.generateHashForString("xs:boolean")) {
                // atomic value of type boolean
                // return true, if atomic values's value is false
                return !(Boolean.parseBoolean(new String(((IValNode)getNode()).getRawValue())));

            } else {
                return false;
            }
        }

    }
}
