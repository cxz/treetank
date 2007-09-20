/*
 * TreeTank - Embedded Native XML Database
 * 
 * Copyright (C) 2007 Marc Kramis
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */

package org.treetank.nodelayer;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.treetank.api.IReadTransaction;
import org.treetank.api.ISession;
import org.treetank.api.IWriteTransaction;
import org.treetank.nodelayer.Session;
import org.treetank.utils.IConstants;
import org.treetank.utils.UTF;


public class UpdateTest {

  public static final String TEST_PATH = "generated/UpdateTest.tnk";

  @Before
  public void setUp() throws Exception {
    new File(TEST_PATH).delete();
  }

  @Test
  public void testInsertChild() throws Exception {

    ISession session = new Session(TEST_PATH);

    // Document root.
    IWriteTransaction trx = session.beginWriteTransaction();
    trx.insertRoot("test");
    session.commit();

    IReadTransaction rTrx = session.beginReadTransaction();
    assertEquals(1L, rTrx.revisionSize());
    assertEquals(1L, rTrx.revisionKey());

    // Insert 100 children.
    for (int i = 1; i <= 100; i++) {
      session = new Session(TEST_PATH);
      trx = session.beginWriteTransaction();
      trx.moveToRoot();
      trx.insertFirstChild(IConstants.TEXT, "", "", "", UTF.convert(Integer
          .toString(i)));
      session.commit();
      session.close();

      rTrx = session.beginReadTransaction();
      rTrx.moveToRoot();
      rTrx.moveToFirstChild();
      assertEquals(Integer.toString(i), new String(rTrx.getValue()));
      assertEquals(i + 1L, rTrx.revisionSize());
      assertEquals(i + 1L, rTrx.revisionKey());
    }
    
    session = new Session(TEST_PATH);
    rTrx = session.beginReadTransaction();
    rTrx.moveToRoot();
    rTrx.moveToFirstChild();
    assertEquals(Integer.toString(100), new String(rTrx.getValue()));
    assertEquals(101L, rTrx.revisionSize());
    assertEquals(101L, rTrx.revisionKey());
    session.close();

  }

}
