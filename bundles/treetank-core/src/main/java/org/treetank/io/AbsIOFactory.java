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

package org.treetank.io;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.treetank.access.DatabaseConfiguration;
import org.treetank.access.SessionConfiguration;
import org.treetank.exception.TTIOException;
import org.treetank.io.berkeley.BerkeleyFactory;
import org.treetank.io.file.FileFactory;

/**
 * Abstract Factory to build up a concrete storage for the data. The Abstract
 * Instance must provide Reader and Writers as well as some additional methods.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public abstract class AbsIOFactory {

    /**
     * Concurrent storage for all avaliable databases in runtime.
     */
    private static final Map<SessionConfiguration, AbsIOFactory> FACTORIES =
        new ConcurrentHashMap<SessionConfiguration, AbsIOFactory>();

    /**
     * Concurrent storage for all avaliable databases in runtime.
     */
    private static final Map<File, Set<SessionConfiguration>> STORAGES =
        new ConcurrentHashMap<File, Set<SessionConfiguration>>();

    /**
     * Config for the session holding information about the settings of the
     * session.
     */
    protected final SessionConfiguration mSessionConfig;

    /**
     * Config for the database holding information about the location of the storage.
     */
    protected final DatabaseConfiguration mDatabaseConfig;

    /** Type for different storages. */
    public enum StorageType {
        /** File Storage. */
        File,
        /** Berkeley Storage. */
        Berkeley
    }

    /** Folder to store the data. */
    public final File mFile;

    /**
     * Protected constructor, just setting the sessionconfiguration.
     * 
     * @param paramFile
     *            to be set
     * @param paramSession
     *            to be set
     * @param paramDatabase
     *            to be set
     */
    protected AbsIOFactory(final File paramFile, final DatabaseConfiguration paramDatabase,
        final SessionConfiguration paramSession) {
        mSessionConfig = paramSession;
        mDatabaseConfig = paramDatabase;
        mFile = paramFile;
    }

    /**
     * Getting a writer.
     * 
     * @return an {@link IWriter} instance
     * @throws TTIOException
     *             if the initalisation fails
     */
    public abstract IWriter getWriter() throws TTIOException;

    /**
     * Getting a reader.
     * 
     * @return an {@link IReader} instance
     * @throws TTIOException
     *             if the initalisation fails
     */
    public abstract IReader getReader() throws TTIOException;

    /**
     * Getting a Closing this storage. Is equivalent to Session.close
     * 
     * @throws TTIOException
     *             exception to be throwns
     */
    public final void closeStorage() throws TTIOException {
        closeConcreteStorage();
        FACTORIES.remove(this.mSessionConfig);
        final Set<SessionConfiguration> conf = STORAGES.get(mFile.getParentFile());
        conf.remove(this.mSessionConfig);
    }

    /**
     * Truncate storage and remove all resources within a database
     * 
     * @param paramFile
     *            which should be removed
     * @throws TTIOException
     *             if anything occures
     */
    public synchronized static final void truncateStorage(final File paramFile) throws TTIOException {
        final Set<SessionConfiguration> configs = STORAGES.remove(paramFile);
        if (configs != null) {
            for (final SessionConfiguration config : configs) {
                final AbsIOFactory fac = FACTORIES.remove(config);
                fac.truncate();
            }
        }
        recursiveDelete(paramFile);
    }

    /**
     * Closing concrete storage.
     * 
     * @throws TTIOException
     *             if anything weird happens
     */
    protected abstract void closeConcreteStorage() throws TTIOException;

    public synchronized static final void registerInstance(final File paramFile,
        final DatabaseConfiguration paramDatabaseConf, final SessionConfiguration paramSessionConf)
        throws TTIOException {
        AbsIOFactory fac = null;
        if (!FACTORIES.containsKey(paramSessionConf)) {
            final AbsIOFactory.StorageType storageType = paramDatabaseConf.mType;
            switch (storageType) {
            case File:
                fac = new FileFactory(paramFile, paramDatabaseConf, paramSessionConf);
                break;
            case Berkeley:
                fac = new BerkeleyFactory(paramFile, paramDatabaseConf, paramSessionConf);
                break;
            default:
                throw new TTIOException("Type", storageType.toString(), "not valid!");
            }
            FACTORIES.put(paramSessionConf, fac);
            Set<SessionConfiguration> configs = STORAGES.get(paramFile.getParentFile());
            if (configs == null) {
                configs = new HashSet<SessionConfiguration>();
            }
            configs.add(paramSessionConf);
            STORAGES.put(paramFile.getParentFile(), configs);
        }
    }

    /**
     * Getting an AbstractIOFactory instance.
     * !!!MUST CALL REGISTERINSTANCE BEFOREHAND!!!!
     * 
     * @param paramSessionConf
     *            settings for the session
     * @throws TTIOException
     *             if an I/O error occurs
     * @return an instance of this factory based on the kind in the conf
     */
    public static final AbsIOFactory getInstance(final SessionConfiguration paramSessionConf)
        throws TTIOException {
        return FACTORIES.get(paramSessionConf);
    }

    /**
     * Check if storage exists.
     * 
     * @return true if storage holds data, false otherwise
     * @throws TTIOException
     *             if storage is not accessible
     */
    public abstract boolean exists() throws TTIOException;

    /**
     * Truncate database completely
     * 
     * @throws TTIOException
     *             if storage is not accessible
     */
    public abstract void truncate() throws TTIOException;

    /** {@inheritDoc} */
    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("factory keys: ").append(FACTORIES.keySet()).append("\n");
        builder.append("DatabaseConfig: ").append(mDatabaseConfig.toString()).append("\n");
        builder.append("SessionConfig: ").append(mSessionConfig.toString()).append("\n");
        // builder.append("exists: ").append(exists()).append("\n");
        return builder.toString();
    }

    /**
     * Deleting a storage recursive. Used for deleting a databases
     * 
     * @param paramFile
     *            which should be deleted included descendants
     * @return true if delete is valid
     */
    protected static boolean recursiveDelete(final File paramFile) {
        if (paramFile.isDirectory()) {
            for (final File child : paramFile.listFiles()) {
                if (!recursiveDelete(child)) {
                    return false;
                }
            }
        }
        return paramFile.delete();
    }
}
