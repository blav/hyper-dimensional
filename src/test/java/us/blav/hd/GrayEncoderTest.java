package us.blav.hd;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.Byte.SIZE;
import static java.lang.Byte.toUnsignedInt;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith (MockitoExtension.class)
class GrayEncoderTest {

  @InjectMocks
  private GrayEncoder encoder;

  @Test
  public void should_generate_unique_entries () {
    long count = IntStream.range (0, 1 << SIZE)
      .mapToObj (i -> encoder.encode ((byte) i))
      .distinct ()
      .count ();

    assertThat (count).isEqualTo (1 << SIZE);
  }

  @Test
  public void siblings_should_differ_in_a_single_bit () {
    IntStream.range (0, 1 << SIZE - 1).forEach (i -> {
      int diff = toUnsignedInt (encoder.encode ((byte) i)) ^
        toUnsignedInt (encoder.encode ((byte) (i + 1)));

      assertThat (IntStream.range (0, SIZE)
        .mapToObj (k -> 1 << k)
        .anyMatch (k -> diff == k)).isTrue ();
    });
  }
}