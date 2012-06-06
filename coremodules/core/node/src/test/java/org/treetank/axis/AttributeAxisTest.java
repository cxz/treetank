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

import static org.treetank.node.IConstants.ROOT_NODE;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.treetank.Holder;
import org.treetank.TestHelper;
import org.treetank.api.INodeReadTrx;
import org.treetank.api.INodeWriteTrx;
import org.treetank.exception.AbsTTException;

public class AttributeAxisTest {

    private Holder holder;

    @Before
    public void setUp() throws AbsTTException {
        TestHelper.deleteEverything();
        TestHelper.createTestDocument();
        holder = Holder.generateRtx();

    }

    @After
    public void tearDown() throws AbsTTException {
        holder.close();
        TestHelper.closeEverything();
    }

    @Test
    public void testIterate() throws AbsTTException {
        final INodeReadTrx wtx = holder.getNRtx();

        wtx.moveTo(ROOT_NODE);
        AbsAxisTest.testIAxisConventions(new AttributeAxis(wtx), new long[] {});

        wtx.moveTo(1L);
        AbsAxisTest.testIAxisConventions(new AttributeAxis(wtx), new long[] {
            2L
        });

        wtx.moveTo(9L);
        AbsAxisTest.testIAxisConventions(new AttributeAxis(wtx), new long[] {
            10L
        });

        wtx.moveTo(12L);
        AbsAxisTest.testIAxisConventions(new AttributeAxis(wtx), new long[] {});

        wtx.moveTo(2L);
        AbsAxisTest.testIAxisConventions(new AttributeAxis(wtx), new long[] {});
    }

    @Test
    @Ignore
    public void testMultipleAttributes() throws AbsTTException {
        final INodeWriteTrx wtx = holder.getNWtx();
        final long nodeKey = wtx.insertElementAsFirstChild(new QName("foo"));
        wtx.insertAttribute(new QName("foo0"), "0");
        wtx.moveTo(nodeKey);
        wtx.insertAttribute(new QName("foo1"), "1");
        wtx.moveTo(nodeKey);
        wtx.insertAttribute(new QName("foo2"), "2");

        Assert.assertEquals(true, wtx.moveTo(nodeKey));

        Assert.assertEquals(true, wtx.moveToAttribute(0));
        Assert.assertEquals("0", wtx.getValueOfCurrentNode());
        Assert.assertEquals(new QName("foo0"), wtx.getQNameOfCurrentNode());

        Assert.assertEquals(true, wtx.moveTo(wtx.getNode().getParentKey()));
        Assert.assertEquals(true, wtx.moveToAttribute(1));
        Assert.assertEquals("1", wtx.getValueOfCurrentNode());
        Assert.assertEquals(new QName("foo1"), wtx.getQNameOfCurrentNode());

        Assert.assertEquals(true, wtx.moveTo(wtx.getNode().getParentKey()));
        Assert.assertEquals(true, wtx.moveToAttribute(2));
        Assert.assertEquals("2", wtx.getValueOfCurrentNode());
        Assert.assertEquals(new QName("foo2"), wtx.getQNameOfCurrentNode());

        Assert.assertEquals(true, wtx.moveTo(nodeKey));
        final AbsAxis axis = new AttributeAxis(wtx);

        Assert.assertEquals(true, axis.hasNext());
        axis.next();
        Assert.assertEquals(nodeKey + 1, wtx.getNode().getNodeKey());
        Assert.assertEquals(new QName("foo0"), wtx.getQNameOfCurrentNode());
        Assert.assertEquals("0", wtx.getValueOfCurrentNode());

        Assert.assertEquals(true, axis.hasNext());
        axis.next();
        Assert.assertEquals(nodeKey + 2, wtx.getNode().getNodeKey());
        Assert.assertEquals(new QName("foo1"), wtx.getQNameOfCurrentNode());
        Assert.assertEquals("1", wtx.getValueOfCurrentNode());

        Assert.assertEquals(true, axis.hasNext());
        axis.next();
        Assert.assertEquals(nodeKey + 3, wtx.getNode().getNodeKey());
        Assert.assertEquals(new QName("foo2"), wtx.getQNameOfCurrentNode());
        Assert.assertEquals("2", wtx.getValueOfCurrentNode());

        wtx.abort();
        wtx.close();
    }
}
