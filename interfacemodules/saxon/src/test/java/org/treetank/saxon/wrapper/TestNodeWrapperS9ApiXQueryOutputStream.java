/**
 * 
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

package org.treetank.saxon.wrapper;

import static org.testng.AssertJUnit.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.treetank.Holder;
import org.treetank.TestHelper;
import org.treetank.exception.TTException;
import org.treetank.saxon.evaluator.XQueryEvaluatorOutputStream;

/**
 * Test XQuery S9Api.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public final class TestNodeWrapperS9ApiXQueryOutputStream {

    /** Treetank database on books document. */
    private transient Holder mHolder;

    @BeforeMethod
    public void setUp() throws Exception {
        SaxonHelper.createBookDB();
        mHolder = Holder.generateSession();
    }

    @AfterMethod
    public void tearDown() throws TTException {
        TestHelper.closeEverything();
        TestHelper.deleteEverything();
    }

    @Test
    public void testWhereBooks() throws Exception {
        final OutputStream out = new ByteArrayOutputStream();
        new XQueryEvaluatorOutputStream("for $x in /bookstore/book where $x/price>30 return $x/title",
            mHolder.getSession(), out).call();
        final String result = out.toString();
        assertEquals("<title lang=\"en\">XQuery Kick Start</title><title lang=\"en\">Learning XML</title>",
            result);
    }

    @Test
    public void testOrderByBooks() throws Exception {
        final OutputStream out = new ByteArrayOutputStream();
        new XQueryEvaluatorOutputStream(
            "for $x in /bookstore/book where $x/price>30 order by $x/title return $x/title", mHolder
                .getSession(), out).call();
        final String result = out.toString();
        assertEquals("<title lang=\"en\">Learning XML</title><title lang=\"en\">XQuery Kick Start</title>",
            result);
    }

    @Test
    public void testFLOWR() throws Exception {
        final OutputStream out = new ByteArrayOutputStream();
        new XQueryEvaluatorOutputStream(
            "for $x in /bookstore/book let $y := $x/price where $y>30 order by $x/title return $x/title",
            mHolder.getSession(), out).call();
        final String result = out.toString();
        assertEquals("<title lang=\"en\">Learning XML</title><title lang=\"en\">XQuery Kick Start</title>",
            result);
    }

}
