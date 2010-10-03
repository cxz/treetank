/**
 * Copyright (c) 2010, Distributed Systems Group, University of Konstanz
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED AS IS AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 */
package com.treetank.wikipedia.hadoop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.treetank.utils.LogWrapper;

/**
 * <h1>TestXSLTTransformation</h1>
 * 
 * <p>
 * Tests the grouping of revisions in one page, which have the same timestamp.
 * </p>
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public final class TestXSLTTransformation {

    /**
     * Log wrapper for better output.
     */
    private static final LogWrapper LOGWRAPPER = new LogWrapper(LoggerFactory.getLogger(XMLReduce.class));

    /** Input XML file. */
    private static final String INPUT =
        "src" + File.separator + "test" + File.separator + "resources" + File.separator + "testInput.xml";

    /** Path to stylesheet for XSLT transformation. */
    private static final String STYLESHEET =
        "src" + File.separator + "main" + File.separator + "resources" + File.separator + "wikipedia.xsl";

    /** Default Constructor. */
    public TestXSLTTransformation() {
        // To make Checkstyle happy.
    }

//    @Before
//    public void setUp() throws FileNotFoundException {
//    }

    @Test
    public void testTransform() throws FileNotFoundException {
        final Processor proc = new Processor(false);
        final XsltCompiler compiler = proc.newXsltCompiler();
        try {
            final XsltExecutable exec = compiler.compile(new StreamSource(new File(STYLESHEET)));
            final XsltTransformer transform = exec.load();
            transform.setSource(new StreamSource(new FileInputStream(INPUT)));
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final Serializer serializer = new Serializer();
            serializer.setOutputStream(System.out);
            transform.setDestination(serializer);
//            System.out.println(out.toString());
        } catch (final SaxonApiException e) {
            LOGWRAPPER.error(e);
        }
    }
}
