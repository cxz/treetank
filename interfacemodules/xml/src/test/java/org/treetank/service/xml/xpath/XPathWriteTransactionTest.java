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

package org.treetank.service.xml.xpath;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import static org.treetank.node.IConstants.ROOT_NODE;

import java.io.File;

import org.treetank.TestHelper;
import org.treetank.TestHelper.PATHS;
import org.treetank.access.NodeWriteTrx;
import org.treetank.access.conf.ResourceConfiguration;
import org.treetank.access.conf.SessionConfiguration;
import org.treetank.api.IDatabase;
import org.treetank.api.INodeWriteTrx;
import org.treetank.api.ISession;
import org.treetank.exception.AbsTTException;
import org.treetank.exception.TTXPathException;
import org.treetank.service.xml.shredder.XMLShredder;

/**
 * Testcase for working with XPath and WriteTransactions
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public final class XPathWriteTransactionTest {

    private static final String XML = "src" + File.separator + "test" + File.separator + "resources"
        + File.separator + "enwiki-revisions-test.xml";

    private static final String RESOURCE = "bla";

    private ISession session;

    private INodeWriteTrx wtx;

    private IDatabase database;

    @BeforeMethod
    public void setUp() throws Exception {
        TestHelper.deleteEverything();
        // Build simple test tree.
        XMLShredder.main(XML, PATHS.PATH1.getFile().getAbsolutePath());

        // Verify.
        database = TestHelper.getDatabase(PATHS.PATH1.getFile());
        database.createResource(new ResourceConfiguration.Builder(RESOURCE, PATHS.PATH1.getConfig()).build());
        session = database.getSession(new SessionConfiguration.Builder(TestHelper.RESOURCE).build());
        wtx = new NodeWriteTrx(session, session.beginPageWriteTransaction());
    }

    @Test
    public void test() throws TTXPathException {
        wtx.moveTo(ROOT_NODE);
        // final XPathAxis xpa =
        // new XPathAxis(wtx, "//revision[./parent::page/title/text() = '"
        // + "AmericanSamoa"
        // + "']");
        final XPathAxis xpa = new XPathAxis(wtx, "//revision");
        if (!xpa.hasNext()) {
            Assert.fail();
        }

    }

    @AfterMethod
    public void tearDown() throws AbsTTException {
        // wtx.abort();
        wtx.close();
        session.close();
        TestHelper.closeEverything();
    }

}
