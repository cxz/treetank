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

import java.io.File;

import org.perfidix.annotation.BenchClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.treetank.Holder;
import org.treetank.TestHelper;
import org.treetank.TestHelper.PATHS;
import org.treetank.exception.TTException;
import org.treetank.service.xml.shredder.XMLShredder;

/**
 * Performes the XMark benchmark.
 * 
 * @author Tina Scherer
 */
@BenchClass(runs = 1)
public class XMarkTest {

    // XMark 1 GB
    private static final String XML = "src" + File.separator + "test" + File.separator + "resources"
        + File.separator + "auction.xml";

    private Holder holder;

    @BeforeMethod
    public void setUp() throws Exception {
        TestHelper.deleteEverything();
        // Build simple test tree.
        XMLShredder.main(XML, PATHS.PATH1.getFile().getAbsolutePath());
        holder = Holder.generateRtx();

    }

    @AfterMethod
    public void tearDown() throws TTException {
        holder.close();
        TestHelper.closeEverything();
    }

    @Test
    public void testQ1_10() throws TTException {
        // Verify.

        XPathStringChecker.testIAxisConventions(holder.getNRtx(), new XPathAxis(holder.getNRtx(),
            "/site/people/person[@id=\"person0\"]/name/text()"), new String[] {
            "Sinisa Farrel"
        });
    }

    @Test
    public void testQ1() throws TTException {

        // Q1 The name of the person with ID 'person0' {projecting}
        XPathStringChecker.testIAxisConventions(holder.getNRtx(), new XPathAxis(holder.getNRtx(),
            "for $b in /site/people/person[@id=\"person0\"] " + "return $b/name/text()"), new String[] {
            "Sinisa Farrel"
        });
    }

    @Test
    public void testQ5() throws TTException {

        // Q5 How many sold items cost more than 40?
        XPathStringChecker.testIAxisConventions(holder.getNRtx(), new XPathAxis(holder.getNRtx(),
            "fn:count(for $i in /site/closed_auctions/closed_auction[price/text() >= 40] "
                + "return $i/price)"), new String[] {
            "75"
        });
    }

    @Test
    public void testQ6() throws TTException {

        // Q6 How many items are listed on all continents?
        XPathStringChecker.testIAxisConventions(holder.getNRtx(), new XPathAxis(holder.getNRtx(),
            "for $b in //site/regions return fn:count($b//item)"), new String[] {
            "217"
        });
    }

    @Test
    public void testQ7() throws TTException {
        // Q7 How many pieces of prose are in our database?
        XPathStringChecker.testIAxisConventions(holder.getNRtx(), new XPathAxis(holder.getNRtx(),
            "for $p in /site return fn:count($p//description) + "
                + "fn:count($p//annotation) + fn:count($p//emailaddress)"), new String[] {
            "916.0"
        }); // TODO: why double?
    }

    // @Test
    // public void testQ8() throws IOException {
    // // Q8 List the names of persons and the number of items they bought
    // // (joins person, closed\_auction)
    // XPathStringChecker.testIAxisConventions(
    // new XPathAxis(rtx, ""),
    // new String[] { "" });
    //
    // }

    // @Test
    // public void testQ9() throws IOException {
    // // // Q9 List the names of persons and the names of the items they bought
    // in
    // // // Europe. (joins person, closed_auction, item)
    // // XPathStringChecker.testIAxisConventions(new XPathAxis(rtx,
    // // ""),
    // // new String[] { "" });
    // }

    //
    // @Test
    // public void testPos() throws IOException {
    // XPathStringChecker.testIAxisConventions(new XPathAxis(rtx,
    // "/site/regions/*/item[2]/@id"), new String[] {"item1", "item6",
    // "item26", "item48", "item108", "item208"});
    // }

}
