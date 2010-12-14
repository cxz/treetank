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

package com.treetank.axis.filter;

import com.treetank.api.IFilter;
import com.treetank.api.IReadTransaction;
import com.treetank.node.ENodes;

/**
 * <h1>FullTextLeafAxisTest</h1>
 * 
 * <p>
 * Only select nodes of kind FULLTEXT_LEAF.
 * </p>
 */
public class TextFilter extends AbsFilter  {

    /**
     * Default constructor.
     * 
     * @param rtx
     *            Transaction this filter is bound to.
     */
    public TextFilter(final IReadTransaction rtx) {
        super(rtx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean filter() {
        return getTransaction().getNode().getKind() == ENodes.TEXT_KIND;
    }

}