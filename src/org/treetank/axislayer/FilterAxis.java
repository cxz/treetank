/*
 * Copyright 2007, Marc Kramis
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *   
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * $Id$
 */

package org.treetank.axislayer;

import org.treetank.api.IAxis;
import org.treetank.api.IFilter;

/**
 * <h1>TestAxis</h1>
 * 
 * <p>
 * Perform a test on a given axis.
 * </p>
 */
public class FilterAxis extends AbstractAxis {

  /** Axis to test. */
  private final IAxis mAxis;

  /** Test to apply to axis. */
  private final IFilter mAxisTest;

  /**
   * Constructor initializing internal state.
   * 
   * @param axis Axis to iterate over.
   * @param axisTest Test to perform for each node found with axis.
   */
  public FilterAxis(final IAxis axis, final IFilter axisTest) {
    super(axis.getTransaction());
    mAxis = axis;
    mAxisTest = axisTest;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void reset(final long nodeKey) {
    super.reset(nodeKey);
    if (mAxis != null) {
      mAxis.reset(nodeKey);
    }
  }

  /**
   * {@inheritDoc}
   */
  public final boolean hasNext() {
    resetToLastKey();
    while (mAxis.hasNext()) {
      mAxis.next();
      if (mAxisTest.test(getTransaction())) {
        return true;
      }
    }
    resetToStartKey();
    return false;
  }
}
