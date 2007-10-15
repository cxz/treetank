/*
 * TreeTank - Embedded Native XML Database
 * 
 * Copyright 2007 Marc Kramis
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */

package org.treetank.utils;

import java.math.BigInteger;

public final class FastByteArrayWriter {

  private byte[] mBuffer;

  private int mSize;

  /**
   * Constructor.
   *
   */
  public FastByteArrayWriter() throws Exception {
    mBuffer = new byte[32];
    mSize = 0;
  }

  public final void writeBoolean(final boolean value) throws Exception {
    assertSize(1);
    mBuffer[mSize++] = (byte) (value ? 1 : 0);
  }

  public final void writeByte(final byte value) throws Exception {
    assertSize(1);
    mBuffer[mSize++] = value;
  }

  public final void writePseudoInt(final int value) throws Exception {
    assertSize(3);
    mBuffer[mSize++] = (byte) (value >> 16);
    mBuffer[mSize++] = (byte) (value >> 8);
    mBuffer[mSize++] = (byte) value;
  }

  public final void writeInt(final int value) throws Exception {
    assertSize(4);
    mBuffer[mSize++] = (byte) (value >> 24);
    mBuffer[mSize++] = (byte) (value >> 16);
    mBuffer[mSize++] = (byte) (value >> 8);
    mBuffer[mSize++] = (byte) value;
  }
  
  public final void writePseudoVarSizeInt(final int value) throws Exception {
    assertSize(4);
    mBuffer[mSize++] = (byte) (value);
    if (value > 127/*63 || value < -64*/) {
      mBuffer[mSize-1] |= 128;
      mBuffer[mSize++] = (byte) (value >> 7);
      if (value > 16383/*8191 || value < -8192*/) {
        mBuffer[mSize-1] |= 128;
        mBuffer[mSize++] = (byte) (value >> 14);
        if (value > 2097151/*1048575 || value < -1048576*/) {
          mBuffer[mSize-1] |= 128;
          mBuffer[mSize++] = (byte) (value >> 21);
        } else mBuffer[mSize-1] &= 127;
      } else mBuffer[mSize-1] &= 127;
    } else mBuffer[mSize-1] &= 127;
  }

  public final void writePseudoLong(final long value) throws Exception {
    assertSize(6);
    mBuffer[mSize++] = (byte) (value >>> 40);
    mBuffer[mSize++] = (byte) (value >>> 32);
    mBuffer[mSize++] = (byte) (value >>> 24);
    mBuffer[mSize++] = (byte) (value >>> 16);
    mBuffer[mSize++] = (byte) (value >> 8);
    mBuffer[mSize++] = (byte) value;
  }

  public final void writeLong(final long value) throws Exception {
    assertSize(8);
    mBuffer[mSize++] = (byte) (value >>> 56);
    mBuffer[mSize++] = (byte) (value >>> 48);
    mBuffer[mSize++] = (byte) (value >>> 40);
    mBuffer[mSize++] = (byte) (value >>> 32);
    mBuffer[mSize++] = (byte) (value >>> 24);
    mBuffer[mSize++] = (byte) (value >>> 16);
    mBuffer[mSize++] = (byte) (value >> 8);
    mBuffer[mSize++] = (byte) value;
  }

  public final void writeByteArray(final byte[] value) throws Exception {
    assertSize(value.length + 3);

    // Size of byte array.    
    mBuffer[mSize++] = (byte) (value.length >> 16);
    mBuffer[mSize++] = (byte) (value.length >> 8);
    mBuffer[mSize++] = (byte) value.length;

    // Byte array.
    System.arraycopy(value, 0, mBuffer, mSize, value.length);
    mSize += value.length;
  }

  public final byte[] getBytes() throws Exception {
    return mBuffer;
  }

  public final int size() {
    return mSize;
  }

  public final void reset() {
    mSize = 0;
  }

  private final void assertSize(final int sizeDifference) {
    final int requestedSize = mSize + sizeDifference;
    if (requestedSize > mBuffer.length) {
      final byte[] biggerBuffer = new byte[requestedSize << 1];
      System.arraycopy(mBuffer, 0, biggerBuffer, 0, mBuffer.length);
      mBuffer = biggerBuffer;
    }
  }

}
