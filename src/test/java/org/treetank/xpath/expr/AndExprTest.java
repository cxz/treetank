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

package org.treetank.xpath.expr;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.treetank.api.IAxis;
import org.treetank.api.IReadTransaction;
import org.treetank.api.ISession;
import org.treetank.api.IWriteTransaction;
import org.treetank.sessionlayer.Session;
import org.treetank.utils.DocumentTest;
import org.treetank.xpath.AtomicValue;
import org.treetank.xpath.XPathAxis;

/**
 * JUnit-test class to test the functionality of the AndExpr.
 * 
 * @author Tina Scherer
 * 
 */
public class AndExprTest {

  public static final String PATH =
      "target" + File.separator + "tnk" + File.separator + "AndExprTest.tnk";

  IAxis ifExpr, thenExpr, elseExpr;

  @Before
  public void setUp() {

    Session.removeSession(PATH);

  }

  @Test
  public void testAnd() {

    final ISession session = Session.beginSession(PATH);
    IReadTransaction rtx = session.beginReadTransaction();

    long iTrue = rtx.getItemList().addItem(new AtomicValue(true));
    long iFalse = rtx.getItemList().addItem(new AtomicValue(false));

    IAxis trueLit1 = new LiteralExpr(rtx, iTrue);
    IAxis trueLit2 = new LiteralExpr(rtx, iTrue);
    IAxis falseLit1 = new LiteralExpr(rtx, iFalse);
    IAxis falseLit2 = new LiteralExpr(rtx, iFalse);

    IAxis axis1 = new AndExpr(rtx, trueLit1, trueLit2);
    assertEquals(true, axis1.hasNext());
    assertEquals(true, Boolean.parseBoolean(rtx.getValue()));
    assertEquals(false, axis1.hasNext());

    IAxis axis2 = new AndExpr(rtx, trueLit1, falseLit1);
    assertEquals(true, axis2.hasNext());
    assertEquals(false, Boolean.parseBoolean(rtx.getValue()));
    assertEquals(false, axis2.hasNext());

    IAxis axis3 = new AndExpr(rtx, falseLit1, trueLit1);
    assertEquals(true, axis3.hasNext());
    assertEquals(false, Boolean.parseBoolean(rtx.getValue()));
    assertEquals(false, axis3.hasNext());

    IAxis axis4 = new AndExpr(rtx, falseLit1, falseLit2);
    assertEquals(true, axis4.hasNext());
    assertEquals(false, Boolean.parseBoolean(rtx.getValue()));
    assertEquals(false, axis4.hasNext());

    rtx.close();
    session.close();
  }

  @Test
  public void testAndQuery() {

    // Build simple test tree.
    final ISession session = Session.beginSession(PATH);
    final IWriteTransaction wtx = session.beginWriteTransaction();
    DocumentTest.create(wtx);
    IReadTransaction rtx = session.beginReadTransaction();

    rtx.moveTo(2L);

    final IAxis axis1 = new XPathAxis(rtx, "text() and node()");
    assertEquals(true, axis1.hasNext());
    assertEquals(true, Boolean.parseBoolean(rtx.getValue()));
    assertEquals(false, axis1.hasNext());

    final IAxis axis2 = new XPathAxis(rtx, "comment() and node()");
    assertEquals(true, axis2.hasNext());
    assertEquals(false, Boolean.parseBoolean(rtx.getValue()));
    assertEquals(false, axis2.hasNext());

    final IAxis axis3 = new XPathAxis(rtx, "1 eq 1 and 2 eq 2");
    assertEquals(true, axis3.hasNext());
    assertEquals(true, Boolean.parseBoolean(rtx.getValue()));
    assertEquals(false, axis3.hasNext());

    final IAxis axis4 = new XPathAxis(rtx, "1 eq 1 and 2 eq 3");
    assertEquals(true, axis4.hasNext());
    assertEquals(false, Boolean.parseBoolean(rtx.getValue()));
    assertEquals(false, axis4.hasNext());

    //is never evaluated.
    //    final IAxis axis5 = new XPathAxis(rtx, "1 eq 2 and (3 idiv 0 = 1)");
    //    assertEquals(true, axis5.hasNext());
    //    assertEquals(false, Boolean.parseBoolean(rtx.getValue()));
    //    assertEquals(false, axis5.hasNext());

    //    //TODO: should raise an dynamic error
    //    final IAxis axis6 = new XPathAxis(rtx, "1 eq 1 and 3 idiv 0 = 1");
    //    assertEquals(true, axis6.hasNext());

    rtx.close();
    wtx.abort();
    wtx.close();
    session.close();

  }

}
