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

package org.treetank.access;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import org.slf4j.LoggerFactory;
import org.treetank.api.IItemList;
import org.treetank.api.IReadTransaction;
import org.treetank.api.IWriteTransaction;
import org.treetank.cache.NodePageContainer;
import org.treetank.exception.AbsTTException;
import org.treetank.exception.TTIOException;
import org.treetank.exception.TTThreadedException;
import org.treetank.exception.TTUsageException;
import org.treetank.io.AbsIOFactory;
import org.treetank.io.IReader;
import org.treetank.io.IWriter;
import org.treetank.page.PageReference;
import org.treetank.page.UberPage;
import org.treetank.settings.ESessionSetting;
import org.treetank.utils.LogWrapper;

/**
 * <h1>SessionState</h1>
 * 
 * <p>
 * State of each session.
 * </p>
 */
public final class SessionState {

    /**
     * Log wrapper for better output.
     */
    private static final LogWrapper LOGWRAPPER = new LogWrapper(LoggerFactory.getLogger(SessionState.class));

    /** Lock for blocking the commit. */
    protected final Lock mCommitLock;

    /** Session configuration. */
    protected final SessionConfiguration mSessionConfiguration;

    /** Database configuration. */
    protected final DatabaseConfiguration mDatabaseConfiguration;

    /** Write semaphore to assure only one exclusive write transaction exists. */
    private final Semaphore mWriteSemaphore;

    /** Read semaphore to control running read transactions. */
    private final Semaphore mReadSemaphore;

    /** Strong reference to uber page before the begin of a write transaction. */
    private UberPage mLastCommittedUberPage;

    /** Remember all running transactions (both read and write). */
    private final Map<Long, IReadTransaction> mTransactionMap;

    /** Remember the write seperatly because of the concurrent writes. */
    private final Map<Long, WriteTransactionState> mWriteTransactionStateMap;

    /** Storing all return futures from the sync process. */
    private final Map<Long, Map<Long, Collection<Future<Void>>>> mSyncTransactionsReturns;

    /** abstract factory for all interaction to the storage. */
    private final AbsIOFactory mFac;

    /** Atomic counter for concurrent generation of transaction id. */
    private final AtomicLong mTransactionIDCounter;

    /**
     * Constructor to bind to a TreeTank file.
     * 
     * @param paramSessionConfig
     *            Session configuration for the TreeTank.
     * @param paramDBConfig
     *            Database configuration for the TreeTank.
     * @throws AbsTTException
     *             if Session state error
     */
    protected SessionState(final DatabaseConfiguration paramDBConfig,
        final SessionConfiguration paramSessionConfig) throws AbsTTException {
        mDatabaseConfiguration = paramDBConfig;
        mSessionConfiguration = paramSessionConfig;
        mTransactionMap = new ConcurrentHashMap<Long, IReadTransaction>();
        mWriteTransactionStateMap = new ConcurrentHashMap<Long, WriteTransactionState>();
        mSyncTransactionsReturns = new ConcurrentHashMap<Long, Map<Long, Collection<Future<Void>>>>();

        mTransactionIDCounter = new AtomicLong();
        mCommitLock = new ReentrantLock(true);

        // Init session members.
        mWriteSemaphore =
            new Semaphore(Integer.parseInt(paramSessionConfig.getProps().getProperty(
                ESessionSetting.MAX_WRITE_TRANSACTIONS.name())));
        mReadSemaphore =
            new Semaphore(Integer.parseInt(paramSessionConfig.getProps().getProperty(
                ESessionSetting.MAX_READ_TRANSACTIONS.name())));
        final PageReference uberPageReference = new PageReference();

        mFac = AbsIOFactory.getInstance(mDatabaseConfiguration, mSessionConfiguration);
        if (!mFac.exists()) {
            // Bootstrap uber page and make sure there already is a root
            // node.
            mLastCommittedUberPage = new UberPage();
            uberPageReference.setPage(mLastCommittedUberPage);

        } else {
            final IReader reader = mFac.getReader();
            final PageReference firstRef = reader.readFirstReference();
            mLastCommittedUberPage = (UberPage)firstRef.getPage();
            reader.close();
        }

    }

    protected int getReadTransactionCount() {
        return Integer.parseInt(mSessionConfiguration.getProps().getProperty(
            ESessionSetting.MAX_READ_TRANSACTIONS.name()))
            - mReadSemaphore.availablePermits();
    }

    protected int getWriteTransactionCount() {
        return Integer.parseInt(mSessionConfiguration.getProps().getProperty(
            ESessionSetting.MAX_WRITE_TRANSACTIONS.name()))
            - mWriteSemaphore.availablePermits();
    }

    protected IReadTransaction beginReadTransaction() throws AbsTTException {
        return beginReadTransaction(mLastCommittedUberPage.getRevisionNumber(), null);
    }

    protected IReadTransaction beginReadTransaction(final IItemList mItemList) throws AbsTTException {
        return beginReadTransaction(mLastCommittedUberPage.getRevisionNumber(), mItemList);
    }

    protected IReadTransaction beginReadTransaction(final long mRevisionNumber, final IItemList mItemList)
        throws AbsTTException {

        // Make sure not to exceed available number of read transactions.
        try {
            mReadSemaphore.acquire();
        } catch (final InterruptedException exc) {
            LOGWRAPPER.error(exc);
            throw new AbsTTException(exc) {
                private static final long serialVersionUID = 1L;
            };
        }

        IReadTransaction rtx = null;
        // Create new read transaction.
        rtx =
            new ReadTransaction(mTransactionIDCounter.incrementAndGet(), this, new ReadTransactionState(
                mDatabaseConfiguration, mLastCommittedUberPage, mRevisionNumber, mItemList, mFac.getReader()));

        // Remember transaction for debugging and safe close.
        if (mTransactionMap.put(rtx.getTransactionID(), rtx) != null) {
            throw new TTUsageException("ID generation is bogus because of duplicate ID.");
        }
        return rtx;
    }

    protected IWriteTransaction beginWriteTransaction(final int maxNodeCount, final int maxTime)
        throws AbsTTException {

        // Make sure not to exceed available number of write transactions.
        if (mWriteSemaphore.availablePermits() == 0) {
            throw new IllegalStateException("There already is a running exclusive write transaction.");
        }
        try {
            mWriteSemaphore.acquire();
        } catch (final InterruptedException exc) {
            LOGWRAPPER.error(exc);
            throw new TTThreadedException(exc);

        }

        final long currentID = mTransactionIDCounter.incrementAndGet();
        final WriteTransactionState wtxState =
            createWriteTransactionState(currentID, mLastCommittedUberPage.getRevisionNumber(),
                mLastCommittedUberPage.getRevisionNumber());

        // Create new write transaction.
        final IWriteTransaction wtx = new WriteTransaction(currentID, this, wtxState, maxNodeCount, maxTime);

        // Remember transaction for debugging and safe close.
        if (mTransactionMap.put(currentID, wtx) != null
            || mWriteTransactionStateMap.put(currentID, wtxState) != null) {
            throw new TTThreadedException("ID generation is bogus because of duplicate ID.");
        }

        return wtx;
    }

    protected WriteTransactionState createWriteTransactionState(final long mId,
        final long mRepresentRevision, final long mStoreRevision) throws TTIOException {
        final IWriter writer = mFac.getWriter();

        return new WriteTransactionState(mDatabaseConfiguration, this, new UberPage(mLastCommittedUberPage,
            mStoreRevision + 1), writer, mId, mRepresentRevision, mStoreRevision);
    }

    protected synchronized void syncLogs(final NodePageContainer mContToSync, final long mTransactionId)
        throws TTThreadedException {
        final ExecutorService exec = Executors.newCachedThreadPool();
        final Collection<Future<Void>> returnVals = new ArrayList<Future<Void>>();
        for (final Long key : mWriteTransactionStateMap.keySet()) {
            if (key != mTransactionId) {
                returnVals.add(exec.submit(new LogSyncer(mWriteTransactionStateMap.get(key), mContToSync)));
            }
        }
        exec.shutdown();
        if (!mSyncTransactionsReturns.containsKey(mTransactionId)) {
            mSyncTransactionsReturns.put(mTransactionId,
                new ConcurrentHashMap<Long, Collection<Future<Void>>>());
        }

        if (mSyncTransactionsReturns.get(mTransactionId).put(mContToSync.getComplete().getNodePageKey(),
            returnVals) != null) {
            throw new TTThreadedException(
                "only one commit and therefore sync per id and nodepage is allowed!");
        }

    }

    protected synchronized void waitForFinishedSync(final long mTransactionKey)
        throws TTThreadedException {
        final Map<Long, Collection<Future<Void>>> completeVals =
            mSyncTransactionsReturns.remove(mTransactionKey);
        if (completeVals != null) {
            for (final Collection<Future<Void>> singleVals : completeVals.values()) {
                for (final Future<Void> returnVal : singleVals) {
                    try {
                        returnVal.get();
                    } catch (final InterruptedException exc) {
                        LOGWRAPPER.error(exc);
                        throw new TTThreadedException(exc);
                    } catch (final ExecutionException exc) {
                        LOGWRAPPER.error(exc);
                        throw new TTThreadedException(exc);
                    }
                }
            }
        }
    }

    protected void setLastCommittedUberPage(final UberPage lastCommittedUberPage) {
        mLastCommittedUberPage = lastCommittedUberPage;
    }

    protected void closeWriteTransaction(final long mTransactionID) {
        // Purge transaction from internal state.
        mTransactionMap.remove(mTransactionID);
        // Removing the write from the own internal mapping
        mWriteTransactionStateMap.remove(mTransactionID);
        // Make new transactions available.
        mWriteSemaphore.release();
    }

    protected void closeReadTransaction(final long mTransactionID) {
        // Purge transaction from internal state.
        mTransactionMap.remove(mTransactionID);
        // Make new transactions available.
        mReadSemaphore.release();
    }

    protected void close() throws AbsTTException {
        // Forcibly close all open transactions.
        for (final IReadTransaction rtx : mTransactionMap.values()) {
            if (rtx instanceof IWriteTransaction) {
                ((IWriteTransaction)rtx).abort();
            }
            rtx.close();
        }

        // Immediately release all ressources.
        mLastCommittedUberPage = null;
        mTransactionMap.clear();
        mWriteTransactionStateMap.clear();

        mFac.closeStorage();
    }

    class LogSyncer implements Callable<Void> {

        final WriteTransactionState mState;
        final NodePageContainer mCont;

        LogSyncer(final WriteTransactionState paramState, final NodePageContainer paramCont) {
            mState = paramState;
            mCont = paramCont;
        }

        @Override
        public Void call() throws Exception {
            mState.updateDateContainer(mCont);
            return null;
        }

    }

    protected void assertValidRevision(final long rev) throws TTUsageException {
        if (rev < 0) {
            throw new TTUsageException("Revision must be at least 0");
        } else if (rev > mLastCommittedUberPage.getRevision()) {
            throw new TTUsageException("Revision must not be bigger than", Long
                .toString(mLastCommittedUberPage.getRevision()));
        }
    }

}