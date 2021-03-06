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

import static com.google.common.base.Objects.toStringHelper;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import org.treetank.api.IDataFactory;
import org.treetank.api.IMetaEntryFactory;
import org.treetank.bucket.BucketFactory;
import org.treetank.bucket.DataBucket;
import org.treetank.bucket.interfaces.IBucket;
import org.treetank.exception.TTIOException;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * <h1>LogValue</h1>
 * 
 * This class acts as a container for revisioned {@link DataBucket}s. Each {@link DataBucket} is stored in a
 * versioned manner. If
 * modifications occur, the versioned {@link DataBucket}s are dereferenced and
 * reconstructed. Afterwards, this container is used to store a complete {@link DataBucket} as well as one for
 * upcoming modifications.
 * 
 * Both {@link DataBucket}s can differ since the complete one is mainly used for
 * read access and the modifying one for write access (and therefore mostly lazy
 * dereferenced).
 * 
 * Since objects of this class are stored in a cache, the class has to be
 * serializable.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public final class LogValue {

    private final IBucket mComplete;

    private final IBucket mModified;

    /**
     * Constructor with both, complete and modifying bucket.
     * 
     * @param pComplete
     *            to be used as a base for this container
     * @param pModifying
     *            to be used as a base for this container
     */
    public LogValue(final IBucket pComplete, final IBucket pModifying) {
        this.mComplete = pComplete;
        this.mModified = pModifying;
    }

    /**
     * Getting the complete bucket.
     * 
     * @return the complete bucket
     */
    public IBucket getComplete() {
        return mComplete;
    }

    /**
     * Getting the modified bucket.
     * 
     * @return the modified bucket
     */
    public IBucket getModified() {
        return mModified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toStringHelper(this).add("mComplete", mComplete).add("mModified", mModified).toString();
    }

    /**
     * Binding for serializing LogValues in the BDB.
     * 
     * @author Sebastian Graf, University of Konstanz
     * 
     */
    static class LogValueBinding extends TupleBinding<LogValue> {

        private final BucketFactory mFac;

        /**
         * Constructor
         * 
         * @param pDataFac
         *            for the deserialization of datas
         * @param pMetaFac
         *            for the deserialization of meta-entries
         */
        public LogValueBinding(final IDataFactory pDataFac, final IMetaEntryFactory pMetaFac) {
            mFac = new BucketFactory(pDataFac, pMetaFac);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public LogValue entryToObject(final TupleInput arg0) {
            try {
                final DataInput data = new DataInputStream(arg0);
                final IBucket current = mFac.deserializeBucket(data);
                final IBucket modified = mFac.deserializeBucket(data);
                arg0.close();
                return new LogValue(current, modified);
            } catch (IOException | TTIOException exc) {
                throw new RuntimeException(exc);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void objectToEntry(final LogValue arg0, final TupleOutput arg1) {
            try {
                final DataOutput data = new DataOutputStream(arg1);
                arg0.getComplete().serialize(data);
                arg0.getModified().serialize(data);
                arg1.close();
            } catch (IOException | TTIOException exc) {
                throw new RuntimeException(exc);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 80309;
        int result = 1;
        result = prime * result + ((mComplete == null) ? 0 : mComplete.hashCode());
        result = prime * result + ((mModified == null) ? 0 : mModified.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LogValue other = (LogValue)obj;
        if (mComplete == null) {
            if (other.mComplete != null)
                return false;
        } else if (!mComplete.equals(other.mComplete))
            return false;
        if (mModified == null) {
            if (other.mModified != null)
                return false;
        } else if (!mModified.equals(other.mModified))
            return false;
        return true;
    }
}
