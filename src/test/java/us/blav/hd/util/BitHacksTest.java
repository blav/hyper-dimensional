package us.blav.hd.util;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.fail;

@ExtendWith (MockitoExtension.class)
class BitHacksTest {

  @InjectMocks
  private Random random;

  @InjectMocks
  private BitHacks hacks;

  @Test
  public void countSet_should_count_bit_set_in_int () {
    for (int i = 0; i < 100000; i++) {
      int bits = random.nextInt ();
      int actual = hacks.countSet (bits);
      int expected = countSet (bits);
      if (actual != expected)
        fail (String.format ("countSet failed for %32s, actual=%d, expected=%d",
          Integer.toBinaryString (bits).replace (' ', '0'), actual, expected));
    }
  }

  @Test
  public void countSet_should_count_bit_set_in_long () {
    for (int i = 0; i < 100000; i++) {
      long bits = random.nextLong ();
      int actual = hacks.countSet (bits);
      int expected = countSet (bits);
      if (actual != expected)
        fail (String.format ("countSet failed for %64s, actual=%d, expected=%d",
          Long.toBinaryString (bits).replace (' ', '0'), actual, expected));
    }
  }

  private int countSet (int bits) {
    return IntStream.range (0, Integer.SIZE)
      .map (i -> (bits & (1 << i)) != 0 ? 1 : 0)
      .sum ();
  }

  private int countSet (long bits) {
    return (int) LongStream.range (0, Long.SIZE)
      .map (i -> (bits & (1L << i)) != 0 ? 1L : 0L)
      .sum ();
  }
}