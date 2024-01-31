package us.blav.hd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import us.blav.hd.util.BitString;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@ExtendWith (MockitoExtension.class)
class BinaryVectorFactoryTest {

  @Mock (answer = RETURNS_DEEP_STUBS)
  private Hyperspace hyperspace;

  @InjectMocks
  private BinaryVectorFactory factory;

  @BeforeEach
  public void beforeEach () {
    when (hyperspace.dimensions ()).thenReturn (3);
  }

  @Test
  public void should_generate_random () {
    when (hyperspace.randomGenerator ().nextBoolean ()).thenReturn (true, false, true);
    assertThat (factory.newRandom (hyperspace).toString ()).isEqualTo ("101");
  }

  @Test
  public void newVector_should_create_vector () {
    BinaryVector vector = factory.newVector (hyperspace, 0, 1, 0);
    BitString bits = vector.bits ();
    assertThat (bits.get (0)).isFalse ();
    assertThat (bits.get (1)).isTrue ();
    assertThat (bits.get (2)).isFalse ();
  }
}