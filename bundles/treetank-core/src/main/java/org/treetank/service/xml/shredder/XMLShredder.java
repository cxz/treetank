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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.LoggerFactory;
import org.treetank.access.DatabaseConfiguration;
import org.treetank.access.FileDatabase;
import org.treetank.access.SessionConfiguration;
import org.treetank.access.WriteTransaction;
import org.treetank.api.IDatabase;
import org.treetank.api.ISession;
import org.treetank.api.IWriteTransaction;
import org.treetank.exception.AbsTTException;
import org.treetank.exception.TTIOException;
import org.treetank.exception.TTUsageException;
import org.treetank.node.ENodes;
import org.treetank.node.ElementNode;
import org.treetank.settings.EFixed;
import org.treetank.utils.FastStack;
import org.treetank.utils.TypedValue;

/**
 * This class appends a given {@link XMLStreamReader} to a {@link IWriteTransaction}. The content of the
 * stream is added as a subtree.
 * Based on a boolean which identifies the point of insertion, the subtree is
 * either added as subtree or as rightsibling.
 * 
 * @author Marc Kramis, Seabix
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class XMLShredder implements Callable<Long> {

    /** {@link IWriteTransaction}. */
    protected final transient IWriteTransaction mWtx;

    /** {@link XMLEventReader}. */
    protected transient XMLEventReader mReader;

    /** Append as first child or not. */
    protected transient EShredderInsert mFirstChildAppend;

    /** Determines if changes are going to be commit right after shredding. */
    private transient EShredderCommit mCommit;

    /** {@link CountDownLatch} reference to allow other threads to wait for the shredding to finish. */
    private transient CountDownLatch mLatch;

    /**
     * Normal constructor to invoke a shredding process on a existing {@link WriteTransaction}.
     * 
     * @param paramWtx
     *            where the new XML Fragment should be placed
     * @param paramReader
     *            of the XML Fragment
     * @param paramAddAsFirstChild
     *            if the insert is occuring on a node in an existing tree. <code>false</code> is not possible
     *            when wtx is on root node.
     * @throws TTUsageException
     *             if insertasfirstChild && updateOnly is both true OR if wtx is
     *             not pointing to doc-root and updateOnly= true
     */
    public XMLShredder(final IWriteTransaction paramWtx, final XMLEventReader paramReader,
        final EShredderInsert paramAddAsFirstChild) throws TTUsageException {
        this(paramWtx, paramReader, paramAddAsFirstChild, EShredderCommit.COMMIT);
    }

    /**
     * Normal constructor to invoke a shredding process on a existing {@link WriteTransaction}.
     * 
     * @param paramWtx
     *            {@link IWriteTransaction} where the new XML Fragment should be placed
     * @param paramReader
     *            {@link XMLEventReader} to parse the xml fragment, which should be inserted
     * @param paramAddAsFirstChild
     *            determines if the insert is occuring on a node in an existing tree. <code>false</code> is
     *            not possible
     *            when wtx is on root node
     * @param paramCommit
     *            determines if inserted nodes should be commited right afterwards
     * @throws TTUsageException
     *             if insertasfirstChild && updateOnly is both true OR if wtx is
     *             not pointing to doc-root and updateOnly= true
     */
    public XMLShredder(final IWriteTransaction paramWtx, final XMLEventReader paramReader,
        final EShredderInsert paramAddAsFirstChild, final EShredderCommit paramCommit)
        throws TTUsageException {
        if (paramWtx == null || paramReader == null || paramAddAsFirstChild == null || paramCommit == null) {
            throw new IllegalArgumentException("None of the constructor parameters may be null!");
        }
        mWtx = paramWtx;
        mReader = paramReader;
        mFirstChildAppend = paramAddAsFirstChild;
        mCommit = paramCommit;
        mLatch = new CountDownLatch(1);
    }

    /**
     * Invoking the shredder.
     * 
     * @throws AbsTTException
     *             if any kind of Treetank exception which has occured
     * @return revision of file
     */
    @Override
    public Long call() throws AbsTTException {
        final long revision = mWtx.getRevisionNumber();
        insertNewContent();

        if (mCommit == EShredderCommit.COMMIT) {
            mWtx.commit();
        }
        return revision;
    }

    /**
     * Insert new content based on a StAX parser {@link XMLStreamReader}.
     * 
     * @throws AbsTTException
     *             if something went wrong while inserting
     */
    protected final void insertNewContent() throws AbsTTException {
        try {
            FastStack<Long> leftSiblingKeyStack = new FastStack<Long>();

            leftSiblingKeyStack.push((Long)EFixed.NULL_NODE_KEY.getStandardProperty());
            boolean firstElement = true;
            int level = 0;
            QName rootElement = null;
            boolean endElemReached = false;
            StringBuilder sBuilder = new StringBuilder();

            // Iterate over all nodes.
            while (mReader.hasNext() && !endElemReached) {
                final XMLEvent event = mReader.nextEvent();

                switch (event.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    level++;
                    leftSiblingKeyStack = addNewElement(leftSiblingKeyStack, (StartElement)event);
                    if (firstElement) {
                        firstElement = false;
                        rootElement = event.asStartElement().getName();
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    level--;
                    if (level == 0 && rootElement != null
                        && rootElement.equals(event.asEndElement().getName())) {
                        endElemReached = true;
                    }
                    leftSiblingKeyStack.pop();
                    mWtx.moveTo(leftSiblingKeyStack.peek());
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (mReader.peek().getEventType() == XMLStreamConstants.CHARACTERS) {
                        sBuilder.append(event.asCharacters().getData().trim());
                    } else {
                        sBuilder.append(event.asCharacters().getData().trim());
                        leftSiblingKeyStack = addNewText(leftSiblingKeyStack, sBuilder.toString());
                        sBuilder = new StringBuilder();
                    }
                    break;
                default:
                    // Node kind not known.
                }
            }
        } catch (final XMLStreamException e) {
            throw new TTIOException(e);
        }
    }

    /**
     * Add a new element node.
     * 
     * @param paramLeftSiblingKeyStack
     *            stack used to determine if the new element has to be inserted
     *            as a right sibling or as a new child (in the latter case is
     *            NULL on top of the stack)
     * @param paramEvent
     *            the current event from the StAX parser
     * @return the modified stack
     * @throws AbsTTException
     *             if adding {@link ElementNode} fails
     */
    protected final FastStack<Long> addNewElement(final FastStack<Long> paramLeftSiblingKeyStack,
        final StartElement paramEvent) throws AbsTTException {
        assert paramLeftSiblingKeyStack != null && paramEvent != null;
        long key;

        final QName name = paramEvent.getName();

        if (mFirstChildAppend == EShredderInsert.ADDASRIGHTSIBLING) {
            if (mWtx.getNode().getKind() == ENodes.ROOT_KIND) {
                throw new TTUsageException("Subtree can not be inserted as sibling of Root");
            }
            key = mWtx.insertElementAsRightSibling(name);
            mFirstChildAppend = EShredderInsert.ADDASFIRSTCHILD;
        } else {
            if (paramLeftSiblingKeyStack.peek() == (Long)EFixed.NULL_NODE_KEY.getStandardProperty()) {
                key = mWtx.insertElementAsFirstChild(name);
            } else {
                key = mWtx.insertElementAsRightSibling(name);
            }
        }

        paramLeftSiblingKeyStack.pop();
        paramLeftSiblingKeyStack.push(key);
        paramLeftSiblingKeyStack.push((Long)EFixed.NULL_NODE_KEY.getStandardProperty());

        // Parse namespaces.
        for (final Iterator<?> it = paramEvent.getNamespaces(); it.hasNext();) {
            final Namespace namespace = (Namespace)it.next();
            mWtx.insertNamespace(new QName(namespace.getNamespaceURI(), "", namespace.getPrefix()));
            mWtx.moveTo(key);
        }

        // Parse attributes.
        for (final Iterator<?> it = paramEvent.getAttributes(); it.hasNext();) {
            final Attribute attribute = (Attribute)it.next();
            mWtx.insertAttribute(attribute.getName(), attribute.getValue());
            mWtx.moveTo(key);
        }
        return paramLeftSiblingKeyStack;
    }

    /**
     * Add a new text node.
     * 
     * @param paramLeftSiblingKeyStack
     *            stack used to determine if the new element has to be inserted
     *            as a right sibling or as a new child (in the latter case is
     *            NULL on top of the stack)
     * @param paramText
     *            the text string to add
     * @return the modified stack
     * @throws AbsTTException
     *             if adding text fails
     */
    protected final FastStack<Long> addNewText(final FastStack<Long> paramLeftSiblingKeyStack,
        final String paramText) throws AbsTTException {
        assert paramLeftSiblingKeyStack != null;
        final String text = paramText;
        long key;
        final ByteBuffer textByteBuffer = ByteBuffer.wrap(TypedValue.getBytes(text));
        if (textByteBuffer.array().length > 0) {

            if (paramLeftSiblingKeyStack.peek() == (Long)EFixed.NULL_NODE_KEY.getStandardProperty()) {
                key = mWtx.insertTextAsFirstChild(new String(textByteBuffer.array()));
            } else {
                key = mWtx.insertTextAsRightSibling(new String(textByteBuffer.array()));
            }

            paramLeftSiblingKeyStack.pop();
            paramLeftSiblingKeyStack.push(key);

        }
        return paramLeftSiblingKeyStack;
    }

    /**
     * Main method.
     * 
     * @param mArgs
     *            Input and output files.
     * @throws Exception
     *             In case of any exception.
     */
    public static void main(final String... mArgs) throws Exception {
        if (mArgs.length != 2) {
            System.out.println("Usage: XMLShredder input.xml output.tnk");
            System.exit(1);
        }

        System.out.print("Shredding '" + mArgs[0] + "' to '" + mArgs[1] + "' ... ");
        final long time = System.currentTimeMillis();
        final File target = new File(mArgs[1]);
        FileDatabase.truncateDatabase(target);
        FileDatabase.createDatabase(target, new DatabaseConfiguration.Builder().build());
        final IDatabase db = FileDatabase.openDatabase(target);
        final ISession session = db.getSession(new SessionConfiguration.Builder().build());
        final IWriteTransaction wtx = session.beginWriteTransaction();
        final XMLEventReader reader = createReader(new File(mArgs[0]));
        final XMLShredder shredder = new XMLShredder(wtx, reader, EShredderInsert.ADDASFIRSTCHILD);
        shredder.call();

        wtx.close();
        session.close();

        System.out.println(" done [" + (System.currentTimeMillis() - time) + "ms].");
    }

    /**
     * Create a new StAX reader on a file.
     * 
     * @param paramFile
     *            the XML file to parse
     * @return an {@link XMLEventReader}
     * @throws IOException
     *             if I/O operation fails
     * @throws XMLStreamException
     *             if any parsing error occurs
     */
    public static synchronized XMLEventReader createReader(final File paramFile) throws IOException,
        XMLStreamException {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        final InputStream in = new FileInputStream(paramFile);
        return factory.createXMLEventReader(in);
    }

    /**
     * Create a new StAX reader on a string.
     * 
     * @param paramString
     *            the XML file as a string to parse
     * @return an {@link XMLEventReader}
     * @throws IOException
     *             if I/O operation fails
     * @throws XMLStreamException
     *             if any parsing error occurs
     */
    public static synchronized XMLEventReader createStringReader(final String paramString)
        throws IOException, XMLStreamException {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        final InputStream in = new ByteArrayInputStream(paramString.getBytes());
        return factory.createXMLEventReader(in);
    }

    /**
     * Create a new StAX reader based on a List of {@link XMLEvent}s.
     * 
     * @param paramEvents
     *            {@link XMLEvent}s
     * @return an {@link XMLEventReader}
     * @throws IOException
     *             if I/O operation fails
     * @throws XMLStreamException
     *             if any parsing error occurs
     */
    public static synchronized XMLEventReader createListReader(final List<XMLEvent> paramEvents)
        throws IOException, XMLStreamException {
        if (paramEvents == null) {
            throw new IllegalArgumentException("paramEvents may not be null!");
        }
        return new ListEventReader(paramEvents);
    }
    
    public CountDownLatch getLatch() {
        return mLatch;
    }
}
