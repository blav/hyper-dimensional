package us.blav.hd;

import org.apache.lucene.util.OpenBitSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith (MockitoExtension.class)
class HyperspaceTest {

  @Mock
  private RandomGenerator random;

  @Test
  public void should_generate_zero () {
    Hyperspace hyperspace = new Hyperspace (3);
    assertThat (hyperspace.newZero ().toString ()).isEqualTo ("000");
  }

  @Test
  public void should_generate_random () {
    when (random.nextBoolean ()).thenReturn (true, false, true);
    Hyperspace hyperspace = new Hyperspace (3, random, null, null, null, Cosine::new, Hamming::new);
    assertThat (hyperspace.newRandom ().toString ()).isEqualTo ("101");
  }

  @Test
  public void newVector_should_create_vector () {
    BinaryVector vector = new Hyperspace (3).newVector (0, 1, 0);
    OpenBitSet bits = vector.bits ();
    assertThat (bits.get (0)).isFalse ();
    assertThat (bits.get (1)).isTrue ();
    assertThat (bits.get (2)).isFalse ();
  }
}