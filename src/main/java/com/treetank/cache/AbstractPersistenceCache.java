/*
 * Copyright (c) 2009, Sebastian Graf (Ph.D. Thesis), University of Konstanz
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
 */
package com.treetank.cache;

import java.io.File;

import com.treetank.exception.TreetankIOException;
import com.treetank.page.NodePage;
import com.treetank.session.SessionConfiguration;

/**
 * Abstract class for holding all persistence caches. Each instance of this
 * class stores the data in a place related to the {@link SessionConfiguration}
 * at a different subfolder.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public abstract class AbstractPersistenceCache implements ICache {

    /**
     * Place to store the data
     */
    protected transient final File place;

    /**
     * Counter to give every instance a different place
     */
    private static int counter = 0; // NOPMD by Sebastian Graf on 05.10.09 11:16

    /**
     * Constructor with the place to store the data
     * 
     * @param paramConfig
     *            {@link SessionConfiguration} which holds the place to store
     *            the data.
     */
    protected AbstractPersistenceCache(final SessionConfiguration paramConfig) {
        place = new File(paramConfig.getAbsolutePath() + File.separator
                + "transactionLog" + File.separator + counter);
        place.mkdirs();
        counter++;
    }

    /**
     * {@inheritDoc}
     */
    public final void put(final long key, final NodePage page) {
        try {
            putPersistent(key, page);
        } catch (final TreetankIOException exc) {
            throw new IllegalStateException(exc);
        }
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public final void clear() {
        try {
            clearPersistent();
            for (final File files : place.listFiles()) {
                files.delete();
            }
            place.delete();
        } catch (final TreetankIOException exc) {
            throw new IllegalStateException(exc);
        }
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public final NodePage get(final long key) {
        try {
            return getPersistent(key);
        } catch (final TreetankIOException exc) {
            throw new IllegalStateException(exc);
        }
    }

    /**
     * Clearing a persistent cache
     * 
     * @throws TreetankIOException
     *             if something odd happens
     */
    public abstract void clearPersistent() throws TreetankIOException;

    /**
     * Putting a page into a persistent log.
     * 
     * @param key
     *            to be put
     * @param page
     *            to be put
     * @throws TreetankIOException
     *             if something odd happens
     */
    public abstract void putPersistent(final long key, final NodePage page)
            throws TreetankIOException;

    /**
     * Getting a NodePage from the persistent cache
     * 
     * @param key
     *            to get the page
     * @return the Nodepage to be fetched
     * @throws TreetankIOException
     *             if something odd happens.
     */
    public abstract NodePage getPersistent(final long key)
            throws TreetankIOException;

}