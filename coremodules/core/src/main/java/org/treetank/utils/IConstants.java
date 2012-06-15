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

package org.treetank.utils;

/**
 * <h1>IConstants</h1>
 * 
 * <p>
 * Interface to hold all constants of the node layer. The node kinds are equivalent to DOM node kinds for
 * interoperability with saxon.
 * </p>
 */
public final class IConstants {

    // --- Varia
    // ------------------------------------------------------------------

    /** Default internal encoding. */
    public static final String DEFAULT_ENCODING = "UTF-8";

    /** Size of write buffer. */
    public static final int BUFFER_SIZE = 32767;

    // --- Indirect Page
    // ----------------------------------------------------------

    /** Count of indirect references in indirect page. */
    public static final int INP_REFERENCE_COUNT = 128;

    /** Exponent of pages per level (root level = 0, leaf level = 5). */
    public static final int[] INP_LEVEL_PAGE_COUNT_EXPONENT = {
        4 * 7, 3 * 7, 2 * 7, 1 * 7, 0 * 7
    };

    // --- Uber Page
    // -------------------------------------------------------------

    /** Revision key of unitialized storage. */
    public static final long UBP_ROOT_REVISION_COUNT = 1L;

    /** Root revisionKey guaranteed to exist in empty storage. */
    public static final long UBP_ROOT_REVISION_NUMBER = 0L;

    // --- Node Page
    // -------------------------------------------------------------

    /** Maximum node count per node page. */
    public static final int NDP_NODE_COUNT = 128;

    /** 2^NDP_NODE_COUNT_EXPONENT = NDP_NODE_COUNT. */
    public static final int NDP_NODE_COUNT_EXPONENT = 7;

}
