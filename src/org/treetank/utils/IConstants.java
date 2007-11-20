/*
 * Copyright (c) 2007, Marc Kramis
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 * $Id$
 */

package org.treetank.utils;

/**
 * <h1>IConstants</h1>
 * 
 * <p>
 * Interface to hold all constants of the node layer. The node kinds
 * are equivalent to DOM node kinds for interoperability with saxon.
 * </p>
 */
public interface IConstants {

  //--- Version ----------------------------------------------------------------

  /** Major version number of this release. */
  public static final int VERSION_MAJOR = 4;

  /** Minor version number of this release. */
  public static final int VERSION_MINOR = 17;

  /** Last major version to which this version is binary compatible. */
  public static final int LAST_VERSION_MAJOR = 4;

  /** Last minor version to which this version is binary compatible. */
  public static final int LAST_VERSION_MINOR = 14;

  //--- Varia ------------------------------------------------------------------

  /** Start of beacon. */
  public static final int BEACON_START = 10;

  /** Length of beacon in bytes. */
  public static final int BEACON_LENGTH = 20;

  /** Default internal encoding. */
  public static final String DEFAULT_ENCODING = "UTF-8";

  /** Default encryption algorithm. */
  public static final String DEFAULT_ENCRYPTION_ALGORITHM = "AES";

  /** Length of encryption key. */
  public static final int ENCRYPTION_KEY_LENGTH = 16;

  /** Read-only random access file. */
  public static final String READ_ONLY = "r";

  /** Read-write random access file. */
  public static final String READ_WRITE = "rw";

  //--- Indirect Page ----------------------------------------------------------

  /** Count of indirect references in indirect page. */
  public static final int INP_REFERENCE_COUNT = 256;

  /** 2^INP_REFERENCE_COUNT_EXPONENT = INP_REFERENCE_COUNT. */
  public static final int INP_REFERENCE_COUNT_EXPONENT = 8;

  /** Exponent of pages per level (root level = 0, leaf level = 5). */
  public static final int[] INP_LEVEL_PAGE_COUNT_EXPONENT =
      {
          4 * INP_REFERENCE_COUNT_EXPONENT,
          3 * INP_REFERENCE_COUNT_EXPONENT,
          2 * INP_REFERENCE_COUNT_EXPONENT,
          1 * INP_REFERENCE_COUNT_EXPONENT,
          0 * INP_REFERENCE_COUNT_EXPONENT };

  // --- Uber Page -------------------------------------------------------------

  /** Revision key of unitialized storage. */
  public static final long UBP_ROOT_REVISION_COUNT = 1L;

  /** Root revisionKey guaranteed to exist in empty storage. */
  public static final long UBP_ROOT_REVISION_NUMBER = 0L;

  // --- Revision Root Page ----------------------------------------------------

  // --- Node Page -------------------------------------------------------------

  /** Maximum node count per node page. */
  public static final int NDP_NODE_COUNT = 256;

  /** 2^NDP_NODE_COUNT_EXPONENT = NDP_NODE_COUNT. */
  public static final int NDP_NODE_COUNT_EXPONENT = 8;

  //--- Node Layer -------------------------------------------------------------

  /** Number of concurrent exclusive write transactions. */
  public static final int MAX_WRITE_TRANSACTIONS = 1;

  /** Number of concurrent shared read transactions. */
  public static final int MAX_READ_TRANSACTIONS = 256;

  /** Null nodeKey constant. */
  public static final long NULL_KEY = -1L;

  /** Document root key constant. */
  public static final long DOCUMENT_ROOT_KEY = 0L;

  /** FullText root key constant. */
  public static final long FULLTEXT_ROOT_KEY = 1L;

  /** Root node page key constant. */
  public static final long ROOT_PAGE_KEY = 0L;

  /** Undefined name. */
  public static final int NULL_NAME = -1;

  /** Undefined node. */
  public static final int UNKNOWN = 0;

  /** INode kind is element. */
  public static final int ELEMENT = 1;

  /** INode kind is attribute. */
  public static final int ATTRIBUTE = 2;

  /** INode kind is text. */
  public static final int TEXT = 3;

  /** INode kind is namespace. */
  public static final int NAMESPACE = 4;

  /** INode kind is processing instruction. */
  public static final int PROCESSING_INSTRUCTION = 7;

  /** INode kind is comment. */
  public static final int COMMENT = 8;

  /** INode kind is document root. */
  public static final int DOCUMENT_ROOT = 9;

  /** INode kind is fulltext root. */
  public static final int FULLTEXT_ROOT = 13;

  /** INode kind is fulltext. */
  public static final int FULLTEXT = 14;

  /** INode kind is fulltext leaf. */
  public static final int FULLTEXT_LEAF = 15;

  //--- XML Layer --------------------------------------------------------------

  /** Commit threshold. */
  public static final int COMMIT_THRESHOLD = 262144;

}