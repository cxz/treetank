package org.treetank.service.xml.xpath.xmark;

import java.io.File;


import org.treetank.TestHelper;
import org.treetank.TestHelper.PATHS;
import org.treetank.api.IDatabase;
import org.treetank.api.IReadTransaction;
import org.treetank.api.ISession;
import org.treetank.exception.AbsTTException;
import org.treetank.exception.TTXPathException;
import org.treetank.service.xml.shredder.XMLShredder;
import org.treetank.service.xml.xpath.XPathAxis;
import org.treetank.service.xml.xpath.XPathStringChecker;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Performes the XMark benchmark.
 * 
 * @author Patrick Lang
 */
public class XMarkBenchTest {

    final XMarkBenchQueries xmbq = new XMarkBenchQueries();

    private static final String FACTOR = "0.1";
    private static final String XMLFILE = "10mb.xml";

    private static final String XML = "src" + File.separator + "test" + File.separator + "resources"
        + File.separator + XMLFILE;

    private IDatabase database = null;
    private ISession session = null;
    private IReadTransaction rtx = null;

    @BeforeClass
    public static void createDb() {

    }

    @Before
    public void setUp() {
        try {
            TestHelper.deleteEverything();
            XMLShredder.main(XML, PATHS.PATH1.getFile().getAbsolutePath());
            database = TestHelper.getDatabase(PATHS.PATH1.getFile());
            session = database.getSession();
            rtx = session.beginReadTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void xMarkTest_Q1() throws TTXPathException {
        String query = xmbq.getQuery(1, FACTOR);
        String result = xmbq.getResult(1, FACTOR);
        XPathStringChecker.testIAxisConventions(new XPathAxis(rtx, query), new String[] {
            result
        });
    }

    @Test
    public void xMarkTest_Q5() throws TTXPathException {
        String query = xmbq.getQuery(5, FACTOR);
        String result = xmbq.getResult(5, FACTOR);
        XPathStringChecker.testIAxisConventions(new XPathAxis(rtx, query), new String[] {
            result
        });
    }

    @Test
    public void xMarkTest_Q6() throws TTXPathException {
        String query = xmbq.getQuery(6, FACTOR);
        String result = xmbq.getResult(6, FACTOR);
        XPathStringChecker.testIAxisConventions(new XPathAxis(rtx, query), new String[] {
            result
        });
    }

    @Test
    public void xMarkTest_Q7() throws TTXPathException {
        String query = xmbq.getQuery(7, FACTOR);
        String result = xmbq.getResult(7, FACTOR);
        XPathStringChecker.testIAxisConventions(new XPathAxis(rtx, query), new String[] {
            result
        });
    }

    /*
     * @Test
     * public void xMarkTest_Q21() {
     * String query = xmbq.getQuery(21, FACTOR);
     * String result = xmbq.getResult(21, FACTOR);
     * XPathStringChecker.testIAxisConventions(new XPathAxis(rtx, query),
     * new String[] { result });
     * }
     * 
     * @Test
     * public void xMarkTest_Q22() {
     * String query = xmbq.getQuery(22, FACTOR);
     * String result = xmbq.getResult(22, FACTOR);
     * XPathStringChecker.testIAxisConventions(new XPathAxis(rtx, query),
     * new String[] { result });
     * }
     * 
     * @Test
     * public void xMarkTest_Q23() {
     * String query = xmbq.getQuery(23, FACTOR);
     * String result = xmbq.getResult(23, FACTOR);
     * XPathStringChecker.testIAxisConventions(new XPathAxis(rtx, query),
     * new String[] { result });
     * }
     */

    @After
    public void tearDown() {
        try {
            rtx.close();
            session.close();
            database.close();
            TestHelper.closeEverything();
        } catch (AbsTTException e) {
            e.printStackTrace();
        }

    }

}