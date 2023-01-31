package us.blav.hd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import us.blav.hd.util.BitHacks;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith (MockitoExtension.class)
class CosineTest {

  private final Hyperspace hyperspace = new Hyperspace (2);

  private final Cosine cosine = new Cosine (hyperspace, new BitHacks ());

  @Test
  public void should_return_1_when_vector_are_parallel () {
    double result = cosine.apply (
      hyperspace.newVector (0, 1),
      hyperspace.newVector (0, 1));

    assertThat (result).isEqualTo (1.0);
  }

  @Test
  public void should_return_minus_1_when_vector_are_opposite () {
    double result = cosine.apply (
      hyperspace.newVector (0, 1),
      hyperspace.newVector (1, 0));

    assertThat (result).isEqualTo (-1.0);
  }

  @Test
  public void should_return_0_when_vectors_are_orthogonal () {
    double result = cosine.apply (
      hyperspace.newVector (0, 0),
      hyperspace.newVector (0, 1));

    assertThat (result).isEqualTo (0);
  }
}