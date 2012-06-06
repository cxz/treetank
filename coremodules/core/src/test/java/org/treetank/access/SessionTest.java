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

package org.treetank.access;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.treetank.node.IConstants.ROOT_NODE;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.treetank.Holder;
import org.treetank.TestHelper;
import org.treetank.TestHelper.PATHS;
import org.treetank.access.conf.SessionConfiguration;
import org.treetank.api.IDatabase;
import org.treetank.api.INodeReadTrx;
import org.treetank.api.INodeWriteTrx;
import org.treetank.api.ISession;
import org.treetank.exception.AbsTTException;
import org.treetank.node.ENode;
import org.treetank.node.interfaces.INode;
import org.treetank.node.interfaces.IStructNode;
import org.treetank.utils.DocumentCreater;

public class SessionTest {

    private Holder holder;

    @Before
    public void setUp() throws AbsTTException {
        TestHelper.deleteEverything();
        holder = Holder.generateWtx();
    }

    @After
    public void tearDown() throws AbsTTException {
        holder.close();
        TestHelper.closeEverything();
    }

    @Test
    public void testSingleton() throws AbsTTException {
        final IDatabase database = Holder.generateSession().getDatabase();
        assertEquals(database, holder.getDatabase());
        final ISession session =
            database.getSession(new SessionConfiguration.Builder(TestHelper.RESOURCE).build());
        assertEquals(session, holder.getSession());
        session.close();
        final ISession session2 =
            database.getSession(new SessionConfiguration.Builder(TestHelper.RESOURCE).build());
        assertNotSame(session2, holder.getSession());
        database.close();

    }

    @Test
    public void testClosed() throws AbsTTException {
        INodeReadTrx rtx = holder.getNRtx();
        rtx.close();

        try {
            final INode node = rtx.getNode();
            node.getNodeKey();
            fail();
        } catch (Exception e) {
            // Must fail.
        } finally {
            holder.getSession().close();
        }
    }

    @Test
    public void testNonExisting() throws AbsTTException, InterruptedException {
        final IDatabase database = TestHelper.getDatabase(PATHS.PATH1.getFile());
        final IDatabase database2 = TestHelper.getDatabase(PATHS.PATH1.getFile());
        assertTrue(database == database2);
    }

    @Test
    public void testInsertChild() throws AbsTTException {
        final INodeWriteTrx wtx = holder.getNWtx();
        DocumentCreater.create(wtx);
        assertNotNull(wtx.moveTo(ROOT_NODE));
        assertEquals(ENode.ROOT_KIND, wtx.getNode().getKind());

        assertNotNull(wtx.moveTo(((IStructNode)wtx.getNode()).getFirstChildKey()));
        assertEquals(ENode.ELEMENT_KIND, wtx.getNode().getKind());
        assertEquals("p:a", new StringBuilder(wtx.getQNameOfCurrentNode().getPrefix()).append(":").append(
            wtx.getQNameOfCurrentNode().getLocalPart()).toString());

        wtx.abort();
        wtx.close();
    }

//    @Test
//    public void testRevision() throws AbsTTException {
//
//        final INodeWriteTrx wtx = holder.getNWtx();
//        assertEquals(0L, holder.getSession().getMostRecentVersion());
//
//        // Commit and check.
//        wtx.commit();
//        wtx.close();
//
//        rtx = holder.getSession().beginNodeReadTransaction();
//
//        assertEquals(IConstants.UBP_ROOT_REVISION_NUMBER, rtx.getRevisionNumber());
//        rtx.close();
//
//        final INodeReadTrx rtx2 = holder.getSession().beginNodeReadTransaction();
//        assertEquals(0L, rtx2.getRevisionNumber());
//        rtx2.close();
//    }
//
//    @Test
//    public void testShreddedRevision() throws AbsTTException {
//
//        final INodeWriteTrx wtx1 = holder.getSession().beginNodeWriteTransaction();
//        DocumentCreater.create(wtx1);
//        assertEquals(0L, wtx1.getRevisionNumber());
//        wtx1.commit();
//        wtx1.close();
//
//        final INodeReadTrx rtx1 = holder.getSession().beginNodeReadTransaction();
//        assertEquals(0L, rtx1.getRevisionNumber());
//        rtx1.moveTo(12L);
//        assertEquals("bar", rtx1.getValueOfCurrentNode());
//
//        final INodeWriteTrx wtx2 = holder.getSession().beginNodeWriteTransaction();
//        assertEquals(1L, wtx2.getRevisionNumber());
//        wtx2.moveTo(12L);
//        wtx2.setValue("bar2");
//
//        assertEquals("bar", rtx1.getValueOfCurrentNode());
//        assertEquals("bar2", wtx2.getValueOfCurrentNode());
//        rtx1.close();
//        wtx2.abort();
//        wtx2.close();
//
//        final INodeReadTrx rtx2 = holder.getSession().beginNodeReadTransaction();
//        assertEquals(0L, rtx2.getRevisionNumber());
//        rtx2.moveTo(12L);
//        assertEquals("bar", rtx2.getValueOfCurrentNode());
//        rtx2.close();
//    }
//
//    @Test
//    public void testExisting() throws AbsTTException {
//        final IDatabase database = TestHelper.getDatabase(PATHS.PATH1.getFile());
//        final ISession session1 =
//            database.getSession(new SessionConfiguration.Builder(TestHelper.RESOURCE).build());
//
//        final INodeWriteTrx wtx1 = session1.beginNodeWriteTransaction();
//        DocumentCreater.create(wtx1);
//        assertEquals(0L, wtx1.getRevisionNumber());
//        wtx1.commit();
//        wtx1.close();
//        session1.close();
//
//        final ISession session2 =
//            database.getSession(new SessionConfiguration.Builder(TestHelper.RESOURCE).build());
//        final INodeReadTrx rtx1 = session2.beginNodeReadTransaction();
//        assertEquals(0L, rtx1.getRevisionNumber());
//        rtx1.moveTo(12L);
//        assertEquals("bar", rtx1.getValueOfCurrentNode());
//
//        final INodeWriteTrx wtx2 = session2.beginNodeWriteTransaction();
//        assertEquals(1L, wtx2.getRevisionNumber());
//        wtx2.moveTo(12L);
//        wtx2.setValue("bar2");
//
//        assertEquals("bar", rtx1.getValueOfCurrentNode());
//        assertEquals("bar2", wtx2.getValueOfCurrentNode());
//
//        rtx1.close();
//        wtx2.commit();
//        wtx2.close();
//        session2.close();
//
//        final IDatabase database2 = TestHelper.getDatabase(PATHS.PATH1.getFile());
//        final ISession session3 =
//            database2.getSession(new SessionConfiguration.Builder(TestHelper.RESOURCE).build());
//        final INodeReadTrx rtx2 = session3.beginNodeReadTransaction();
//        assertEquals(1L, rtx2.getRevisionNumber());
//        rtx2.moveTo(12L);
//        assertEquals("bar2", rtx2.getValueOfCurrentNode());
//
//        rtx2.close();
//        session3.close();
//
//    }
//
//    @Test
//    public void testIdempotentClose() throws AbsTTException {
//        final INodeWriteTrx wtx = holder.getSession().beginNodeWriteTransaction();
//        DocumentCreater.create(wtx);
//        wtx.commit();
//        wtx.close();
//        wtx.close();
//
//        final INodeReadTrx rtx = holder.getSession().beginNodeReadTransaction();
//        assertEquals(false, rtx.moveTo(14L));
//        rtx.close();
//        rtx.close();
//        holder.getSession().close();
//
//    }
//
//    @Test
//    public void testAutoCommit() throws AbsTTException {
//        final INodeWriteTrx wtx = holder.getSession().beginNodeWriteTransaction();
//
//        DocumentCreater.create(wtx);
//    }
//
//    @Test
//    public void testAutoClose() throws AbsTTException {
//
//        final INodeWriteTrx wtx = holder.getSession().beginNodeWriteTransaction();
//        DocumentCreater.create(wtx);
//        wtx.commit();
//        holder.getSession().beginNodeReadTransaction();
//    }
}
