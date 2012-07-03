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

package org.treetank.axis;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.treetank.node.IConstants.ROOT_NODE;

import org.treetank.Holder;
import org.treetank.TestHelper;
import org.treetank.api.INodeReadTrx;
import org.treetank.exception.AbsTTException;

public class DescendantAxisTest {

    private Holder holder;

    @BeforeMethod
    public void setUp() throws AbsTTException {
        TestHelper.deleteEverything();
        TestHelper.createTestDocument();
        holder = Holder.generateRtx();
    }

    @AfterMethod
    public void tearDown() throws AbsTTException {
        holder.close();
        TestHelper.closeEverything();
    }

    @Test
    public void testIterate() throws AbsTTException {
        final INodeReadTrx rtx = holder.getNRtx();

        rtx.moveTo(ROOT_NODE);
        AbsAxisTest.testIAxisConventions(new DescendantAxis(rtx), new long[] {
                1L, 4L, 5L, 6L, 7L, 8L, 9L, 11L, 12L, 13L });

        rtx.moveTo(1L);
        AbsAxisTest.testIAxisConventions(new DescendantAxis(rtx), new long[] {
                4L, 5L, 6L, 7L, 8L, 9L, 11L, 12L, 13L });

        rtx.moveTo(9L);
        AbsAxisTest.testIAxisConventions(new DescendantAxis(rtx), new long[] {
                11L, 12L });

        rtx.moveTo(13L);
        AbsAxisTest
                .testIAxisConventions(new DescendantAxis(rtx), new long[] {});
    }

    @Test
    public void testIterateIncludingSelf() throws AbsTTException {
        final INodeReadTrx rtx = holder.getNRtx();
        rtx.moveTo(ROOT_NODE);
        AbsAxisTest.testIAxisConventions(new DescendantAxis(rtx, true),
                new long[] { ROOT_NODE, 1L, 4L, 5L, 6L, 7L, 8L, 9L, 11L, 12L,
                        13L });

        rtx.moveTo(1L);
        AbsAxisTest.testIAxisConventions(new DescendantAxis(rtx, true),
                new long[] { 1L, 4L, 5L, 6L, 7L, 8L, 9L, 11L, 12L, 13L });

        rtx.moveTo(9L);
        AbsAxisTest.testIAxisConventions(new DescendantAxis(rtx, true),
                new long[] { 9L, 11L, 12L });

        rtx.moveTo(13L);
        AbsAxisTest.testIAxisConventions(new DescendantAxis(rtx, true),
                new long[] { 13L });

    }

}
