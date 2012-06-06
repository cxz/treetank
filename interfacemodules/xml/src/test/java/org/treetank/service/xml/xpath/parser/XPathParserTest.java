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

package org.treetank.service.xml.xpath.parser;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.treetank.Holder;
import org.treetank.TestHelper;
import org.treetank.axis.AbsAxis;
import org.treetank.exception.AbsTTException;
import org.treetank.node.interfaces.IValNode;
import org.treetank.service.xml.xpath.XPathAxis;
import org.treetank.utils.NamePageHash;

public class XPathParserTest {

    private Holder holder;

    @Before
    public void setUp() throws AbsTTException {
        TestHelper.deleteEverything();
        TestHelper.createTestDocument();
        holder = Holder.generateRtx();
    }

    @After
    public void tearDown() throws AbsTTException {
        holder.close();
        TestHelper.deleteEverything();
    }

    @Test
    public void testLiterals() throws AbsTTException {

        holder.getNRtx().moveTo(2L);

        AbsAxis axis;

        axis = new XPathAxis(holder.getNRtx(), "\"12.5\"");
        assertEquals(true, axis.hasNext());
        assertEquals("12.5", new String(((IValNode)axis.getNode()).getRawValue()));
        assertEquals(NamePageHash.generateHashForString("xs:string"), axis.getNode().getTypeKey());
        assertEquals(false, axis.hasNext());

        axis = new XPathAxis(holder.getNRtx(), "\"He said, \"\"I don't like it\"\"\"");
        assertEquals(true, axis.hasNext());
        assertEquals("He said, I don't like it", new String(((IValNode)axis.getNode()).getRawValue()));
        assertEquals(NamePageHash.generateHashForString("xs:string"), axis.getNode().getTypeKey());
        assertEquals(false, axis.hasNext());

        axis = new XPathAxis(holder.getNRtx(), "12");
        assertEquals(true, axis.hasNext());
        assertEquals(NamePageHash.generateHashForString("xs:integer"), axis.getNode().getTypeKey());
        assertEquals("12", new String(((IValNode)axis.getNode()).getRawValue()));
        assertEquals(false, axis.hasNext());

        axis = new XPathAxis(holder.getNRtx(), "12.5");
        assertEquals(true, axis.hasNext());
        assertEquals(NamePageHash.generateHashForString("xs:decimal"), axis.getNode().getTypeKey());
        assertEquals("12.5", new String(((IValNode)axis.getNode()).getRawValue()));
        assertEquals(false, axis.hasNext());

        axis = new XPathAxis(holder.getNRtx(), "12.5E2");
        assertEquals(true, axis.hasNext());
        assertEquals(NamePageHash.generateHashForString("xs:double"), axis.getNode().getTypeKey());
        assertEquals("12.5E2", new String(((IValNode)axis.getNode()).getRawValue()));
        assertEquals(false, axis.hasNext());

        axis = new XPathAxis(holder.getNRtx(), "1");
        assertEquals(true, axis.hasNext());
        assertEquals("1", new String(((IValNode)axis.getNode()).getRawValue()));
        assertEquals(NamePageHash.generateHashForString("xs:integer"), axis.getNode().getTypeKey());
        assertEquals(false, axis.hasNext());

    }

    @Test
    public void testEBNF() throws AbsTTException {

        XPathParser parser = new XPathParser(holder.getNRtx(), "/p:a");
        parser.parseQuery();

        parser = new XPathParser(holder.getNRtx(), "/p:a/node(), /b/descendant-or-self::adsfj");
        parser.parseQuery();

        parser = new XPathParser(holder.getNRtx(), "for $i in /p:a return $i");
        parser.parseQuery();

        parser = new XPathParser(holder.getNRtx(), "for $i in /p:a return /p:a");
        parser.parseQuery();

        parser = new XPathParser(holder.getNRtx(), "child::element(person)");
        parser.parseQuery();

        parser = new XPathParser(holder.getNRtx(), "child::element(person, xs:string)");
        parser.parseQuery();

        parser = new XPathParser(holder.getNRtx(), " child::element(*, xs:string)");
        parser.parseQuery();

        parser = new XPathParser(holder.getNRtx(), "child::element()");
        parser.parseQuery();

        // parser = new XPathParser(holder.getRtx(), ". treat as item()");
        // parser.parseQuery();

        parser = new XPathParser(holder.getNRtx(), "/b instance of item()");
        parser.parseQuery();

    }

}
