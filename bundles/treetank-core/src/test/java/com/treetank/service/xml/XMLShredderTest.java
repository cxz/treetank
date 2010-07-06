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
 * $Id: XMLShredderTest.java 4413 2008-08-27 16:59:32Z kramis $
 */

package com.treetank.service.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.custommonkey.xmlunit.XMLTestCase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.treetank.TestHelper;
import com.treetank.TestHelper.PATHS;
import com.treetank.api.IDatabase;
import com.treetank.api.IReadTransaction;
import com.treetank.api.ISession;
import com.treetank.api.IWriteTransaction;
import com.treetank.axis.DescendantAxis;
import com.treetank.exception.TreetankException;
import com.treetank.node.ElementNode;
import com.treetank.node.IStructuralNode;
import com.treetank.settings.ENodes;
import com.treetank.utils.DocumentCreater;
import com.treetank.utils.IConstants;
import com.treetank.utils.TypedValue;

public class XMLShredderTest extends XMLTestCase {

  public static final String XML =
      "src"
          + File.separator
          + "test"
          + File.separator
          + "resources"
          + File.separator
          + "test.xml";

  public static final String XML2 =
      "src"
          + File.separator
          + "test"
          + File.separator
          + "resources"
          + File.separator
          + "test2.xml";

  public static final String XML3 =
      "src"
          + File.separator
          + "test"
          + File.separator
          + "resources"
          + File.separator
          + "test3.xml";

  public static final String XMLREFFOLDER =
      "src"
          + File.separator
          + "test"
          + File.separator
          + "resources"
          + File.separator
          + "revXMLs";

  @Before
  public void setUp() throws TreetankException {
    TestHelper.deleteEverything();
  }

  @After
  public void tearDown() throws TreetankException {
    TestHelper.closeEverything();
  }

  @Test
  public void testSTAXShredder() throws Exception {
    // Setup expected session.
    final IDatabase database = TestHelper.getDatabase(PATHS.PATH1.getFile());

    final ISession expectedSession = database.getSession();
    final IWriteTransaction expectedTrx =
        expectedSession.beginWriteTransaction();
    DocumentCreater.create(expectedTrx);
    expectedTrx.commit();

    // Setup parsed session.
    XMLShredder.main(XML, PATHS.PATH2.getFile().getAbsolutePath());

    // Verify.
    final IDatabase database2 = TestHelper.getDatabase(PATHS.PATH2.getFile());
    final ISession session = database2.getSession();
    final IReadTransaction rtx = session.beginReadTransaction();
    rtx.moveToDocumentRoot();
    final Iterator<Long> expectedDescendants = new DescendantAxis(expectedTrx);
    final Iterator<Long> descendants = new DescendantAxis(rtx);

    while (expectedDescendants.hasNext() && descendants.hasNext()) {
      assertEquals(expectedTrx.getNode().getNodeKey(), rtx
          .getNode()
          .getNodeKey());
      assertEquals(expectedTrx.getNode().getParentKey(), rtx
          .getNode()
          .getParentKey());
      assertEquals(
          ((IStructuralNode) expectedTrx.getNode()).getFirstChildKey(),
          ((IStructuralNode) rtx.getNode()).getFirstChildKey());
      assertEquals(((IStructuralNode) expectedTrx.getNode())
          .getLeftSiblingKey(), ((IStructuralNode) rtx.getNode())
          .getLeftSiblingKey());
      assertEquals(((IStructuralNode) expectedTrx.getNode())
          .getRightSiblingKey(), ((IStructuralNode) rtx.getNode())
          .getRightSiblingKey());

      if (expectedTrx.getNode().getKind() == ENodes.ELEMENT_KIND
          || rtx.getNode().getKind() == ENodes.ELEMENT_KIND) {
        assertEquals(
            ((ElementNode) expectedTrx.getNode()).getChildCount(),
            ((ElementNode) rtx.getNode()).getChildCount());
        assertEquals(
            ((ElementNode) expectedTrx.getNode()).getAttributeCount(),
            ((ElementNode) rtx.getNode()).getAttributeCount());
        assertEquals(
            ((ElementNode) expectedTrx.getNode()).getNamespaceCount(),
            ((ElementNode) rtx.getNode()).getNamespaceCount());
      }
      assertEquals(expectedTrx.getNode().getKind(), rtx.getNode().getKind());
      assertEquals(
          expectedTrx.nameForKey(expectedTrx.getNode().getNameKey()),
          rtx.nameForKey(rtx.getNode().getNameKey()));
      assertEquals(
          expectedTrx.nameForKey(expectedTrx.getNode().getURIKey()),
          rtx.nameForKey(rtx.getNode().getURIKey()));
      if (expectedTrx.getNode().getKind() == ENodes.TEXT_KIND
          || rtx.getNode().getKind() == ENodes.TEXT_KIND) {
        assertEquals(new String(
            expectedTrx.getNode().getRawValue(),
            IConstants.DEFAULT_ENCODING), new String(rtx
            .getNode()
            .getRawValue(), IConstants.DEFAULT_ENCODING));
      }
    }

    expectedTrx.close();
    expectedSession.close();
    rtx.close();
    session.close();
  }

  @Test
  public void testShredIntoExisting() throws Exception {
    final IDatabase database = TestHelper.getDatabase(PATHS.PATH1.getFile());
    final ISession session = database.getSession();
    final IWriteTransaction wtx = session.beginWriteTransaction();
    final XMLShredder shredder =
        new XMLShredder(wtx, XMLShredder.createReader(new File(XML)), true);
    shredder.call();
    assertEquals(1, wtx.getRevisionNumber());
    wtx.moveToDocumentRoot();
    wtx.moveToFirstChild();
    final XMLShredder shredder2 =
        new XMLShredder(wtx, XMLShredder.createReader(new File(XML)), false);
    shredder2.call();
    assertEquals(2, wtx.getRevisionNumber());
    wtx.close();

    // Setup expected session.
    final IDatabase database2 = TestHelper.getDatabase(PATHS.PATH2.getFile());
    final ISession expectedSession = database2.getSession();

    final IWriteTransaction expectedTrx =
        expectedSession.beginWriteTransaction();
    DocumentCreater.create(expectedTrx);
    expectedTrx.commit();
    expectedTrx.moveToDocumentRoot();

    // Verify.
    final IReadTransaction rtx = session.beginReadTransaction();

    final Iterator<Long> descendants = new DescendantAxis(rtx);
    final Iterator<Long> expectedDescendants = new DescendantAxis(expectedTrx);

    while (expectedDescendants.hasNext()) {
      expectedDescendants.next();
      descendants.hasNext();
      descendants.next();
      assertEquals(expectedTrx.getQNameOfCurrentNode(), rtx
          .getQNameOfCurrentNode());
    }

    expectedTrx.moveToDocumentRoot();
    final Iterator<Long> expectedDescendants2 = new DescendantAxis(expectedTrx);
    while (expectedDescendants2.hasNext()) {
      expectedDescendants2.next();
      descendants.hasNext();
      descendants.next();
      assertEquals(expectedTrx.getQNameOfCurrentNode(), rtx
          .getQNameOfCurrentNode());
    }

    expectedTrx.close();
    expectedSession.close();
    rtx.close();
    session.close();
    database.close();
    database2.close();

  }

  @Test
  public void testAttributesNSPrefix() throws Exception {
    // Setup expected session.
    final IDatabase database = TestHelper.getDatabase(PATHS.PATH1.getFile());
    final ISession expectedSession2 = database.getSession();
    final IWriteTransaction expectedTrx2 =
        expectedSession2.beginWriteTransaction();
    DocumentCreater.createWithoutNamespace(expectedTrx2);
    expectedTrx2.commit();

    // Setup parsed session.
    final IDatabase database2 = TestHelper.getDatabase(PATHS.PATH2.getFile());
    final ISession session2 = database2.getSession();
    final IWriteTransaction wtx = session2.beginWriteTransaction();
    final XMLShredder shredder =
        new XMLShredder(wtx, XMLShredder.createReader(new File(XML2)), true);
    shredder.call();
    wtx.commit();
    wtx.close();

    // Verify.
    final IReadTransaction rtx = session2.beginReadTransaction();
    rtx.moveToDocumentRoot();
    final Iterator<Long> expectedAttributes = new DescendantAxis(expectedTrx2);
    final Iterator<Long> attributes = new DescendantAxis(rtx);

    while (expectedAttributes.hasNext() && attributes.hasNext()) {
      if (expectedTrx2.getNode().getKind() == ENodes.ELEMENT_KIND
          || rtx.getNode().getKind() == ENodes.ELEMENT_KIND) {
        assertEquals(
            ((ElementNode) expectedTrx2.getNode()).getNamespaceCount(),
            ((ElementNode) rtx.getNode()).getNamespaceCount());
        assertEquals(
            ((ElementNode) expectedTrx2.getNode()).getAttributeCount(),
            ((ElementNode) rtx.getNode()).getAttributeCount());
        for (int i = 0; i < ((ElementNode) expectedTrx2.getNode())
            .getAttributeCount(); i++) {
          assertEquals(expectedTrx2.nameForKey(expectedTrx2
              .getNode()
              .getNameKey()), rtx.nameForKey(rtx.getNode().getNameKey()));
          assertEquals(expectedTrx2.getNode().getNameKey(), rtx
              .getNode()
              .getNameKey());
          assertEquals(expectedTrx2.nameForKey(expectedTrx2
              .getNode()
              .getURIKey()), rtx.nameForKey(rtx.getNode().getURIKey()));

        }
      }
    }

    assertEquals(expectedAttributes.hasNext(), attributes.hasNext());

    expectedTrx2.close();
    expectedSession2.close();
    rtx.close();
    session2.close();
    database.close();
    database2.close();
  }

  @Test
  public void testShreddingLargeText() throws Exception {
    final IDatabase database = TestHelper.getDatabase(PATHS.PATH2.getFile());
    final ISession session = database.getSession();
    final IWriteTransaction wtx = session.beginWriteTransaction();
    final XMLShredder shredder =
        new XMLShredder(wtx, XMLShredder.createReader(new File(XML3)), true);
    shredder.call();
    wtx.close();

    final IReadTransaction rtx = session.beginReadTransaction();
    assertTrue(rtx.moveToFirstChild());
    assertTrue(rtx.moveToFirstChild());

    final StringBuilder tnkBuilder = new StringBuilder();
    do {
      tnkBuilder.append(TypedValue.parseString(rtx.getNode().getRawValue()));
    } while (rtx.moveToRightSibling());

    final String tnkString = tnkBuilder.toString();

    rtx.close();
    session.close();

    final XMLEventReader validater = XMLShredder.createReader(new File(XML3));
    final StringBuilder xmlBuilder = new StringBuilder();
    while (validater.hasNext()) {
      final XMLEvent event = validater.nextEvent();
      switch (event.getEventType()) {
      case XMLStreamConstants.CHARACTERS:
        final String text = ((Characters) event).getData().trim();
        if (text.length() > 0) {
          xmlBuilder.append(text);
        }
        break;
      }
    }

    assertEquals(xmlBuilder.toString(), tnkString);
  }

  @Test
  public void testShreddingModifiedExistingInserts() throws Exception {
    final IDatabase database = TestHelper.getDatabase(PATHS.PATH1.getFile());
    final ISession session = database.getSession();
    final File folder = new File(XMLREFFOLDER);
    int i = 1;
    final File[] filesList = folder.listFiles();
    final List<File> list = new ArrayList<File>();
    for (final File file : filesList) {
      if (file.getName().endsWith(".xml")) {
        list.add(file);
      }
    }

    // Sort files array according to file names.
    Collections.sort(list, new Comparator<Object>() {
      public int compare(final Object o1, final Object o2) {
        final String fileName1 =
            ((File) o1).getName().toString().substring(
                0,
                ((File) o1).getName().toString().indexOf('.'));
        final String fileName2 =
            ((File) o2).getName().toString().substring(
                0,
                ((File) o2).getName().toString().indexOf('.'));
        if (Integer.parseInt(fileName1) < Integer.parseInt(fileName2)) {
          return -1;
        } else if (Integer.parseInt(fileName1) > Integer.parseInt(fileName2)) {
          return +1;
        } else {
          return 0;
        }
      }
    });

    for (final File file : list) {
      if (file.getName().endsWith(".xml")) {
        System.out.println(file);
        final IWriteTransaction wtx = session.beginWriteTransaction();
        final XMLShredder shredder =
            new XMLShredder(wtx, XMLShredder.createReader(file), true, true);
        shredder.call();
        assertEquals(i, wtx.getRevisionNumber());

        i++;
        wtx.moveToDocumentRoot();
        wtx.close();

        final OutputStream out = new ByteArrayOutputStream();
        final XMLSerializer serializer =
            new XMLSerializer(
                session.beginReadTransaction(),
                out,
                true,
                false,
                false,
                false);
        serializer.call();
        final StringBuilder sBuilder =
            TestHelper.readFile(file.getAbsoluteFile(), false);
        assertXMLEqual(sBuilder.toString(), out.toString());
      }
    }
  }
}
