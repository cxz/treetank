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

import org.treetank.exception.TTException;

/**

 */
public interface INodeReadTrx {

    // --- Node Selectors
    // --------------------------------------------------------

    /**
     * Move cursor to a node by its node key.
     * 
     * @param pKey
     *            Key of node to select.
     * @return True if the node with the given node key is selected.
     */
    boolean moveTo(final long pKey);

    /**
     * Move cursor to attribute by its index.
     * 
     * @param pIndex
     *            Index of attribute to move to.
     * @return True if the attribute node is selected.
     */
    boolean moveToAttribute(final int pIndex);

    /**
     * Move cursor to namespace declaration by its index.
     * 
     * @param pIndex
     *            Index of attribute to move to.
     * @return True if the namespace node is selected.
     */
    boolean moveToNamespace(final int pIndex);

    // --- Node Getters
    // ----------------------------------------------------------

    /**
     * Getting the value of the current node.
     * 
     * @return the current value of the node
     */
    String getValueOfCurrentNode();

    /**
     * Getting the name of a current node.
     * 
     * @return the {@link QName} of the node
     */
    QName getQNameOfCurrentNode();

    /**
     * Getting the type of the current node.
     * 
     * @return the normal type of the node
     */
    String getTypeOfCurrentNode();

    /**
     * Get name for key. This is used for efficient key testing.
     * 
     * @param pKey
     *            Key, i.e., local part key, URI key, or prefix key.
     * @return String containing name for given key.
     */
    String nameForKey(final int pKey);

    /**
     * Getting the current node.
     * 
     * @return the node
     */
    org.treetank.node.interfaces.INode getNode();

    /**
     * Close shared read transaction and immediately release all resources.
     * 
     * This is an idempotent operation and does nothing if the transaction is
     * already closed.
     * 
     * @throws TTException
     *             If can't close Read Transaction.
     */
    void close() throws TTException;

    /**
     * Is this transaction closed?
     * 
     * @return true if closed, false otherwise
     */
    boolean isClosed();
}
