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

package org.treetank.axis;

import java.util.Stack;

import org.treetank.api.INodeReadTrx;
import org.treetank.node.IConstants;
import org.treetank.node.interfaces.IStructNode;

/**
 * <h1>PrecedingAxis</h1>
 * 
 * <p>
 * Iterate over all preceding nodes of kind ELEMENT or TEXT starting at a given node. Self is not included.
 * </p>
 */
public class PrecedingAxis extends AbsAxis {

    private boolean mIsFirst;

    private Stack<Long> mStack;

    /**
     * Constructor initializing internal state.
     * 
     * @param rtx
     *            Exclusive (immutable) trx to iterate with.
     */
    public PrecedingAxis(final INodeReadTrx rtx) {

        super(rtx);
        mIsFirst = true;
        mStack = new Stack<Long>();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void reset(final long mNodeKey) {

        super.reset(mNodeKey);
        mIsFirst = true;
        mStack = new Stack<Long>();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean hasNext() {

        // assure, that preceding is not evaluated on an attribute or a
        // namespace
        if (mIsFirst) {
            mIsFirst = false;
            if (getNode().getKind() == IConstants.ATTRIBUTE
            // || getTransaction().isNamespaceKind()
            ) {
                resetToStartKey();
                return false;
            }
        }

        resetToLastKey();

        if (!mStack.empty()) {
            // return all nodes of the current subtree in reverse document order
            moveTo(mStack.pop());
            return true;
        }

        if (((IStructNode)getNode()).hasLeftSibling()) {
            moveTo(((IStructNode)getNode()).getLeftSiblingKey());
            // because this axis return the precedings in reverse document
            // order, we
            // need to travel to the node in the subtree, that comes last in
            // document
            // order.
            getLastChild();
            return true;
        }

        while (getNode().hasParent()) {
            // ancestors are not part of the preceding set
            moveTo(getNode().getParentKey());
            if (((IStructNode)getNode()).hasLeftSibling()) {
                moveTo(((IStructNode)getNode()).getLeftSiblingKey());
                // move to last node in the subtree
                getLastChild();
                return true;
            }
        }

        resetToStartKey();
        return false;

    }

    /**
     * Moves the transaction to the node in the current subtree, that is last in
     * document order and pushes all other node key on a stack. At the end the
     * stack contains all node keys except for the last one in reverse document
     * order.
     */
    private void getLastChild() {

        // nodekey of the root of the current subtree
        final long parent = getNode().getDataKey();

        // traverse tree in pre order to the leftmost leaf of the subtree and
        // push
        // all nodes to the stack
        if (((IStructNode)getNode()).hasFirstChild()) {
            while (((IStructNode)getNode()).hasFirstChild()) {
                mStack.push(getNode().getDataKey());
                moveTo(((IStructNode)getNode()).getFirstChildKey());
            }

            // traverse all the siblings of the leftmost leave and all their
            // descendants and push all of them to the stack
            while (((IStructNode)getNode()).hasRightSibling()) {
                mStack.push(getNode().getDataKey());
                moveTo(((IStructNode)getNode()).getRightSiblingKey());
                getLastChild();
            }

            // step up the path till the root of the current subtree and process
            // all
            // right siblings and their descendants on each step
            if (getNode().hasParent() && (getNode().getParentKey() != parent)) {

                mStack.push(getNode().getDataKey());
                while (getNode().hasParent() && (getNode().getParentKey() != parent)) {

                    moveTo(getNode().getParentKey());

                    // traverse all the siblings of the leftmost leave and all
                    // their
                    // descendants and push all of them to the stack
                    while (((IStructNode)getNode()).hasRightSibling()) {

                        moveTo(((IStructNode)getNode()).getRightSiblingKey());
                        getLastChild();
                        mStack.push(getNode().getDataKey());
                    }
                }

                // set transaction to the node in the subtree that is last in
                // document
                // order
                moveTo(mStack.pop());
            }
        }
    }
}
