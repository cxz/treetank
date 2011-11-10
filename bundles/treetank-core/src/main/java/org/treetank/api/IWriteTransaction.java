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

package org.treetank.api;

import javax.xml.namespace.QName;

import org.treetank.exception.AbsTTException;
import org.treetank.exception.TTIOException;

/**
 * <h1>IWriteTransaction</h1>
 * 
 * <h2>Description</h2>
 * 
 * <p>
 * Interface to access nodes based on the
 * Key/ParentKey/FirstChildKey/LeftSiblingKey/RightSiblingKey/ChildCount encoding. This encoding keeps the
 * children ordered but has no knowledge of the global node ordering. The underlying tree is accessed in a
 * cursor-like fashion.
 * </p>
 * 
 * <p>
 * Each commit at least adds <code>10kB</code> to the TreeTank file. It is thus recommended to work with the
 * auto commit mode only committing after a given amount of node modifications or elapsed time. For very
 * update-intensive data, a value of one million modifications and ten seconds is recommended. Note that this
 * might require to increment to available heap.
 * </p>
 * 
 * <h2>Convention</h2>
 * 
 * <p>
 * <ol>
 * <li>Only a single thread accesses the single IWriteTransaction instance.</li>
 * <li><strong>Precondition</strong> before moving cursor: <code>IWriteTransaction.getNodeKey() == n</code>.</li>
 * <li><strong>Postcondition</strong> after modifying the cursor: <code>(IWriteTransaction.insertX() == m &&
 *       IWriteTransaction.getNodeKey() == m)</code>.</li>
 * </ol>
 * </p>
 * 
 * <h2>User Example</h2>
 * 
 * <p>
 * 
 * <pre>
 * // Without auto commit.
 * final IWriteTransaction wtx = session.beginWriteTransaction();
 * wtx.insertElementAsFirstChild(&quot;foo&quot;);
 * // Explicit forced commit.
 * wtx.commit();
 * wtx.close();
 * 
 * // With auto commit after every 10th modification.
 * final IWriteTransaction wtx = session.beginWriteTransaction(10, 0);
 * wtx.insertElementAsFirstChild(new QName(&quot;foo&quot;));
 * // Implicit commit.
 * wtx.close();
 * 
 * // With auto commit after every second.
 * final IWriteTransaction wtx = session.beginWriteTransaction(0, 1);
 * wtx.insertElementAsFirstChild(new QName(&quot;foo&quot;));
 * // Implicit commit.
 * wtx.close();
 * 
 * // With auto commit after every 10th modification and every second.
 * final IWriteTransaction wtx = session.beginWriteTransaction(10, 1);
 * wtx.insertElementAsFirstChild(new QName(&quot;foo&quot;));
 * // Implicit commit.
 * wtx.close();
 * </pre>
 * 
 * </p>
 * 
 * <h2>Developer Example</h2>
 * 
 * <p>
 * 
 * <pre>
 *   public final void someIWriteTransactionMethod() {
 *     // This must be called to make sure the transaction is not closed.
 *     assertNotClosed();
 *     // This must be called to track the modifications.
 *     mModificationCount++;
 *     ...
 *   }
 * </pre>
 * 
 * </p>
 */
public interface IWriteTransaction extends IReadTransaction {

    // --- Node Modifiers
    // --------------------------------------------------------

    /**
     * Move a subtree rooted at paramToKey to the first child rooted at
     * paramToKey.
     * 
     * @param paramFromKey
     *            root node key of the subtree to move
     * @return key of rooted subtree
     * @throws AbsTTException
     *             if move adaption fails
     */
    long moveSubtreeToFirstChild(final long paramFromKey) throws AbsTTException;

    /**
     * Move a subtree rooted at paramToKey to the right sibling of the node at
     * paramToKey.
     * 
     * @param paramFromKey
     *            root node key of the subtree to move
     * @return key of rooted subtree
     * @throws AbsTTException
     *             if move adaption fails
     */
    long moveSubtreeToRightSibling(final long paramFromKey) throws AbsTTException;

    /**
     * Insert new element node as first child of currently selected node. The
     * cursor is moved to the inserted node.
     * 
     * @param mQName
     *            {@link QName} of node to insert
     * @throws AbsTTException
     *             if element node couldn't be inserted as first child
     * @return key of inserted node
     */
    long insertElementAsFirstChild(final QName mQName) throws AbsTTException;

    /**
     * Insert new text node as first child of currently selected node. The
     * cursor is moved to the inserted node.
     * 
     * @param paramValue
     *            value of node to insert
     * @throws AbsTTException
     *             if text node couldn't be inserted as first child.
     * @return Key of inserted node. Already has a first child.
     */
    long insertTextAsFirstChild(final String paramValue) throws AbsTTException;

    /**
     * Insert new element node as right sibling of currently selected node. The
     * cursor is moved to the inserted node.
     * 
     * @param paramQName
     *            {@link QName} of the new node
     * @throws AbsTTException
     *             if element node couldn't be inserted as right sibling
     * @return Key of inserted node. Already has a first child.
     */
    long insertElementAsRightSibling(final QName paramQName) throws AbsTTException;

    /**
     * Insert new text node as right sibling of currently selected node. The
     * cursor is moved to the inserted node.
     * 
     * @param paramValue
     *            value of node to insert
     * @throws AbsTTException
     *             if text node couldn't be inserted as right sibling
     * @return Key of inserted node. the root node which is not allowed to have
     *         right siblings.
     */
    long insertTextAsRightSibling(final String paramValue) throws AbsTTException;

    /**
     * Insert attribute in currently selected node. The cursor is moved to the
     * inserted node.
     * 
     * @param paramQName
     *            {@link QName} reference
     * @param paramValue
     *            value of inserted node
     * @throws AbsTTException
     *             if attribute couldn't be inserted.
     * @return key of inserted node
     */
    long insertAttribute(final QName paramQName, final String paramValue) throws AbsTTException;

    /**
     * Insert namespace declaration in currently selected node. The cursor is
     * moved to the inserted node.
     * 
     * @param paramQName
     *            {@link QName} reference
     * @throws AbsTTException
     *             if attribute couldn't be inserted.
     * @return key of inserted node
     */
    long insertNamespace(final QName paramQName) throws AbsTTException;

    /**
     * Remove currently selected node. This does automatically remove
     * descendants.
     * 
     * The cursor is located at the former right sibling. If there was no right
     * sibling, it is located at the former left sibling. If there was no left
     * sibling, it is located at the former parent.
     * 
     * @throws AbsTTException
     *             if node couldn't be removed
     */
    void remove() throws AbsTTException;

    // --- Node Setters
    // -----------------------------------------------------------

    /**
     * Set QName of node.
     * 
     * @param paramName
     *            New qualified name of node.
     * @throws TTIOException
     *             If can't set Name in node.
     */
    void setQName(final QName paramName) throws TTIOException;

    /**
     * Set URI of node.
     * 
     * @param paramUri
     *            new URI of node
     * @throws TTIOException
     *             if URI of node couldn't be set
     */
    void setURI(final String paramUri) throws TTIOException;

    /**
     * Set value of node.
     * 
     * @param paramValue
     *            new value of node
     * @throws TTIOException
     *             if value couldn't be set
     */
    void setValue(final String paramValue) throws AbsTTException;

    /**
     * Commit all modifications of the exclusive write transaction. Even commit
     * if there are no modification at all.
     * 
     * @throws AbsTTException
     *             if this revision couldn't be commited
     */
    void commit() throws AbsTTException;

    /**
     * Abort all modifications of the exclusive write transaction.
     * 
     * @throws TTIOException
     *             if this revision couldn't be aborted
     */
    void abort() throws TTIOException;

    /**
     * Reverting all changes to the revision defined. This command has to be
     * finalized with a commit. A revert is always bound to a {@link IReadTransaction#moveToDocumentRoot()}.
     * 
     * @param paramRevision
     *            revert to the revision
     * @throws AbsTTException
     *             if couldn't revert to revision
     */
    void revertTo(final long paramRevision) throws AbsTTException;

    /**
     * Closing current WriteTransaction.
     * 
     * @throws AbsTTException
     *             if write transaction couldn't be closed
     */
    @Override
    void close() throws AbsTTException;

    /**
     * Copy subtree from another database/resource/revision and insert as right
     * sibling of the current node.
     * 
     * @param paramNodeKey
     *            node key of the root node of the subtree to copy
     * @param paramRevision
     *            revision from which to copy a subtree
     * @param paramDatabase
     *            database reference which implements the {@link IDatabase} interface
     * @return key of copied subtree root node
     * @throws AbsTTException
     *             if anything went wrong
     */
    long copySubtreeAsFirstChild(final long paramNodeKey, final long paramRevision) throws AbsTTException;

    /**
     * Copy subtree from another database/resource/revision and insert as right
     * sibling of the current node.
     * 
     * @param paramNodeKey
     *            node key of the root node of the subtree to copy
     * @param paramRevision
     *            revision from which to copy a subtree
     * @param paramDatabase
     *            database reference which implements the {@link IDatabase} interface
     * @return key of copied subtree root node
     * @throws AbsTTException
     *             if anything went wrong
     */
    long copySubtreeAsRightSibling(final long paramNodeKey, final long paramRevision) throws AbsTTException;
}
