package us.blav.hd.util;

import java.util.stream.IntStream;

import static java.lang.Byte.SIZE;
import static java.lang.Byte.toUnsignedInt;

public class GrayEncoder {

  private final byte[] codes;

  public GrayEncoder () {
    codes = new byte[1 << SIZE];
    IntStream.range (0, SIZE).forEach (level -> {
      int middle = 1 << level;
      IntStream.range (0, middle).forEach (i ->
        codes[middle + i] = (byte) (codes[middle - 1 - i] | middle));
    });
  }

  public byte encode (byte index) {
    return codes[toUnsignedInt (index)];
  }
}
