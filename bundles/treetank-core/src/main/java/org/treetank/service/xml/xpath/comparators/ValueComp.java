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

package org.treetank.service.xml.xpath.comparators;

import org.treetank.api.IReadTransaction;
import org.treetank.axis.AbsAxis;
import org.treetank.exception.TTXPathException;
import org.treetank.service.xml.xpath.AtomicValue;
import org.treetank.service.xml.xpath.EXPathError;
import org.treetank.service.xml.xpath.types.Type;
import org.treetank.utils.TypedValue;

/**
 * <h1>ValueComp</h1>
 * <p>
 * Value comparisons are used for comparing single values.
 * </p>
 * 
 */
public class ValueComp extends AbsComparator {

    /**
     * Constructor. Initializes the internal state.
     * 
     * @param rtx
     *            Exclusive (immutable) trx to iterate with.
     * @param mOperand1
     *            First value of the comparison
     * @param mOperand2
     *            Second value of the comparison
     * @param mComp
     *            comparison kind
     */
    public ValueComp(final IReadTransaction rtx, final AbsAxis mOperand1, final AbsAxis mOperand2,
        final CompKind mComp) {

        super(rtx, mOperand1, mOperand2, mComp);
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    protected boolean compare(final AtomicValue[] mOperand1, final AtomicValue[] mOperand2)
        throws TTXPathException {
        final Type type = getType(mOperand1[0].getTypeKey(), mOperand2[0].getTypeKey());
        final String op1 = TypedValue.parseString(mOperand1[0].getRawValue());
        final String op2 = TypedValue.parseString(mOperand2[0].getRawValue());

        return getCompKind().compare(op1, op2, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AtomicValue[] atomize(final AbsAxis mOperand) throws TTXPathException {

        final IReadTransaction trx = getTransaction();

        int type = trx.getNode().getTypeKey();

        // (3.) if type is untypedAtomic, cast to string
        if (type == trx.keyForName("xs:unytpedAtomic")) {
            type = trx.keyForName("xs:string");
        }

        final AtomicValue atomized = new AtomicValue(mOperand.getTransaction().getNode().getRawValue(), type);
        final AtomicValue[] op = {
            atomized
        };

        // (4.) the operands must be singletons in case of a value comparison
        if (mOperand.hasNext()) {
            throw EXPathError.XPTY0004.getEncapsulatedException();
        } else {
            return op;
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @throws TTXPathException
     */
    @Override
    protected Type getType(final int mKey1, final int mKey2) throws TTXPathException {

        Type type1 = Type.getType(mKey1).getPrimitiveBaseType();
        Type type2 = Type.getType(mKey2).getPrimitiveBaseType();
        return Type.getLeastCommonType(type1, type2);

    }

}