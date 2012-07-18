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

import static org.testng.AssertJUnit.assertEquals;

import org.treetank.TestHelper;
import org.treetank.exception.TTException;
import org.treetank.page.PageReference;
import org.treetank.page.UberPage;

/**
 * Helper class for testing the io interfaces
 * 
 * @author Sebastian Graf, University of Konstanz
 * 
 */
public final class IOTestHelper {

    private IOTestHelper() {
    }

    /**
     * Tear down for all tests related to the io layer.
     */
    public static void clean() throws TTException {
        TestHelper.deleteEverything();

    }

    public static void testReadWriteFirstRef(final IStorage storage) throws TTException {

        final PageReference pageRef1 = new PageReference();
        final UberPage page1 = new UberPage();
        pageRef1.setPage(page1);

        // same instance check
        final IWriter writer = storage.getWriter();
        writer.writeFirstReference(pageRef1);
        final PageReference pageRef2 = writer.readFirstReference();
        assertEquals(pageRef1.getNodePageKey(), pageRef2.getNodePageKey());
        assertEquals(((UberPage)pageRef1.getPage()).getRevisionCount(), ((UberPage)pageRef2.getPage())
            .getRevisionCount());
        writer.close();

        // new instance check
        final IReader reader = storage.getReader();
        final PageReference pageRef3 = reader.readFirstReference();
        assertEquals(pageRef1.getNodePageKey(), pageRef3.getNodePageKey());
        assertEquals(((UberPage)pageRef1.getPage()).getRevisionCount(), ((UberPage)pageRef3.getPage())
            .getRevisionCount());
        reader.close();
        storage.close();

    }
}
