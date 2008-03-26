package org.treetank.axislayer;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.treetank.api.ISession;
import org.treetank.api.IWriteTransaction;
import org.treetank.sessionlayer.Session;
import org.treetank.utils.TestDocument;

public class CommentFilterTest {

  public static final String PATH =
      "target"
          + File.separator
          + "tnk"
          + File.separator
          + "CommentFilterTest.tnk";

  @Before
  public void setUp() {
    Session.removeSession(PATH);
  }

  @Test(expected = IllegalStateException.class)
  public void testIFilterConvetions() {

    // Build simple test tree.
    final ISession session = Session.beginSession(PATH);
    final IWriteTransaction wtx = session.beginWriteTransaction();
    TestDocument.create(wtx);

    wtx.moveTo(8L);
    IFilterTest.testIFilterConventions(new CommentFilter(wtx), false);

    wtx.moveTo(3L);
    IFilterTest.testIFilterConventions(new CommentFilter(wtx), false);

    wtx.moveTo(2L);
    wtx.moveToAttribute(0);
    IFilterTest.testIFilterConventions(new CommentFilter(wtx), true);

    wtx.moveTo(8L);
    wtx.moveToAttribute(0);
    IFilterTest.testIFilterConventions(new CommentFilter(wtx), true);

    wtx.abort();
    wtx.close();
    session.close();

  }

}