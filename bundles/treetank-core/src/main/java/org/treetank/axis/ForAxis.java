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

package org.treetank.axis;

import org.treetank.api.IReadTransaction;

/**
 * <h1>ForAxis</h1>
 * <p>
 * IAxis that handles a for expression.
 * </p>
 * <p>
 * This Axis represents only the single-variable for expression. A multiple variables for expression is
 * created by wrap for-Axes by first expanding the expression to a set of nested for expressions, each of
 * which uses only one variable. For example, the expression for $x in X, $y in Y return $x + $y is expanded
 * to for $x in X return for $y in Y return $x + $y.
 * </p>
 * <p>
 * In a single-variable for expression, the variable is called the range variable, the value of the expression
 * that follows the 'in' keyword is called the binding sequence, and the expression that follows the 'return'
 * keyword is called the return expression. The result of the for expression is obtained by evaluating the
 * return expression once for each item in the binding sequence, with the range variable bound to that item.
 * The resulting sequences are concatenated (as if by the comma operator) in the order of the items in the
 * binding sequence from which they were derived.
 * </p>
 */
public class ForAxis extends AbsAxis {

    /** The range expression. */
    private final AbsAxis mRange;

    /** The result expression. */
    private final AbsAxis mReturn;

    /** Defines, whether is first call of hasNext(). */
    private boolean mIsFirst;

    /**
     * Constructor. Initializes the internal state.
     * 
     * @param rtx
     *            Exclusive (immutable) trx to iterate with.
     * @param range
     *            the range variable that holds the binding sequence
     * @param returnExpr
     *            the return expression of the for expression
     */
    public ForAxis(final AbsAxis range, final AbsAxis returnExpr) {

        super(range.getTransaction());
        mRange = range;
        mReturn = returnExpr;
        mIsFirst = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset(final long mNodeKey) {
        super.reset(mNodeKey);
        mIsFirst = true;
        if (mRange != null) {
            mRange.reset(mNodeKey);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {

        resetToLastKey();

        if (mIsFirst) {
            // makes sure, that mRange.hasNext() is called before the return
            // statement,
            // on the first call
            mIsFirst = false;

        } else {
            if (mReturn.hasNext()) {
                return true;
            }
        }

        // check for more items in the binding sequence
        while (mRange.hasNext()) {
            mRange.next();

            // TODO: resetTo startKey would be better here, but not accessible
            // mReturn.resetToStartKey();
            mReturn.reset(getStartKey());
            if (mReturn.hasNext()) {
                return true;
            }
        }

        resetToStartKey();
        return false;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void setTransaction(final IReadTransaction rtx) {
        super.setTransaction(rtx);
        mRange.setTransaction(rtx);
        mReturn.setTransaction(rtx);
    }

}