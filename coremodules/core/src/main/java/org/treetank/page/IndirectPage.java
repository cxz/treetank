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

package org.treetank.page;

import static com.google.common.base.Objects.toStringHelper;

import org.treetank.page.interfaces.IReferencePage;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

/**
 * <h1>IndirectPage</h1>
 * 
 * <p>
 * Indirect page holds a set of references to build a reference tree.
 * </p>
 */
public final class IndirectPage implements IReferencePage {

    /** Reference keys. */
    private final long[] mReferenceKeys;

    /** Key of this page. */
    private final long mPageKey;

    /**
     * Create indirect page.
     * 
     * @param pPageKey
     *            Key of the page
     */
    public IndirectPage(final long pPageKey) {
        mPageKey = pPageKey;
        mReferenceKeys = new long[IConstants.CONTENT_COUNT];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getByteRepresentation() {
        final ByteArrayDataOutput pOutput = ByteStreams.newDataOutput();
        pOutput.writeInt(IConstants.INDIRCTPAGE);
        pOutput.writeLong(mPageKey);
        for (long key : mReferenceKeys) {
            pOutput.writeLong(key);
        }
        return pOutput.toByteArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getPageKey() {
        return mPageKey;
    }

    @Override
    public long[] getReferenceKeys() {
        return mReferenceKeys;
    }

    @Override
    public void setReferenceKey(int pIndex, long pKey) {
        mReferenceKeys[pIndex] = pKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return toStringHelper(this).add("mPageKey", mPageKey).add("mReferenceKeys", mReferenceKeys)
            .toString();
    }

}
