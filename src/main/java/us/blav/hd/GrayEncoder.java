package us.blav.hd;

import jakarta.inject.Singleton;
import java.util.stream.IntStream;

import lombok.Value;

import static java.lang.Byte.SIZE;
import static java.lang.Byte.toUnsignedInt;

@Value
@Singleton
public class GrayEncoder implements ByteEncoder {

  byte[] codes;

  public GrayEncoder () {
    codes = new byte[1 << SIZE];
    IntStream.range (0, SIZE).forEach (level -> {
      int middle = 1 << level;
      IntStream.range (0, middle).forEach (i ->
        codes[middle + i] = (byte) (codes[middle - 1 - i] | middle));
    });
  }

  @Override
  public byte encode (byte index) {
    return codes[toUnsignedInt (index)];
  }
}
