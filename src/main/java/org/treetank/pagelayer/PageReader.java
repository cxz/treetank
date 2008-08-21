/*
 * Copyright (c) 2008, Marc Kramis (Ph.D. Thesis), University of Konstanz
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

package org.treetank.pagelayer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.treetank.sessionlayer.SessionConfiguration;
import org.treetank.utils.FastByteArrayReader;
import org.treetank.utils.IConstants;

/**
 * <h1>PageReader</h1>
 * 
 * <p>
 * Each thread working on a I[Read|Write]Transaction has one PageReader
 * instance to independently read from an TreeTank file.
 * </p>
 */
public final class PageReader {

  /** Random access mFile to work on. */
  private final RandomAccessFile mFile;

  /** Adler32 Checksum to assert integrity. */
  private final Checksum mChecksum;

  /** Do we use encryption? */
  private final boolean mIsEncrypted;

  /** Do we use checksumming? */
  private final boolean mIsChecksummed;

  /** Cipher to encrypt and decrypt blocks. */
  private final Cipher mCipher;

  /** Secret nodeKey to use for cryptographic operations. */
  private final SecretKeySpec mSecretKeySpec;

  /** Inflater to decompress. */
  private ICompression mDecompressor;

  /**
   * Constructor.
   * 
   * @param sessionConfiguration Configuration of session we are bound to.
   * @throws RuntimeException if the class could not be instantiated.
   */
  public PageReader(final SessionConfiguration sessionConfiguration) {

    try {

      mFile =
          new RandomAccessFile(
              sessionConfiguration.getAbsolutePath(),
              IConstants.READ_ONLY);

      if (sessionConfiguration.isChecksummed()) {
        mIsChecksummed = true;
        mChecksum = new CRC32();
      } else {
        mIsChecksummed = false;
        mChecksum = null;
      }

      if (sessionConfiguration.isEncrypted()) {
        mIsEncrypted = true;
        mCipher = Cipher.getInstance(IConstants.DEFAULT_ENCRYPTION_ALGORITHM);
        mSecretKeySpec =
            new SecretKeySpec(
                sessionConfiguration.getEncryptionKey(),
                IConstants.DEFAULT_ENCRYPTION_ALGORITHM);
        mCipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec);
      } else {
        mIsEncrypted = false;
        mCipher = null;
        mSecretKeySpec = null;
      }

      try {
        System.loadLibrary("TreeTank");
        mDecompressor = new NativeTreeTank();
      } catch (UnsatisfiedLinkError e) {
        mDecompressor = new JavaCompression();
      }

    } catch (Exception e) {
      throw new RuntimeException("Could not create page reader: "
          + e.getLocalizedMessage());
    }
  }

  /**
   * Read page from storage.
   * 
   * @param pageReference to read.
   * @return Byte array reader to read bytes from.o
   * @throws RuntimeException if there was an error during reading.
   */
  public final FastByteArrayReader read(
      final PageReference<? extends AbstractPage> pageReference) {

    if (!pageReference.isCommitted()) {
      throw new IllegalArgumentException("Page reference is invalid.");
    }

    // Prepare members.
    byte[] page;

    try {

      // Read encrypted page from mFile.
      mFile.seek(pageReference.getStart());
      page = new byte[pageReference.getLength()];
      mFile.read(page);

      // Decrypt page.
      //      if (mIsEncrypted) {
      //        page = mCipher.doFinal(page);
      //      }

      // Verify checksummed page.
      if (mIsChecksummed) {
        mChecksum.reset();
        mChecksum.update(page, 0, page.length);
        if (mChecksum.getValue() != pageReference.getChecksum()) {
          throw new Exception("Page checksum is not valid for start="
              + pageReference.getStart()
              + "; size="
              + pageReference.getLength()
              + "; checksum="
              + pageReference.getChecksum());
        }
      }

      // Decompress page.
      page = mDecompressor.decompress(page, page.length);

    } catch (Exception e) {
      throw new RuntimeException("Could not read page "
          + pageReference
          + " due to: "
          + e.getLocalizedMessage());
    }

    // Return reader required to instantiate and deserialize page.
    return new FastByteArrayReader(page);

  }

  /**
   * Properly close file handle.
   */
  public final void close() {
    try {
      mFile.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Close file handle in case it is not properly closed by the application.
   * 
   * @throws Throwable if the finalization of the superclass does not work.
   */
  @Override
  protected void finalize() throws Throwable {
    try {
      mFile.close();
    } finally {
      super.finalize();
    }
  }

}
