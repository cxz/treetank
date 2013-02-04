package org.treetank.log;

import static com.google.common.base.Objects.toStringHelper;

import java.util.Objects;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * Container for Key-Entry in the log determining the level and the the sequence in the level.
 * Needed for the WriteTrx for getting inserting any modified pages in the right order since the page-key can
 * not be computed from the nodekeys due to the relative position of the nodes in the subtree of the related
 * RevisionRootPage.
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public class LogKey {

    /** Is this key referencing to the root level or to the node level. */
    private final boolean mRootLevel;

    /** Level Key. */
    private final long mLevel;

    /** Sequence Key. */
    private final long mSeq;

    /**
     * Constructor.
     * 
     * @param pLevel
     *            to be set.
     * @param pSeq
     *            to be set.
     */
    public LogKey(final boolean pRootLevel, final long pLevel, final long pSeq) {
        mRootLevel = pRootLevel;
        mLevel = pLevel;
        mSeq = pSeq;
    }

    /**
     * 
     * Getting the level key.
     * 
     * @return the mLevel
     */
    public long getLevel() {
        return mLevel;
    }

    /**
     * Getting the seq key.
     * 
     * @return the mSeq
     */
    public long getSeq() {
        return mSeq;
    }

    /**
     * Getter for mRootLevel.
     * 
     * @return the mRootLevel
     */
    public boolean isRootLevel() {
        return mRootLevel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(mLevel, mRootLevel, mSeq);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toStringHelper(this).add("mRootLevel", mRootLevel).add("mLevel", mLevel).add("mSeq", mSeq)
            .toString();
    }

    /**
     * Binding for serializing LogKeys in the BDB.
     * 
     * @author Sebastian Graf, University of Konstanz
     * 
     */
    static class LogKeyBinding extends TupleBinding<LogKey> {

        /**
         * {@inheritDoc}
         */
        @Override
        public LogKey entryToObject(TupleInput arg0) {
            final ByteArrayDataInput data = ByteStreams.newDataInput(arg0.getBufferBytes());
            final LogKey key = new LogKey(data.readBoolean(), data.readLong(), data.readLong());
            return key;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void objectToEntry(LogKey arg0, TupleOutput arg1) {
            final ByteArrayDataOutput output = ByteStreams.newDataOutput();
            output.writeBoolean(arg0.isRootLevel());
            output.writeLong(arg0.getLevel());
            output.writeLong(arg0.getSeq());
            arg1.write(output.toByteArray());
        }

    }

}
