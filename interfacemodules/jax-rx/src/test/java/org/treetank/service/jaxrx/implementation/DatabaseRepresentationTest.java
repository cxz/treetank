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

package org.treetank.service.jaxrx.implementation;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.treetank.CoreTestHelper.RESOURCENAME;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jaxrx.core.JaxRxException;
import org.jaxrx.core.QueryParameter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.treetank.CoreTestHelper;
import org.treetank.ModuleFactory;
import org.treetank.exception.TTException;
import org.treetank.io.IBackend.IBackendFactory;
import org.treetank.revisioning.IRevisioning;
import org.treetank.service.jaxrx.util.DOMHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.inject.Inject;

/**
 * This class is responsible to test the implementation class <code> DatabaseRepresentation</code>;
 * 
 * @author Patrick Lang, Lukas Lewandowski, University of Konstanz
 * 
 * 
 */

@Guice(moduleFactory = ModuleFactory.class)
public class DatabaseRepresentationTest {

    /**
     * Check message for JUnit test: assertTrue
     */
    private final static transient String ASSTRUE = "check if true";

    /**
     * Check message for JUnit test: assertEquals
     */
    private final static transient String ASSEQUALS = "check if equals";

    /**
     * Treetank reference.
     */
    private static transient DatabaseRepresentation treetank;

    /**
     * Instances xml file static variable
     */
    private static final transient String XMLFILE = "/factbook.xml";

    /**
     * Instances literal true static variable
     */
    private static final transient String LITERALTRUE = "yes";

    /**
     * The name of the result node
     */
    private static final transient String RESULTNAME = "jaxrx:result";
    /**
     * The name of the id attribute
     */
    private static final transient String IDNAME = "rest:ttid";

    /**
     * The name of the country node
     */
    private static final transient String NAME = "name";

    @Inject
    public IBackendFactory mStorageFac;

    @Inject
    public IRevisioning mRevisioning;

    /**
     * This a simple setUp.
     * 
     * @throws TTException
     */
    @BeforeMethod
    public void setUp() throws TTException {
        CoreTestHelper.deleteEverything();
        final InputStream input = DatabaseRepresentationTest.class.getResourceAsStream(XMLFILE);
        treetank =
            new DatabaseRepresentation(CoreTestHelper.getStorage(CoreTestHelper.PATHS.PATH1.getFile()),
                mStorageFac, mRevisioning);
        treetank.shred(input, RESOURCENAME);
    }

    /**
     * This is a simple tear down.
     * 
     * @throws TTException
     */
    @AfterMethod
    public void tearDown() throws TTException {
        CoreTestHelper.deleteEverything();
    }

    /**
     * This method tests {@link DatabaseRepresentation#createResource(java.io.InputStream,String)}
     */
    @Test
    public void createResource() {
        final InputStream input = DatabaseRepresentationTest.class.getResourceAsStream(XMLFILE);
        treetank.createResource(input, RESOURCENAME);
        assertNotNull("check if resource has been created", treetank.getResource(RESOURCENAME, null));
    }

    /**
     * This method tests {@link DatabaseRepresentation#createResource(java.io.InputStream,String)}
     */
    @Test(expectedExceptions = JaxRxException.class)
    public void createResourceExc() {
        treetank.createResource(null, RESOURCENAME);
    }

    /**
     * This method tests {@link DatabaseRepresentation#getResource(String, java.util.Map)}
     * 
     * @throws TTException
     * @throws IOException
     * @throws WebApplicationException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test
    public void getResource() throws TTException, WebApplicationException, IOException,
        ParserConfigurationException, SAXException {
        final Map<QueryParameter, String> queryParams = new HashMap<QueryParameter, String>();
        Document doc;
        Node node;
        Node resultNode;
        Attr attribute;
        StreamingOutput sOutput = treetank.getResource(RESOURCENAME, queryParams);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        sOutput.write(output);
        doc = DOMHelper.buildDocument(output);
        node = doc.getElementsByTagName("mondial").item(0);
        attribute = (Attr)node.getAttributes().getNamedItem(IDNAME);
        resultNode = doc.getElementsByTagName(RESULTNAME).item(0);
        assertNotNull("mondial does exist", node);
        assertNull("test if result node exists - null", resultNode);
        assertNull("test if id element exists - null", attribute);
        output.close();

        queryParams.put(QueryParameter.WRAP, LITERALTRUE);
        sOutput = treetank.getResource(RESOURCENAME, queryParams);
        output = new ByteArrayOutputStream();
        sOutput.write(output);
        doc = DOMHelper.buildDocument(output);
        node = doc.getElementsByTagName("country").item(0);
        attribute = (Attr)node.getAttributes().getNamedItem(IDNAME);
        resultNode = doc.getElementsByTagName(RESULTNAME).item(0);
        assertNotNull("test if country exists", node);
        assertNotNull("test if result node exists", resultNode);
        assertNull("test if id element exists", attribute);
        output.close();

        queryParams.put(QueryParameter.OUTPUT, LITERALTRUE);
        sOutput = treetank.getResource(RESOURCENAME, queryParams);
        output = new ByteArrayOutputStream();
        sOutput.write(output);
        doc = DOMHelper.buildDocument(output);
        node = doc.getElementsByTagName(NAME).item(0);
        attribute = (Attr)node.getAttributes().getNamedItem(IDNAME);
        resultNode = doc.getElementsByTagName(RESULTNAME).item(0);
        assertNotNull("test if country exists2", node);
        assertNotNull("test if result node exists2", resultNode);
        assertNotNull("test if id element exists2", attribute);
        output.close();

        sOutput = treetank.getResource(RESOURCENAME, queryParams);
        output = new ByteArrayOutputStream();
        sOutput.write(output);
        doc = DOMHelper.buildDocument(output);
        node = doc.getElementsByTagName("city").item(0);
        attribute = (Attr)node.getAttributes().getNamedItem(IDNAME);
        resultNode = doc.getElementsByTagName(RESULTNAME).item(0);
        assertNotNull("test if city exists2", node);
        assertNotNull("test if result node exists2", resultNode);
        assertNotNull("test if id element exists2", attribute);
        output.close();

    }

    /**
     * This method tests {@link DatabaseRepresentation#getResourcesNames()}
     * 
     * @throws TTExceptions
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test
    public void getResourcesNames() throws TTException, ParserConfigurationException, SAXException,
        IOException {

        final StreamingOutput sOutput = treetank.getResourcesNames();
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        sOutput.write(output);
        final Document doc = DOMHelper.buildDocument(output);
        final NodeList nodes = doc.getElementsByTagName("resource");
        assertTrue("Check if a resource exists", nodes.getLength() > 0);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Attr attribute = (Attr)node.getAttributes().getNamedItem("lastRevision");
            assertNotNull("Check if lastRevision exists", attribute);
            attribute = (Attr)node.getAttributes().getNamedItem("name");
            assertNotNull("Check if name attribute exists", attribute);
            assertTrue("Check if name is the expected one", attribute.getTextContent().equals(RESOURCENAME)
                || attribute.getTextContent().equals(RESOURCENAME));
        }
        output.close();
    }

    /**
     * This method tests {@link DatabaseRepresentation#add(InputStream, String)}
     * 
     * @throws IOException
     * @throws WebApplicationException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws TTException
     * 
     */
    @Test
    public void addResource() throws WebApplicationException, IOException, ParserConfigurationException,
        SAXException, TTException {
        final InputStream input = DatabaseRepresentationTest.class.getResourceAsStream("/books.xml");
        treetank.add(input, RESOURCENAME);
        final Map<QueryParameter, String> params = new HashMap<QueryParameter, String>();
        params.put(QueryParameter.WRAP, LITERALTRUE);
        final StreamingOutput sOutput = treetank.performQueryOnResource(RESOURCENAME, ".", params);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        sOutput.write(output);
        final Document doc = DOMHelper.buildDocument(output);
        Node node = doc.getElementsByTagName("books").item(0);
        assertNotNull("check if books has been added to factbook", node);
        node = doc.getElementsByTagName("mondial").item(0);
        assertNotNull("check if mondial still exists", node);
        output.close();
    }

    /**
     * This method tests {@link DatabaseRepresentation#deleteResource(String)}
     * 
     * @throws TTException
     * @throws IOException
     * @throws WebApplicationException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test
    public void deleteResource() throws TTException, WebApplicationException, IOException,
        ParserConfigurationException, SAXException {
        final InputStream input = DatabaseRepresentationTest.class.getResourceAsStream(XMLFILE);
        treetank.shred(input, RESOURCENAME + "99");
        treetank.deleteResource(RESOURCENAME + "99");
        final StreamingOutput sOutput = treetank.getResourcesNames();
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        sOutput.write(output);
        final Document doc = DOMHelper.buildDocument(output);
        final NodeList nodes = doc.getElementsByTagName("resource");
        String searchName = null;
        for (int i = 0; i < nodes.getLength(); i++) {
            final Attr attribute = (Attr)nodes.item(i).getAttributes().getNamedItem(NAME);
            if (attribute.getTextContent().equals(RESOURCENAME + "99")) {
                searchName = attribute.getTextContent();
                break;
            }
        }
        assertNull("Check if the resource has been deleted", searchName);
        output.close();
    }

    /**
     * This method tests {@link DatabaseRepresentation#shred(java.io.InputStream, String)}
     * 
     * @throws TTException
     */
    @Test
    public void shred() throws TTException {
        final InputStream input = DatabaseRepresentationTest.class.getResourceAsStream(XMLFILE);
        assertTrue(ASSTRUE, treetank.shred(input, RESOURCENAME + "88"));
        treetank.deleteResource(RESOURCENAME + "88");
    }

    /**
     * This method tests {@link DatabaseRepresentation#performQueryOnResource(String, String, Map)}
     * 
     * @throws IOException
     * @throws WebApplicationException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    @Test
    public void performQueryOnResource() throws WebApplicationException, IOException,
        ParserConfigurationException, SAXException {

        final Map<QueryParameter, String> params = new HashMap<QueryParameter, String>();
        params.put(QueryParameter.OUTPUT, LITERALTRUE);
        params.put(QueryParameter.WRAP, LITERALTRUE);
        final StreamingOutput sOutput = treetank.performQueryOnResource(RESOURCENAME, "//continent", params);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        sOutput.write(output);
        final Document doc = DOMHelper.buildDocument(output);
        Node node = doc.getElementsByTagName("continent").item(0);
        assertNotNull("check if continent exists", node);
        node = doc.getElementsByTagName("country").item(0);
        assertNull("check for null country object", node);
        output.close();
    }

    /**
     * This method tests {@link DatabaseRepresentation#getLastRevision(String)}
     * 
     * @throws TTException
     */
    @Test
    public void getLastRevision() throws TTException {
        assertEquals(ASSEQUALS, 1, treetank.getLastRevision(RESOURCENAME));
        final NodeIdRepresentation rid =
            new NodeIdRepresentation(CoreTestHelper.getStorage(CoreTestHelper.PATHS.PATH1.getFile()));
        rid.deleteResource(RESOURCENAME, 8);
        assertEquals(ASSEQUALS, 2, treetank.getLastRevision(RESOURCENAME));
    }

    /**
     * This method tests
     * {@link DatabaseRepresentation#getModificHistory(String, String, boolean, java.io.OutputStream, boolean)}
     * 
     * @throws TTException
     * @throws WebApplicationException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    @Test
    public void getModificHistory() throws WebApplicationException, TTException, SAXException, IOException,
        ParserConfigurationException {
        final NodeIdRepresentation rid =
            new NodeIdRepresentation(CoreTestHelper.getStorage(CoreTestHelper.PATHS.PATH1.getFile()));
        rid.deleteResource(RESOURCENAME, 8);
        final OutputStream output = new ByteArrayOutputStream();
        treetank.getModificHistory(RESOURCENAME, "1-2", false, output, true);
        final InputStream inpSt = new ByteArrayInputStream(((ByteArrayOutputStream)output).toByteArray());
        final Document doc = xmlDocument(inpSt);
        final NodeList nodes = doc.getElementsByTagName("continent");
        final int changeditems = nodes.getLength();
        assertEquals(ASSEQUALS, 1, changeditems);
    }

    /**
     * This method tests {@link DatabaseRepresentation#revertToRevision(String, long)}
     * 
     * @throws TTException
     * @throws IOException
     * @throws WebApplicationException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws InterruptedException
     */
    @Test
    public void revertToRevision() throws TTException, WebApplicationException, IOException,
        ParserConfigurationException, SAXException, InterruptedException {
        final NodeIdRepresentation rid =
            new NodeIdRepresentation(CoreTestHelper.getStorage(CoreTestHelper.PATHS.PATH1.getFile()));
        rid.deleteResource(RESOURCENAME, 8);
        rid.deleteResource(RESOURCENAME, 11);
        rid.deleteResource(RESOURCENAME, 14);
        assertEquals(ASSEQUALS, 4, treetank.getLastRevision(RESOURCENAME));
        treetank.revertToRevision(RESOURCENAME, 1);
        final StreamingOutput sOutput =
            rid.getResource(RESOURCENAME, 14, new HashMap<QueryParameter, String>());
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        sOutput.write(output);
        final Document doc = DOMHelper.buildDocument(output);
        final Node node = doc.getElementsByTagName("continent").item(0);
        final Attr attribute = (Attr)node.getAttributes().getNamedItem(NAME);
        final String africaString = attribute.getTextContent();
        assertNotNull("check if africa (14) exists in the latest version", africaString);
        assertEquals(ASSEQUALS, 5, treetank.getLastRevision(RESOURCENAME));
        output.close();
    }

    /**
     * This method creates of an input stream an XML document.
     * 
     * @param input
     *            The input stream.
     * @return The packed XML document.
     * @throws SAXException
     *             Exception occurred.
     * @throws IOException
     *             Exception occurred.
     * @throws ParserConfigurationException
     *             Exception occurred.
     */
    private Document xmlDocument(final InputStream input) throws SAXException, IOException,
        ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
    }

}
