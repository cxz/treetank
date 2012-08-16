/**
 * Copyright (c) 2011, University of Konstanz, Distributed Systems Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the University of Konstanz nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.treetank.service.xml.shredder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.treetank.NodeModuleFactory;
import org.treetank.TestHelper;
import org.treetank.TestHelper.PATHS;
import org.treetank.access.NodeWriteTrx;
import org.treetank.access.NodeWriteTrx.HashKind;
import org.treetank.access.conf.ResourceConfiguration.IResourceConfigurationFactory;
import org.treetank.access.conf.SessionConfiguration;
import org.treetank.access.conf.StandardSettings;
import org.treetank.api.IDatabase;
import org.treetank.api.INodeWriteTrx;
import org.treetank.api.ISession;
import org.treetank.exception.TTException;
import org.treetank.service.xml.XMLTestHelper;
import org.treetank.service.xml.serialize.XMLSerializer;
import org.treetank.service.xml.serialize.XMLSerializer.XMLSerializerBuilder;

import com.google.inject.Inject;

/**
 * Test XMLUpdateShredder.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 */

@Guice(moduleFactory = NodeModuleFactory.class)
public final class XMLUpdateShredderTest extends XMLTestCase {
    private static final String RESOURCES = "src" + File.separator + "test" + File.separator + "resources";

    private static final String XMLINSERTFIRST = RESOURCES + File.separator + "revXMLsInsert";

    private static final String XMLINSERTSECOND = RESOURCES + File.separator + "revXMLsInsert1";

    private static final String XMLINSERTTHIRD = RESOURCES + File.separator + "revXMLsInsert2";

    private static final String XMLDELETEFIRST = RESOURCES + File.separator + "revXMLsDelete";

    private static final String XMLDELETESECOND = RESOURCES + File.separator + "revXMLsDelete1";

    private static final String XMLDELETETHIRD = RESOURCES + File.separator + "revXMLsDelete2";

    private static final String XMLDELETEFOURTH = RESOURCES + File.separator + "revXMLsDelete3";

    private static final String XMLSAME = RESOURCES + File.separator + "revXMLsSame";

    private static final String XMLALLFIRST = RESOURCES + File.separator + "revXMLsAll";

    private static final String XMLALLSECOND = RESOURCES + File.separator + "revXMLsAll1";

    private static final String XMLALLTHIRD = RESOURCES + File.separator + "revXMLsAll2";

    private static final String XMLALLFOURTH = RESOURCES + File.separator + "revXMLsAll3";

    private static final String XMLALLFIFTH = RESOURCES + File.separator + "revXMLsAll4";

    private static final String XMLALLSIXTH = RESOURCES + File.separator + "revXMLsAll5";

    private static final String XMLALLSEVENTH = RESOURCES + File.separator + "revXMLsAll6";

    private static final String XMLALLEIGHTH = RESOURCES + File.separator + "revXMLsAll7";

    private static final String XMLALLNINETH = RESOURCES + File.separator + "revXMLsAll8";

    // private static final String XMLLINGUISTICS = RESOURCES + File.separator +
    // "linguistics";

    static {
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreWhitespace(true);
    }

    @Inject
    private IResourceConfigurationFactory mResourceConfig;

    @BeforeMethod
    public void setUp() throws TTException {
        TestHelper.deleteEverything();
    }

    @AfterMethod
    public void tearDown() throws TTException {
        TestHelper.deleteEverything();
    }

    @Test
    public void testSame() throws Exception {
        check(XMLSAME);
    }

    @Test
    public void testInsertsFirst() throws Exception {
        check(XMLINSERTFIRST);
    }

    @Test
    public void testInsertsSecond() throws Exception {
        check(XMLINSERTSECOND);
    }

    @Test
    public void testInsertsThird() throws Exception {
        check(XMLINSERTTHIRD);
    }

    @Test
    public void testDeletesFirst() throws Exception {
        check(XMLDELETEFIRST);
    }

    @Test
    public void testDeletesSecond() throws Exception {
        check(XMLDELETESECOND);
    }

    @Test
    public void testDeletesThird() throws Exception {
        check(XMLDELETETHIRD);
    }

    @Test
    public void testDeletesFourth() throws Exception {
        check(XMLDELETEFOURTH);
    }

    @Test
    public void testAllFirst() throws Exception {
        check(XMLALLFIRST);
    }

    @Test
    public void testAllSecond() throws Exception {
        check(XMLALLSECOND);
    }

    @Test
    public void testAllThird() throws Exception {
        check(XMLALLTHIRD);
    }

    @Test
    public void testAllFourth() throws Exception {
        check(XMLALLFOURTH);
    }

    @Test
    public void testAllFifth() throws Exception {
        check(XMLALLFIFTH);
    }

    @Test
    public void testAllSixth() throws Exception {
        check(XMLALLSIXTH);
    }

    @Test
    public void testAllSeventh() throws Exception {
        check(XMLALLSEVENTH);
    }

    @Test
    public void testAllEighth() throws Exception {
        check(XMLALLEIGHTH);
    }

    @Test
    public void testAllNineth() throws Exception {
        check(XMLALLNINETH);
    }

    // @Test
    // public void testLinguistics() throws Exception {
    // test(XMLLINGUISTICS);
    // }

    private void check(final String FOLDER) throws Exception {
        final IDatabase database = TestHelper.getDatabase(TestHelper.PATHS.PATH1.getFile());
        database.createResource(mResourceConfig.create(PATHS.PATH1.getFile(), TestHelper.RESOURCENAME, 1));
        final ISession session =
            database.getSession(new SessionConfiguration(TestHelper.RESOURCENAME, StandardSettings.KEY));
        final File folder = new File(FOLDER);
        final File[] filesList = folder.listFiles();
        final List<File> list = new ArrayList<File>();
        for (final File file : filesList) {
            if (file.getName().endsWith(".xml")) {
                list.add(file);
            }
        }

        // Sort files array according to file names.
        Collections.sort(list, new Comparator<Object>() {
            @Override
            public int compare(final Object paramFirst, final Object paramSecond) {
                final String firstName =
                    ((File)paramFirst).getName().toString().substring(0,
                        ((File)paramFirst).getName().toString().indexOf('.'));
                final String secondName =
                    ((File)paramSecond).getName().toString().substring(0,
                        ((File)paramSecond).getName().toString().indexOf('.'));
                if (Integer.parseInt(firstName) < Integer.parseInt(secondName)) {
                    return -1;
                } else if (Integer.parseInt(firstName) > Integer.parseInt(secondName)) {
                    return +1;
                } else {
                    return 0;
                }
            }
        });

        boolean first = true;

        // Shredder files.
        for (final File file : list) {
            if (file.getName().endsWith(".xml")) {
                final INodeWriteTrx wtx =
                    new NodeWriteTrx(session, session.beginPageWriteTransaction(), HashKind.Rolling);
                if (first) {
                    final XMLShredder shredder =
                        new XMLShredder(wtx, XMLShredder.createFileReader(file),
                            EShredderInsert.ADDASFIRSTCHILD);
                    shredder.call();
                    first = false;
                } else {
                    final XMLShredder shredder =
                        new XMLUpdateShredder(wtx, XMLShredder.createFileReader(file),
                            EShredderInsert.ADDASFIRSTCHILD, file, EShredderCommit.COMMIT);
                    shredder.call();
                }

                final OutputStream out = new ByteArrayOutputStream();
                final XMLSerializer serializer = new XMLSerializerBuilder(session, out).build();
                serializer.call();
                final StringBuilder sBuilder = XMLTestHelper.readFile(file.getAbsoluteFile(), false);

                // System.out.println(out.toString());
                final Diff diff = new Diff(sBuilder.toString(), out.toString());
                // final DetailedDiff detDiff = new DetailedDiff(diff);
                // @SuppressWarnings("unchecked")
                // final List<Difference> differences =
                // detDiff.getAllDifferences();
                // for (final Difference difference : differences) {
                // // System.out.println("***********************");
                // // System.out.println(difference);
                // // System.out.println("***********************");
                // }

                AssertJUnit.assertTrue("pieces of XML are similar " + diff, diff.similar());
                AssertJUnit.assertTrue("but are they identical? " + diff, diff.identical());
                wtx.close();
            }
        }
    }
}
