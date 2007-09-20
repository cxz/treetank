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

package org.treetank.pagelayer;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.treetank.pagelayer.Node;
import org.treetank.pagelayer.NodePage;
import org.treetank.pagelayer.PageReader;
import org.treetank.pagelayer.PageReference;
import org.treetank.pagelayer.PageWriter;
import org.treetank.utils.FastByteArrayReader;
import org.treetank.utils.IConstants;
import org.treetank.utils.UTF;


public class PageWriterTest {

  private final static String PATH = "generated/PageWriterTest.tnk";

  @Before
  public void setUp() throws Exception {
    new File(PATH).delete();
  }

  @Test
  public void testWriteRead() throws Exception {

    // Create node page with single node.
    final NodePage page1 = NodePage.create(13L);
    page1.setNode(3, new Node(0L, 1L, 2L, 3L, 4L, 5, 6, 7, 8, UTF
        .convert("foo")));
    final PageReference pageReference = new PageReference();

    // Serialize node page.
    final PageWriter pageWriter = new PageWriter(PATH);
    pageReference.setPage(page1);
    pageWriter.write(pageReference);
    assertEquals(0L, pageReference.getStart());

    // Deserialize node page.
    final PageReader pageReader = new PageReader(PATH);
    final FastByteArrayReader in = pageReader.read(pageReference);
    final NodePage page2 = NodePage.read(in);

    assertEquals("foo", new String(
        page2.getNode(3).getValue(),
        IConstants.ENCODING));
  }

}
