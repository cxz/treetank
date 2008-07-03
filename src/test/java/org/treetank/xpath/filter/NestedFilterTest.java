
package org.treetank.xpath.filter;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.treetank.api.ISession;
import org.treetank.api.IWriteTransaction;
import org.treetank.axislayer.AttributeFilter;
import org.treetank.axislayer.ElementFilter;
import org.treetank.axislayer.IFilterTest;
import org.treetank.axislayer.NameFilter;
import org.treetank.axislayer.NodeFilter;
import org.treetank.axislayer.TextFilter;
import org.treetank.sessionlayer.Session;
import org.treetank.utils.TestDocument;
import org.treetank.xpath.filter.ItemFilter;
import org.treetank.xpath.filter.NestedFilter;

public class NestedFilterTest {

  public static final String PATH = "target" + File.separator + "tnk"
      + File.separator + "NestedFilterTest.tnk";

  @Before
  public void setUp() {

    Session.removeSession(PATH);
  }

  @Test
  public void testIFilterConvetions() {

    // Build simple test tree.
    final ISession session = Session.beginSession(PATH);
    final IWriteTransaction wtx = session.beginWriteTransaction();
    TestDocument.create(wtx);

    wtx.moveTo(8L);
    IFilterTest.testIFilterConventions(new NestedFilter(
        wtx, new ItemFilter(wtx), new ElementFilter(wtx), 
        new NameFilter(wtx, "b")), true);
    IFilterTest.testIFilterConventions(
        new NestedFilter(wtx, new ItemFilter(wtx), 
            new AttributeFilter(wtx), new NameFilter(wtx, "b")), false);

    wtx.moveTo(3L);
    IFilterTest.testIFilterConventions(
        new NestedFilter(wtx, new NodeFilter(wtx), 
            new ElementFilter(wtx)), false);
    IFilterTest.testIFilterConventions(
        new NestedFilter(wtx, new NodeFilter(wtx), new TextFilter(wtx)), true);

    wtx.moveTo(2L);
    wtx.moveToAttribute(0);
    IFilterTest.testIFilterConventions(
        new NestedFilter(wtx, new AttributeFilter(wtx), 
            new NameFilter(wtx, "i")), true);

    wtx.abort();
    wtx.close();
    session.close();

  }
}