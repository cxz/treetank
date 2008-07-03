package org.treetank.xpath.expr;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.treetank.api.IReadTransaction;
import org.treetank.api.ISession;
import org.treetank.api.IWriteTransaction;
import org.treetank.axislayer.IAxisTest;
import org.treetank.sessionlayer.ItemList;
import org.treetank.sessionlayer.Session;
import org.treetank.utils.TestDocument;
import org.treetank.xpath.XPathAxis;


/**
 * JUnit-test class to test the functionality of the UnionAxis.
 * 
 * @author Tina Scherer
 * 
 */
public class UnionAxisTest {

  public static final String PATH =
      "generated" + File.separator + "UnionAxisTest.tnk";

  @Before
  public void setUp() {
    
    Session.removeSession(PATH);
  }

  
  
  @Test
  public void testUnion() throws IOException {

    // Build simple test tree.
    final ISession session = Session.beginSession(PATH);
    final IWriteTransaction wtx = session.beginWriteTransaction();
    TestDocument.create(wtx);
    IReadTransaction rtx = session.beginReadTransaction(new ItemList());

    
    rtx.moveTo(2L);

    IAxisTest.testIAxisConventions(new XPathAxis(
        rtx, "child::node()/parent::node() union child::node()"), 
        new long[] {2L, 3L, 4L, 7L, 8L, 11L});

    IAxisTest.testIAxisConventions(new XPathAxis(
        rtx, "child::node()/parent::node() | child::node()"), 
        new long[] {2L, 3L, 4L, 7L, 8L, 11L});
    
    IAxisTest.testIAxisConventions(new XPathAxis(
        rtx, "child::node()/parent::node() | child::node() | self::node()"), 
        new long[] {2L, 3L, 4L, 7L, 8L, 11L});
    
    IAxisTest.testIAxisConventions(new XPathAxis(
        rtx, "child::node()/parent::node() | child::node() | self::node()" 
        + "union parent::node()"), 
        new long[] {2L, 3L, 4L, 7L, 8L, 11L, 0L});

    
    IAxisTest.testIAxisConventions(new XPathAxis(
        rtx, "b/preceding::node() union text() | descendant::node()"), 
        new long[] {3L, 7L, 6L, 5L, 4L, 11L, 8L, 9L, 10L});
    
    
    IAxisTest.testIAxisConventions(new XPathAxis(
        rtx, "//c/ancestor::node() | //node()"), 
        new long[] {4L, 2L, 8L, 3L, 7L, 11L, 5L, 6L,  9L, 10L});
    
    rtx.close();
    wtx.abort();
    wtx.close();
    session.close();

  }

}

