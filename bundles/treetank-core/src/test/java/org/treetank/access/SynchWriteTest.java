package org.treetank.access;

import java.util.concurrent.Callable;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import javax.xml.namespace.QName;

import junit.framework.TestCase;


import org.treetank.TestHelper;
import org.treetank.TestHelper.PATHS;
import org.treetank.api.IDatabase;
import org.treetank.api.IReadTransaction;
import org.treetank.api.ISession;
import org.treetank.api.IWriteTransaction;
import org.treetank.exception.AbsTTException;
import org.treetank.exception.TTIOException;
import org.treetank.utils.DocumentCreater;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SynchWriteTest {
    Exchanger<Boolean> threadsFinished = new Exchanger<Boolean>();
    Exchanger<Boolean> verify = new Exchanger<Boolean>();

    @Before
    public void setUp() throws AbsTTException {
        TestHelper.deleteEverything();
        final IDatabase database = TestHelper.getDatabase(PATHS.PATH1.getFile());
        final ISession session = database.getSession();
        final IWriteTransaction wtx = session.beginWriteTransaction();
        wtx.moveToDocumentRoot();
        wtx.insertElementAsFirstChild(new QName(""));
        wtx.insertElementAsRightSibling(new QName(""));
        wtx.moveToLeftSibling();
        wtx.insertElementAsFirstChild(new QName(""));
        wtx.moveToParent();
        wtx.moveToRightSibling();
        wtx.insertElementAsFirstChild(new QName(""));
        wtx.commit();
        wtx.close();
        session.close();
    }

    @After
    public void tearDown() throws AbsTTException {
        TestHelper.closeEverything();
    }

    @Test
    @Ignore
    /**
     * Two threads are launched which access the file concurrently, performing changes 
     * that have to persist.
     */
    public void testConcurrentWrite() throws AbsTTException, InterruptedException, ExecutionException {
        final IDatabase database = TestHelper.getDatabase(PATHS.PATH1.getFile());
        final Semaphore semaphore = new Semaphore(1);
        final ISession session = database.getSession();
        final IWriteTransaction wtx = session.beginWriteTransaction();
        final IWriteTransaction wtx2 = session.beginWriteTransaction();
        final ExecutorService exec = Executors.newFixedThreadPool(2);
        final Callable<Void> c1 = new Wtx1(wtx, semaphore);
        final Callable<Void> c2 = new Wtx2(wtx2, semaphore);
        final Future<Void> r1 = exec.submit(c1);
        final Future<Void> r2 = exec.submit(c2);
        exec.shutdown();

        r1.get();
        r2.get();
        
        final IReadTransaction rtx = session.beginWriteTransaction();
        TestCase.assertTrue(rtx.moveToFirstChild());
        TestCase.assertTrue(rtx.moveToFirstChild());
        TestCase.assertFalse(rtx.moveToRightSibling());
        TestCase.assertTrue(rtx.moveToParent());
        TestCase.assertTrue(rtx.moveToRightSibling());
        TestCase.assertTrue(rtx.moveToFirstChild());
        TestCase.assertTrue(rtx.moveToFirstChild());
        rtx.close();
    }
}

class Wtx1 implements Callable<Void> {
    final IWriteTransaction wtx;
    final Semaphore mSemaphore;

    Wtx1(final IWriteTransaction swtx, final Semaphore semaphore) {
        this.wtx = swtx;
        mSemaphore = semaphore;
    }

    @Override
    public Void call() throws Exception {
        wtx.moveToFirstChild();
        wtx.moveToFirstChild();
        mSemaphore.acquire();
        wtx.insertElementAsFirstChild(new QName("a"));
        mSemaphore.release();
        mSemaphore.acquire();
        wtx.insertElementAsRightSibling(new QName("a"));
        mSemaphore.release();
        mSemaphore.acquire();
        wtx.moveToLeftSibling();
        wtx.remove();
        mSemaphore.release();
        wtx.commit();
        wtx.close();
        return null;
    }

}

class Wtx2 implements Callable<Void> {

    final IWriteTransaction wtx;
    final Semaphore mSemaphore;

    Wtx2(final IWriteTransaction swtx, final Semaphore semaphore) {
        this.wtx = swtx;
        mSemaphore = semaphore;
    }

    @Override
    public Void call() throws Exception {
        wtx.moveToFirstChild();
        wtx.moveToRightSibling();
        wtx.moveToFirstChild();
        mSemaphore.acquire();
        wtx.insertElementAsFirstChild(new QName("a"));
        mSemaphore.release();
        mSemaphore.acquire();
        wtx.moveToParent();
        wtx.insertElementAsFirstChild(new QName("a"));
        mSemaphore.release();
        wtx.commit();
        wtx.close();
        return null;
    }

}