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

package org.treetank.node;

import static org.junit.Assert.assertEquals;
import static org.treetank.node.IConstants.NULL_NODE;
import static org.treetank.node.IConstants.ROOT_NODE;

import org.junit.Test;
import org.treetank.io.file.ByteBufferSinkAndSource;
import org.treetank.node.delegates.NodeDelegate;
import org.treetank.node.delegates.StructNodeDelegate;

public class DocumentRootNodeTest {

    @Test
    public void testDocumentRootNode() {

        // Create empty node.
        final NodeDelegate nodeDel = new NodeDelegate(ROOT_NODE, NULL_NODE, NULL_NODE);
        final StructNodeDelegate strucDel =
            new StructNodeDelegate(nodeDel, NULL_NODE, NULL_NODE, NULL_NODE, 0);
        final DocumentRootNode node1 = new DocumentRootNode(nodeDel, strucDel);
        check(node1);

        // Serialize and deserialize node.
        final ByteBufferSinkAndSource out = new ByteBufferSinkAndSource();
        ENode.getKind(node1.getClass()).serialize(out, node1);
        out.position(0);
        final DocumentRootNode node2 = (DocumentRootNode)ENode.ROOT_KIND.deserialize(out);
        check(node2);

    }

    private final static void check(final DocumentRootNode node) {
        // Now compare.
        assertEquals(ROOT_NODE, node.getNodeKey());
        assertEquals(NULL_NODE, node.getParentKey());
        assertEquals(NULL_NODE, node.getFirstChildKey());
        assertEquals(NULL_NODE, node.getLeftSiblingKey());
        assertEquals(NULL_NODE, node.getRightSiblingKey());
        assertEquals(0L, node.getChildCount());
        assertEquals(IConstants.ROOT, node.getKind());

    }

}
