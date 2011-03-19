/*
 * Copyright (c) 2008, Marc Kramis (Ph.D. Thesis), University of Konstanz
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
 * $Id: MultipleCommitTest.java 4413 2008-08-27 16:59:32Z kramis $
 */

package org.treetank.access;

import javax.xml.namespace.QName;

import junit.framework.Assert;


import org.treetank.TestHelper;
import org.treetank.TestHelper.PATHS;
import org.treetank.api.IDatabase;
import org.treetank.api.IReadTransaction;
import org.treetank.api.ISession;
import org.treetank.api.IWriteTransaction;
import org.treetank.axis.AbsAxis;
import org.treetank.axis.DescendantAxis;
import org.treetank.axis.PostOrderAxis;
import org.treetank.exception.AbsTTException;
import org.treetank.node.ENodes;
import org.treetank.node.ElementNode;
import org.treetank.utils.DocumentCreater;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MultipleCommitTest {

    @Before
    public void setUp() throws AbsTTException {
        TestHelper.deleteEverything();
    }

    @After
    public void tearDown() throws AbsTTException {
        TestHelper.closeEverything();
    }

    @Test
    public void test() throws AbsTTException {
        final IDatabase database = TestHelper.getDatabase(PATHS.PATH1.getFile());
        final ISession session = database.getSession();
        final IWriteTransaction wtx = session.beginWriteTransaction();
        Assert.assertEquals(0L, wtx.getRevisionNumber());

        wtx.commit();

        wtx.insertElementAsFirstChild(new QName("foo"));
        assertEquals(1L, wtx.getRevisionNumber());
        wtx.moveTo(1);
        assertEquals(new QName("foo"), wtx.getQNameOfCurrentNode());
        wtx.abort();

        assertEquals(1L, wtx.getRevisionNumber());

        wtx.close();

        session.close();
        database.close();
    }

    @Test
    public void testAutoCommit() throws AbsTTException {
        final IDatabase database = TestHelper.getDatabase(PATHS.PATH1.getFile());
        final ISession session = database.getSession();
        final IWriteTransaction wtx = session.beginWriteTransaction(100, 1);
        DocumentCreater.create(wtx);
        wtx.commit();
        wtx.close();

        final IReadTransaction rtx = session.beginReadTransaction();
        rtx.close();
        session.close();
        database.close();
    }

    @Test
    public void testRemove() throws AbsTTException {
        final IDatabase database = TestHelper.getDatabase(PATHS.PATH1.getFile());
        final ISession session = database.getSession();
        final IWriteTransaction wtx = session.beginWriteTransaction();
        DocumentCreater.create(wtx);
        wtx.commit();
        assertEquals(1L, wtx.getRevisionNumber());

        wtx.moveToDocumentRoot();
        wtx.moveToFirstChild();
        wtx.remove();
        wtx.commit();
        assertEquals(2L, wtx.getRevisionNumber());

        wtx.close();
        session.close();
        database.close();

    }

    @Test
    public void testAttributeRemove() throws AbsTTException {
        final IDatabase database = TestHelper.getDatabase(PATHS.PATH1.getFile());
        final ISession session = database.getSession();
        final IWriteTransaction wtx = session.beginWriteTransaction();
        DocumentCreater.create(wtx);
        wtx.commit();
        wtx.moveToDocumentRoot();

        final AbsAxis postorderAxis = new PostOrderAxis(wtx);
        while (postorderAxis.hasNext()) {
            postorderAxis.next();
            if (wtx.getNode().getKind() == ENodes.ELEMENT_KIND
                && ((ElementNode)wtx.getNode()).getAttributeCount() > 0) {
                for (int i = 0, attrCount = ((ElementNode)wtx.getNode()).getAttributeCount(); i < attrCount; i++) {
                    wtx.moveToAttribute(i);
                    wtx.remove();
                }
            }
        }
        wtx.commit();
        wtx.moveToDocumentRoot();

        int attrTouch = 0;
        final AbsAxis descAxis = new DescendantAxis(wtx);
        while (descAxis.hasNext()) {
            descAxis.next();
            if (wtx.getNode().getKind() == ENodes.ELEMENT_KIND) {
                for (int i = 0, attrCount = ((ElementNode)wtx.getNode()).getAttributeCount(); i < attrCount; i++) {
                    if (wtx.moveToAttribute(i)) {
                        attrTouch++;
                    } else {
                        throw new IllegalStateException("Should never occur!");
                    }
                }
            }
        }
        wtx.close();
        session.close();
        database.close();
        assertEquals(0, attrTouch);

    }

}