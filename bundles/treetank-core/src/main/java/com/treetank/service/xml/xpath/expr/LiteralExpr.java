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

package com.treetank.service.xml.xpath.expr;

import com.treetank.api.IReadTransaction;

/**
 * <h1>LiteralExpr</h1>
 * <p>
 * Expression that holds a literal.
 * </p>
 */
public class LiteralExpr extends AbsExpression {

    private final long mLiteralKey;

    /**
     * Constructor. Initializes the internal state.
     * 
     * @param rtx
     *            Exclusive (immutable) trx to iterate with.
     * @param mItemKey
     *            itemKey of the literal
     */
    public LiteralExpr(final IReadTransaction rtx, final long mItemKey) {
        super(rtx);

        mLiteralKey = mItemKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evaluate() {

        // set transaction to literal
        getTransaction().moveTo(mLiteralKey);

    }

}
