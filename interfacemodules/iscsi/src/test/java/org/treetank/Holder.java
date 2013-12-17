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
package org.treetank;

import org.treetank.access.conf.ResourceConfiguration;
import org.treetank.api.IBucketWriteTrx;
import org.treetank.exception.TTException;
import org.treetank.iscsi.access.IscsiWriteTrx;
import org.treetank.iscsi.api.IIscsiReadTrx;
import org.treetank.iscsi.api.IIscsiWriteTrx;
import org.treetank.testutil.CoreTestHelper;
import org.treetank.testutil.CoreTestHelper.PATHS;

/**
 * Generating a standard resource within the {@link PATHS#PATH1} path. It also
 * generates a standard resource defined within {@link CoreTestHelper#RESOURCENAME}.
 * 
 * @author Andreas Rain adapted from Sebastian Graf, University of Konstanz
 * 
 */
public class Holder {

    private CoreTestHelper.Holder mHolder;

    private IIscsiReadTrx mIRtx;

    /**
     * @param pHolder
     * @param pConf
     * @return {@link org.treetank.CoreTestHelper.Holder} - the holder for the generated wtx.
     * @throws TTException
     */
    public static Holder generateWtx(CoreTestHelper.Holder pHolder, ResourceConfiguration pConf)
        throws TTException {
        final Holder holder = new Holder();
        holder.mHolder = pHolder;
        IBucketWriteTrx wtx = pHolder.getSession().beginBucketWtx();
        holder.mIRtx = new IscsiWriteTrx(wtx, holder.mHolder.getSession());
        return holder;
    }

    /**
     * @throws TTException
     */
    public void close() throws TTException {
        if (mIRtx != null && !mIRtx.isClosed()) {
            mIRtx.close();
        }
        mHolder.close();
    }

    /**
     * @return {@link IIscsiReadTrx} - the irtx
     */
    public IIscsiReadTrx getIRtx() {
        return mIRtx;
    }

    /**
     * @return {@link IIscsiWriteTrx} - the iwtx
     */
    public IIscsiWriteTrx getIWtx() {
        if (mIRtx instanceof IIscsiWriteTrx) {
            return (IIscsiWriteTrx)mIRtx;
        } else {
            throw new IllegalStateException();
        }

    }

}
