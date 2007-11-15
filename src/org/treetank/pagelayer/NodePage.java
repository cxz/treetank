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

package org.treetank.pagelayer;

import org.treetank.api.IConstants;
import org.treetank.nodelayer.AbstractNode;
import org.treetank.nodelayer.DocumentRootNode;
import org.treetank.nodelayer.ElementNode;
import org.treetank.nodelayer.FullTextLeafNode;
import org.treetank.nodelayer.FullTextNode;
import org.treetank.nodelayer.FullTextRootNode;
import org.treetank.nodelayer.TextNode;
import org.treetank.utils.FastByteArrayReader;
import org.treetank.utils.FastByteArrayWriter;

/**
 * <h1>NodePage</h1>
 * 
 * <p>
 * A node page stores a set of nodes.
 * </p>
 */
public final class NodePage extends AbstractPage {

  /** Key of node page. This is the base key of all contained nodes. */
  private final long mNodePageKey;

  /** Array of nodes. This can have null nodes that were removed. */
  private final AbstractNode[] mNodes;

  /**
   * Create node page.
   * 
   * @param nodePageKey Base key assigned to this node page.
   */
  public NodePage(final long nodePageKey) {
    super(0);
    mNodePageKey = nodePageKey;
    mNodes = new AbstractNode[IConstants.NDP_NODE_COUNT];
  }

  /**
   * Read node page.
   * 
   * @param in Input bytes to read page from.
   * @param nodePageKey Base key assigned to this node page.
   */
  public NodePage(final FastByteArrayReader in, final long nodePageKey) {
    super(0, in);
    mNodePageKey = nodePageKey;
    mNodes = new AbstractNode[IConstants.NDP_NODE_COUNT];

    final long keyBase = mNodePageKey << IConstants.NDP_NODE_COUNT_EXPONENT;
    for (int offset = 0; offset < IConstants.NDP_NODE_COUNT; offset++) {
      final int kind = in.readByte();
      switch (kind) {
      case IConstants.UNKNOWN:
        // Was null node, do nothing here.
        break;
      case IConstants.DOCUMENT_ROOT:
        mNodes[offset] = new DocumentRootNode(in);
        break;
      case IConstants.ELEMENT:
        mNodes[offset] = new ElementNode(keyBase + offset, in);
        break;
      case IConstants.TEXT:
        mNodes[offset] = new TextNode(keyBase + offset, in);
        break;
      case IConstants.FULLTEXT:
        mNodes[offset] = new FullTextNode(keyBase + offset, in);
        break;
      case IConstants.FULLTEXT_LEAF:
        mNodes[offset] = new FullTextLeafNode(keyBase + offset, in);
        break;
      case IConstants.FULLTEXT_ROOT:
        mNodes[offset] = new FullTextRootNode(in);
        break;
      default:
        throw new IllegalStateException(
            "Unsupported node kind encountered during read: " + kind);
      }
    }
  }

  /**
   * Clone node page.
   * 
   * @param committedNodePage Node page to clone.
   */
  public NodePage(final NodePage committedNodePage) {
    super(0, committedNodePage);
    mNodePageKey = committedNodePage.mNodePageKey;
    mNodes = new AbstractNode[IConstants.NDP_NODE_COUNT];

    // Deep-copy all nodes.
    for (int offset = 0; offset < IConstants.NDP_NODE_COUNT; offset++) {
      if (committedNodePage.mNodes[offset] != null) {
        final int kind = committedNodePage.mNodes[offset].getKind();
        switch (kind) {
        case IConstants.UNKNOWN:
          // Was null node, do nothing here.
          break;
        case IConstants.DOCUMENT_ROOT:
          mNodes[offset] =
              new DocumentRootNode(committedNodePage.mNodes[offset]);
          break;
        case IConstants.ELEMENT:
          mNodes[offset] = new ElementNode(committedNodePage.mNodes[offset]);
          break;
        case IConstants.TEXT:
          mNodes[offset] = new TextNode(committedNodePage.mNodes[offset]);
          break;
        case IConstants.FULLTEXT:
          mNodes[offset] = new FullTextNode(committedNodePage.mNodes[offset]);
          break;
        case IConstants.FULLTEXT_LEAF:
          mNodes[offset] =
              new FullTextLeafNode(committedNodePage.mNodes[offset]);
          break;
        case IConstants.FULLTEXT_ROOT:
          mNodes[offset] =
              new FullTextRootNode(committedNodePage.mNodes[offset]);
          break;
        default:
          throw new IllegalStateException(
              "Unsupported node kind encountered during clone: " + kind);
        }
      }
    }
  }

  /**
   * Get key of node page.
   * 
   * @return Node page key.
   */
  public final long getNodePageKey() {
    return mNodePageKey;
  }

  /**
   * Get node at a given offset.
   * 
   * @param offset Offset of node within local node page.
   * @return Node at given offset.
   */
  public final AbstractNode getNode(final int offset) {
    return mNodes[offset];
  }

  /**
   * Overwrite a single node at a given offset.
   * 
   * @param offset Offset of node to overwrite in this node page.
   * @param node Node to store at given nodeOffset.
   */
  public final void setNode(final int offset, final AbstractNode node) {
    mNodes[offset] = node;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void serialize(final FastByteArrayWriter out) {
    super.serialize(out);

    for (final AbstractNode node : mNodes) {
      if (node != null) {
        out.writeByte((byte) node.getKind());
        node.serialize(out);
      } else {
        out.writeByte((byte) IConstants.UNKNOWN);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String toString() {
    return super.toString()
        + ": nodePageKey="
        + mNodePageKey
        + ", isDirty="
        + isDirty();
  }

}
