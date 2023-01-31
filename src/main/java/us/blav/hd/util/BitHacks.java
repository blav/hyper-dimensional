package us.blav.hd.util;

import javax.inject.Singleton;

@Singleton
public class BitHacks {

  public int countSet (long v) {
    return countSet ((int) (v & 0xffffffffL)) + countSet ((int) (v >> 32));
  }

  // see https://graphics.stanford.edu/~seander/bithacks.html#CountBitsSetParallel
  public int countSet (int v) {
    v = v - ((v >> 1) & 0x55555555);
    v = (v & 0x33333333) + ((v >> 2) & 0x33333333);
    return ((v + (v >> 4) & 0xF0F0F0F) * 0x1010101) >> 24;
  }
}
