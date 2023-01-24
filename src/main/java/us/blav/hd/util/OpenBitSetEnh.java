package us.blav.hd.util;

import org.apache.lucene.util.OpenBitSet;

public class OpenBitSetEnh extends OpenBitSet {

  private static final int SHORTS_PER_LONG = Long.SIZE / Short.SIZE;

  public OpenBitSetEnh (int dimensions) {
    super (dimensions);
  }

  public void setWord (int wordPosition, long value) {
    assert wordPosition >= 0 && wordPosition < bits.length : "index out of range";
    bits[wordPosition] = value;
  }

  public long getWord (int wordPosition) {
    assert wordPosition >= 0 && wordPosition < bits.length : "index out of range";
    return bits[wordPosition];
  }

  public void setShortWord (int wordPosition, short value) {
    assert wordPosition >= 0 && wordPosition < bits.length * SHORTS_PER_LONG : "index out of range";
    int position = wordPosition / SHORTS_PER_LONG;
    int shift = Short.SIZE * (wordPosition % SHORTS_PER_LONG);
    long mask = ~ (0xffffL << shift);
    bits[wordPosition] = (bits[position] & mask) | ((long) value << shift);
  }

  public short getShortWord (int wordPosition) {
    assert wordPosition >= 0 && wordPosition < bits.length * SHORTS_PER_LONG : "index out of range";
    int position = wordPosition / SHORTS_PER_LONG;
    int shift = Short.SIZE * (wordPosition % SHORTS_PER_LONG);
    return (short) ((bits[position] & (0xffffL << shift)) >> shift);
  }

  public void set (long index, boolean value) {
    if (value) {
      set (index);
    } else {
      clear (index);
    }
  }
}