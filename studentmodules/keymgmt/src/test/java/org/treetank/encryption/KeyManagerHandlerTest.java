/**
 * Copyright (c) 2011, University of Konstanz, Distributed Systems Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of Konstanz nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
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
package org.treetank.encryption;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.treetank.exception.TTEncryptionException;

public class KeyManagerHandlerTest {


    @Before
    public void setUp() throws Exception {
        new EncryptionController().clear();
        new EncryptionController().setEncryptionOption(true);
        new EncryptionController().init();

    }

    @After
    public void tearDown() {
        new EncryptionController().setEncryptionOption(false);
    }

    @Test
    public void testJoinAndLeave() throws TTEncryptionException {
        String[] nodes = new String[] {
            "Inf", "Disy", "TT", "Group1"
        };
        EncryptionOperator op = new EncryptionOperator();
        op.join("ROOT", nodes);

        String[] nodes2 = new String[] {
            "BaseX", "Group2"
        };
        EncryptionOperator op2 = new EncryptionOperator();
        op2.join("Inf", nodes2);

        String[] nodes3 = new String[] {
            "RZ", "Waldvogel"
        };
        EncryptionOperator op3 = new EncryptionOperator();
        op3.join("ROOT", nodes3);

        String[] nodes4 = new String[] {
            "Waldvogel"
        };
        EncryptionOperator op4 = new EncryptionOperator();
        op4.join("TT", nodes4);

        EncryptionOperator op10 = new EncryptionOperator();
        op10.leave("Group2", new String[] {
            "BaseX"
        });

        EncryptionOperator op9 = new EncryptionOperator();
        op9.leave("Waldvogel", new String[] {});
        


        // after all joins and leaves and join/leave updates database size must be 31.
        assertEquals(new EncryptionController().getSelDb().count(), 31);
        assertEquals(new EncryptionController().getDAGDb().count(), 7);
        assertEquals(new EncryptionController().getManDb().count(), 2);
    }
}