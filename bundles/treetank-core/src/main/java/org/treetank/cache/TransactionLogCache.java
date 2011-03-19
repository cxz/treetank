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
package org.treetank.cache;

import org.treetank.access.DatabaseConfiguration;
import org.treetank.exception.TTIOException;

/**
 * Transactionlog for storing all upcoming nodes in either the ram cache or a
 * persistent second cache.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public final class TransactionLogCache extends AbstractPersistenceCache {

    /**
     * RAM-Based first cache.
     */
    private transient final LRUCache mFirstCache;

    /**
     * Constructor including the {@link SessionConfiguration} for persistent
     * storage.
     * 
     * @param paramConfig
     *            the config for having a storage-place
     * @param revision
     *            revision number
     * @throws TTIOException
     *             Exception if IO is not successful
     */
    public TransactionLogCache(final DatabaseConfiguration paramConfig, final long revision)
        throws TTIOException {
        super(paramConfig);
        final BerkeleyPersistenceCache secondCache = new BerkeleyPersistenceCache(paramConfig, revision);
        mFirstCache = new LRUCache(secondCache);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearPersistent() throws TTIOException {
        mFirstCache.clear();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodePageContainer getPersistent(final long mKey) throws TTIOException {

        return mFirstCache.get(mKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putPersistent(final long mKey, final NodePageContainer mPage) throws TTIOException {
        mFirstCache.put(mKey, mPage);
    }

}