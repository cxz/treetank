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

package org.treetank.axislayer;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.treetank.api.ISession;
import org.treetank.api.IWriteTransaction;
import org.treetank.sessionlayer.Session;
import org.treetank.utils.DocumentTest;

public class DocumentRootNodeFilterTest {

  public static final String PATH =
      "target"
          + File.separator
          + "tnk"
          + File.separator
          + "DocumentRootNodeFilterTest.tnk";

  @Before
  public void setUp() {
    Session.removeSession(PATH);
  }

  @Test
  public void testIFilterConvetions() {

    // Build simple test tree.
    final ISession session = Session.beginSession(PATH);
    final IWriteTransaction wtx = session.beginWriteTransaction();
    DocumentTest.create(wtx);

    wtx.moveTo(0L);
    IFilterTest.testIFilterConventions(new DocumentRootNodeFilter(wtx), true);

    wtx.moveTo(1L);
    IFilterTest.testIFilterConventions(new DocumentRootNodeFilter(wtx), false);

    wtx.moveTo(1L);
    wtx.moveToAttribute(0);
    IFilterTest.testIFilterConventions(new DocumentRootNodeFilter(wtx), false);

    wtx.moveTo(3L);
    IFilterTest.testIFilterConventions(new DocumentRootNodeFilter(wtx), false);

    wtx.moveTo(4L);
    IFilterTest.testIFilterConventions(new DocumentRootNodeFilter(wtx), false);

    wtx.moveTo(5L);
    IFilterTest.testIFilterConventions(new DocumentRootNodeFilter(wtx), false);

    wtx.moveTo(9L);
    IFilterTest.testIFilterConventions(new DocumentRootNodeFilter(wtx), false);

    wtx.moveTo(9L);
    wtx.moveToAttribute(0);
    IFilterTest.testIFilterConventions(new DocumentRootNodeFilter(wtx), false);

    wtx.moveTo(12L);
    IFilterTest.testIFilterConventions(new DocumentRootNodeFilter(wtx), false);

    wtx.moveTo(13L);
    wtx.moveToDocumentRoot();
    IFilterTest.testIFilterConventions(new DocumentRootNodeFilter(wtx), true);

    wtx.abort();
    wtx.close();
    session.close();

  }

}
