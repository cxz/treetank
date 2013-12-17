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

package org.treetank.data;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AtomicValueTest {

    private AtomicValue a, b, c, d, e, f, zero, n_zero;

    private int aVal = 1;

    private String bVal = "test";

    private float cVal = 2345.1441f;

    private double dVal = 245E2;

    private boolean eVal = true;

    @BeforeMethod
    public void setUp() {

        a = new AtomicValue(aVal, Type.INTEGER);
        b = new AtomicValue(bVal, Type.STRING);
        c = new AtomicValue(cVal, Type.FLOAT);
        d = new AtomicValue(dVal, Type.DOUBLE);
        e = new AtomicValue(eVal);
        f = new AtomicValue(2.0d, Type.DOUBLE);

        zero = new AtomicValue(+0, Type.INTEGER);
        n_zero = new AtomicValue(-0, Type.INTEGER);

    }

    @Test
    public final void testGetInt() {

        assertEquals(a.getInt(), aVal);
        assertEquals(zero.getInt(), +0);
        assertEquals(n_zero.getInt(), -0);
    }

    @Test
    public final void testGetDBL() {
        assertEquals(d.getDBL(), dVal);
        assertEquals(f.getDBL(), 2.0d);
    }

    @Test
    public final void testGetFLT() {
        assertEquals(c.getFLT(), (float)cVal);

    }

    @Test
    public final void testGetString() {

        assertEquals(new String(b.getRawValue()), bVal);
    }

    @Test
    public final void testGetBool() {
        assertEquals(true, e.getBool());
    }

    @Test
    public final void testGetStringValue() {
        testGetString();
    }

}
