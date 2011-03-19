package org.treetank.service.xml.xpath;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import org.treetank.TestHelper;
import org.treetank.TestHelper.PATHS;
import org.treetank.api.IDatabase;
import org.treetank.api.ISession;
import org.treetank.api.IWriteTransaction;
import org.treetank.exception.AbsTTException;
import org.treetank.exception.TTXPathException;
import org.treetank.service.xml.shredder.XMLShredder;
import org.treetank.service.xml.xpath.XPathAxis;

/**
 * Testcase for working with XPath and WriteTransactions
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public final class XPathWriteTransactionTest {

    public static final String XML = "src" + File.separator + "test" + File.separator + "resources"
        + File.separator + "enwiki-revisions-test.xml";

    private ISession session;

    private IWriteTransaction wtx;

    private IDatabase database;

    @Before
    public void setUp() throws Exception {
        TestHelper.deleteEverything();
        // Build simple test tree.
        XMLShredder.main(XML, PATHS.PATH1.getFile().getAbsolutePath());

        // Verify.
        database = TestHelper.getDatabase(PATHS.PATH1.getFile());
        session = database.getSession();
        wtx = session.beginWriteTransaction();
    }

    @Test
    public void test() throws TTXPathException {
        wtx.moveToDocumentRoot();
        // final XPathAxis xpa =
        // new XPathAxis(wtx, "//revision[./parent::page/title/text() = '"
        // + "AmericanSamoa"
        // + "']");
        final XPathAxis xpa = new XPathAxis(wtx, "//revision");
        if (!xpa.hasNext()) {
            fail();
        }

    }

    @After
    public void tearDown() throws AbsTTException {
        // wtx.abort();
        wtx.close();
        session.close();
        database.close();
        TestHelper.closeEverything();
    }

}