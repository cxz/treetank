/*
 * Copyright (c) 2007, Marc Kramis
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 * $Id$
 */

package org.treetank.nodelayer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.treetank.utils.IConstants;

public class AttributeNodeTest {

  @Test
  public void testAttributeNode() {

    // Create empty node.
    final AbstractNode node1 =
        new AttributeNode(13L, 14, 15, 16, new byte[] {
            (byte) 17,
            (byte) 18 });

    // Modify it.
    node1.incrementChildCount();
    node1.decrementChildCount();

    // Clone node.
    final AbstractNode node2 = new AttributeNode(node1);

    // Now compare.
    assertEquals(13L, node2.getNodeKey());
    assertEquals(13L, node2.getParentKey());
    assertEquals(IConstants.NULL_KEY, node2.getFirstChildKey());
    assertEquals(IConstants.NULL_KEY, node2.getLeftSiblingKey());
    assertEquals(IConstants.NULL_KEY, node2.getRightSiblingKey());
    assertEquals(0, node2.getChildCount());
    assertEquals(0, node2.getAttributeCount());
    assertEquals(0, node2.getNamespaceCount());
    assertEquals(14, node2.getLocalPartKey());
    assertEquals(15, node2.getURIKey());
    assertEquals(16, node2.getPrefixKey());
    assertEquals(2, node2.getValue().length);
    assertEquals(IConstants.ATTRIBUTE, node2.getKind());
    assertEquals(false, node2.hasFirstChild());
    assertEquals(true, node2.hasParent());
    assertEquals(false, node2.hasLeftSibling());
    assertEquals(false, node2.hasRightSibling());
    assertEquals(true, node2.isAttribute());
    assertEquals(false, node2.isDocumentRoot());
    assertEquals(false, node2.isElement());
    assertEquals(false, node2.isFullText());
    assertEquals(false, node2.isFullTextLeaf());
    assertEquals(false, node2.isFullTextRoot());
    assertEquals(false, node2.isText());

  }

}