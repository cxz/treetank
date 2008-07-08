/*
 * Copyright (c) 2008, Tina Scherer (Master Thesis), University of Konstanz
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 * $Id$
 */

package org.treetank.xpath.filter;

import org.treetank.api.IAxis;
import org.treetank.api.IReadTransaction;
import org.treetank.axislayer.AbstractAxis;
import org.treetank.xpath.functions.FNPosition;

/**
 * <h1>PredicateFilterAxis</h1>
 * <p>
 * The PredicateAxis evaluates a predicate (in the form of an axis) and returns
 * true, if the predicates has a value (axis.hasNext == true) and this value if
 * not the boolean value false. Otherwise false is returned. Since a predicate
 * is a kind of filter, the transaction that has been altered by means of the
 * predicate's evaluation has to be reset to the key that it was set to before
 * the evaluation.
 * </p>
 */
public class PredicateFilterAxis extends AbstractAxis implements IAxis {

  private boolean mIsFirst;

  private final IAxis mPredicate;

  /**
   * Constructor. Initializes the internal state.
   * 
   * @param rtx
   *          Exclusive (immutable) trx to iterate with.
   * @param predicate
   *          predicate expression
   */
  public PredicateFilterAxis(final IReadTransaction rtx, final IAxis predicate) {

    super(rtx);
    mIsFirst = true;
    mPredicate = predicate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void reset(final long nodeKey) {

    super.reset(nodeKey);
    if (mPredicate != null) {
      mPredicate.reset(nodeKey);
    }
    mIsFirst = true;
  }

  /**
   * {@inheritDoc}
   */
  public final boolean hasNext() {

    resetToLastKey();

    // a predicate has to evaluate to true only once.
    if (mIsFirst) {
      mIsFirst = false;
      mPredicate.reset(getTransaction().getNodeKey());

      if (mPredicate.hasNext()) {

        if (isBooleanFalse()) {
          resetToStartKey();
          return false;
        }

        // reset is needed, because a predicate works more like a filter. It
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

    if (getTransaction().getNodeKey() >= 0) {
      return false;
    } else { // is AtomicValue
      if (getTransaction().getTypeKey() == getTransaction().keyForName(
          "xs:boolean")) {
        // atomic value of type boolean
        // return true, if atomic values's value is false
        return !(Boolean.parseBoolean(getTransaction().getValue()));

      } else {
        return false;
      }
    }

  }

}
