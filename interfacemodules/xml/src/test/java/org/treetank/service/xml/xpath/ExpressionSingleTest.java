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

package org.treetank.service.xml.xpath;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.treetank.Holder;
import org.treetank.NodeHelper;
import org.treetank.TestHelper;
import org.treetank.axis.AbsAxis;
import org.treetank.axis.ChildAxis;
import org.treetank.axis.DescendantAxis;
import org.treetank.axis.FollowingSiblingAxis;
import org.treetank.axis.NestedAxis;
import org.treetank.axis.ParentAxis;
import org.treetank.axis.SelfAxis;
import org.treetank.exception.TTException;
import org.treetank.service.xml.xpath.axis.UnionAxis;
import org.treetank.service.xml.xpath.filter.DupFilterAxis;

public class ExpressionSingleTest {

    private Holder holder;

    @BeforeMethod
    public void setUp() throws TTException {
        TestHelper.deleteEverything();
        NodeHelper.createTestDocument();
        holder = Holder.generateRtx();
    }

    @AfterMethod
    public void tearDown() throws TTException {
        holder.close();
        TestHelper.closeEverything();
    }

    @Test
    public void testAdd() throws TTException {
        // Verify.
        final ExpressionSingle builder = new ExpressionSingle(holder.getNRtx());

        // test one axis
        AbsAxis self = new SelfAxis(holder.getNRtx());
        builder.add(self);
        assertEquals(builder.getExpr(), self);

        // test 2 axis
        AbsAxis axis1 = new SelfAxis(holder.getNRtx());
        AbsAxis axis2 = new SelfAxis(holder.getNRtx());
        builder.add(axis1);
        builder.add(axis2);
        assertTrue(builder.getExpr() instanceof NestedAxis);

    }

    @Test
    public void testDup() throws TTException {
        ExpressionSingle builder = new ExpressionSingle(holder.getNRtx());
        builder.add(new ChildAxis(holder.getNRtx()));
        builder.add(new DescendantAxis(holder.getNRtx()));
        assertTrue(builder.getExpr() instanceof NestedAxis);

        builder = new ExpressionSingle(holder.getNRtx());
        builder.add(new ChildAxis(holder.getNRtx()));
        builder.add(new DescendantAxis(holder.getNRtx()));
        assertEquals(true, builder.isOrdered());
        assertTrue(builder.getExpr() instanceof NestedAxis);

        builder = new ExpressionSingle(holder.getNRtx());
        builder.add(new ChildAxis(holder.getNRtx()));
        builder.add(new DescendantAxis(holder.getNRtx()));
        builder.add(new ChildAxis(holder.getNRtx()));
        assertEquals(false, builder.isOrdered());

        builder = new ExpressionSingle(holder.getNRtx());
        builder = new ExpressionSingle(holder.getNRtx());
        builder.add(new ChildAxis(holder.getNRtx()));
        builder.add(new DescendantAxis(holder.getNRtx()));
        builder.add(new ChildAxis(holder.getNRtx()));
        builder.add(new ParentAxis(holder.getNRtx()));
        assertEquals(true, builder.isOrdered());

        builder = new ExpressionSingle(holder.getNRtx());
        builder.add(new ChildAxis(holder.getNRtx()));
        builder.add(new DescendantAxis(holder.getNRtx()));
        builder.add(new FollowingSiblingAxis(holder.getNRtx()));
        assertEquals(false, builder.isOrdered());

        builder = new ExpressionSingle(holder.getNRtx());
        builder.add(new UnionAxis(holder.getNRtx(), new DescendantAxis(holder.getNRtx()), new ParentAxis(
            holder.getNRtx())));
        assertEquals(false, builder.isOrdered());
        assertTrue(builder.getExpr() instanceof DupFilterAxis);

    }
}
