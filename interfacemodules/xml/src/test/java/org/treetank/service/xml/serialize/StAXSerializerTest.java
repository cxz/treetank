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

package org.treetank.service.xml.serialize;

import static org.testng.AssertJUnit.assertEquals;
import static org.treetank.node.IConstants.ROOT_NODE;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.treetank.CoreTestHelper;
import org.treetank.Holder;
import org.treetank.ModuleFactory;
import org.treetank.NodeTestHelper;
import org.treetank.access.NodeReadTrx;
import org.treetank.access.conf.ResourceConfiguration;
import org.treetank.access.conf.ResourceConfiguration.IResourceConfigurationFactory;
import org.treetank.access.conf.StandardSettings;
import org.treetank.api.INodeReadTrx;
import org.treetank.axis.DescendantAxis;
import org.treetank.exception.TTException;
import org.treetank.service.xml.serialize.XMLSerializer.XMLSerializerBuilder;

import com.google.inject.Inject;

/**
 * Test StAXSerializer.
 * 
 * @author Johannes Lichtenberger, University of Konstanz.
 */
@Guice(moduleFactory = ModuleFactory.class)
public class StAXSerializerTest {
    private Holder holder;

    @Inject
    private IResourceConfigurationFactory mResourceConfig;

    private ResourceConfiguration mResource;

    @BeforeMethod
    public void setUp() throws TTException {
        CoreTestHelper.deleteEverything();
        CoreTestHelper.Holder holder = CoreTestHelper.Holder.generateStorage();
        Properties props =
            StandardSettings.getProps(CoreTestHelper.PATHS.PATH1.getFile()
                .getAbsolutePath(), CoreTestHelper.RESOURCENAME);
        mResource = mResourceConfig.create(props);
        NodeTestHelper.createTestDocument(mResource);
        this.holder = Holder.generateWtx(holder, mResource);
    }

    @AfterMethod
    public void tearDown() throws TTException {
        CoreTestHelper.deleteEverything();
    }

    @Test
    public void testStAXSerializer() throws TTException, XMLStreamException {

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final XMLSerializerBuilder builder = new XMLSerializerBuilder(holder.getSession(), out);
        builder.setDeclaration(false);
        final XMLSerializer xmlSerializer = builder.build();
        xmlSerializer.call();

        final INodeReadTrx rtx =
            new NodeReadTrx(holder.getSession().beginPageReadTransaction(
                holder.getSession().getMostRecentVersion()));
        StAXSerializer serializer = new StAXSerializer(new DescendantAxis(rtx), rtx);
        final StringBuilder strBuilder = new StringBuilder();
        boolean isEmptyElement = false;

        while (serializer.hasNext()) {
            XMLEvent event = serializer.nextEvent();

            switch (event.getEventType()) {
            case XMLStreamConstants.START_DOCUMENT:
                strBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
                break;
            case XMLStreamConstants.START_ELEMENT:
                emitElement(event, strBuilder);

                if (serializer.peek().getEventType() == XMLStreamConstants.END_ELEMENT) {
                    strBuilder.append("/>");
                    isEmptyElement = true;
                } else {
                    strBuilder.append('>');
                }
                break;
            case XMLStreamConstants.END_ELEMENT:
                if (isEmptyElement) {
                    isEmptyElement = false;
                } else {
                    emitQName(true, event, strBuilder);
                    strBuilder.append('>');
                }
                break;
            case XMLStreamConstants.CHARACTERS:
                strBuilder.append(((Characters)event).getData());
                break;
            }
        }

        assertEquals(out.toString(), strBuilder.toString());

        // Check getElementText().
        // ========================================================
        holder.getNRtx().moveTo(ROOT_NODE);
        serializer = new StAXSerializer(new DescendantAxis(rtx), rtx);
        String elemText = null;

        // <p:a>
        if (serializer.hasNext()) {
            serializer.next();
            elemText = serializer.getElementText();
        }
        assertEquals("oops1foooops2baroops3", elemText);

        // oops1
        checkForException(serializer);

        // <b>
        if (serializer.hasNext()) {
            serializer.next();
            elemText = serializer.getElementText();
        }
        assertEquals("foo", elemText);

        // foo
        checkForException(serializer);

        // <c>
        if (serializer.hasNext()) {
            serializer.next();
            elemText = serializer.getElementText();
        }
        assertEquals("", elemText);

        // </c>
        checkForException(serializer);

        // </b>
        checkForException(serializer);

        // oops2
        checkForException(serializer);

        // <b p:x='y'>
        if (serializer.hasNext()) {
            serializer.next();
            elemText = serializer.getElementText();
        }
        assertEquals("bar", elemText);

        // <c>
        if (serializer.hasNext()) {
            serializer.next();
            elemText = serializer.getElementText();
        }
        assertEquals("", elemText);

        // </c>
        checkForException(serializer);

        // bar
        checkForException(serializer);

        // </b>
        checkForException(serializer);

        // oops3
        checkForException(serializer);

        // </p:a>
        checkForException(serializer);
        rtx.close();
    }

    /**
     * Checks for an XMLStreamException if the current event isn't a start tag.
     * Used for testing getElementText().
     * 
     * @param serializer
     *            {@link StAXSerializer}
     */
    private void checkForException(final StAXSerializer serializer) {
        String elemText = "";
        try {
            if (serializer.hasNext()) {
                serializer.next();
                elemText = serializer.getElementText();
            }
            Assert.fail("");
        } catch (final XMLStreamException e) {
            assertEquals("", elemText);
        }
    }

    /**
     * Emit an element.
     * 
     * @param event
     *            {@link XMLEvent}, either a start tag or an end tag.
     * @param strBuilder
     *            String builder to build the string representation.
     */
    @Test(enabled = false)
    private void emitElement(final XMLEvent event, final StringBuilder strBuilder) {
        emitQName(true, event, strBuilder);

        if (event.isStartElement()) {
            final StartElement elem = ((StartElement)event);
            // Parse namespaces.
            for (Iterator<?> it = elem.getNamespaces(); it.hasNext();) {
                final Namespace namespace = (Namespace)it.next();

                if ("".equals(namespace.getPrefix())) {
                    strBuilder.append(" xmlns=\"").append(namespace.getNamespaceURI()).append("\"");
                } else {
                    strBuilder.append(" xmlns:").append(namespace.getPrefix()).append("=\"").append(
                        namespace.getNamespaceURI()).append("\"");
                }
            }

            // Parse attributes.
            for (Iterator<?> it = elem.getAttributes(); it.hasNext();) {
                final Attribute attribute = (Attribute)it.next();
                emitQName(false, attribute, strBuilder);
                strBuilder.append("=\"").append(attribute.getValue()).append("\"");
            }
        }
    }

    /**
     * Emit a qualified name.
     * 
     * @param event
     *            {@link XMLEvent}, either a start tag or an end tag.
     * @param strBuilder
     *            String builder to build the string representation.
     * @param isElem
     *            Determines if it is an element or an attribute.
     */
    @Test(enabled = false)
    private void emitQName(final boolean isElem, final XMLEvent event, final StringBuilder strBuilder) {
        QName qName;
        if (isElem) {
            if (event.isStartElement()) {
                strBuilder.append('<');
                qName = ((StartElement)event).getName();
            } else {
                strBuilder.append("</");
                qName = ((EndElement)event).getName();
            }
        } else {
            qName = ((Attribute)event).getName();
        }

        if (!isElem) {
            strBuilder.append(' ');
        }

        if (qName.getPrefix() == "") {
            strBuilder.append(qName.getLocalPart());
        } else {
            strBuilder.append(qName.getPrefix() + ':' + qName.getLocalPart());
        }
    }
}