package us.blav.hd.util;

import jakarta.inject.Singleton;

@Singleton
public class BitHacks {

  public int countSet (long bits) {
    return countSet ((int) (bits & 0xffffffffL)) + countSet ((int) (bits >> 32));
  }

  // see https://graphics.stanford.edu/~seander/bithacks.html#CountBitsSetParallel
  public int countSet (int bits) {
    bits = bits - ((bits >> 1) & 0x55555555);
    bits = (bits & 0x33333333) + ((bits >> 2) & 0x33333333);
    return ((bits + (bits >> 4) & 0xF0F0F0F) * 0x1010101) >> 24;
  }
}
