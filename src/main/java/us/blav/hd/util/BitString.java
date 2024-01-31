package us.blav.hd.util;

import org.apache.lucene.util.FixedBitSet;

public class BitString {

  private static final int SHORTS_PER_LONG = Long.SIZE / Short.SIZE;
  private static final int BYTES_PER_LONG = Long.SIZE / Byte.SIZE;

  private final FixedBitSet bitSet;

  public BitString (int dimensions) {
    this.bitSet = new FixedBitSet (dimensions);
  }

  public BitString (BitString that) {
    this.bitSet = that.bitSet.clone ();
  }

  public BitString duplicate () {
    return new BitString (this);
  }

  public void setLongWord (int wordPosition, long value) {
    assert wordPosition >= 0 && wordPosition < getBits ().length : "index out of range";
    getBits ()[wordPosition] = value;
  }

  public long getLongWord (int wordPosition) {
    assert wordPosition >= 0 && wordPosition < getBits ().length : "index out of range";
    return getBits ()[wordPosition];
  }

  public void setShortWord (int wordPosition, short value) {
    assert wordPosition >= 0 && wordPosition < getBits ().length * SHORTS_PER_LONG : "index out of range";
    int position = wordPosition / SHORTS_PER_LONG;
    int shift = Short.SIZE * (wordPosition % SHORTS_PER_LONG);
    long mask = ~ (0xffffL << shift);
    getBits ()[position] = (getBits ()[position] & mask) | ((long) value << shift);
  }

  public short getShortWord (int wordPosition) {
    assert wordPosition >= 0 && wordPosition < getBits ().length * SHORTS_PER_LONG : "index out of range";
    int position = wordPosition / SHORTS_PER_LONG;
    int shift = Short.SIZE * (wordPosition % SHORTS_PER_LONG);
    return (short) ((getBits ()[position] & (0xffffL << shift)) >> shift);
  }

  public void setByteWord (int wordPosition, byte value) {
    assert wordPosition >= 0 && wordPosition < getBits ().length * BYTES_PER_LONG : "index out of range";
    int position = wordPosition / BYTES_PER_LONG;
    int shift = Byte.SIZE * (wordPosition % BYTES_PER_LONG);
    long mask = ~ (0xffL << shift);
    getBits ()[position] = (getBits ()[position] & mask) | ((long) value << shift);
  }

  public byte getByteWord (int wordPosition) {
    assert wordPosition >= 0 && wordPosition < getBits ().length * BYTES_PER_LONG : "index out of range";
    int position = wordPosition / BYTES_PER_LONG;
    int shift = Byte.SIZE * (wordPosition % BYTES_PER_LONG);
    return (byte) ((getBits ()[position] & (0xffL << shift)) >> shift);
  }

  public void set (int index, boolean value) {
    if (value) {
      bitSet.set (index);
    } else {
      bitSet.clear (index);
    }
  }

  private long[] getBits () {
    return bitSet.getBits ();
  }

  public boolean get (int index) {
    return bitSet.get (index);
  }

  public void set (int index) {
    bitSet.set (index);
  }

  public void xor (BitString bits) {
    bitSet.xor (bits.bitSet);
  }

  public int getNumWords () {
    return bitSet.getBits ().length;
  }

  public void flip (int index) {
    bitSet.flip (index);
  }
}
