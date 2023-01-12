package us.blav.hd;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith (MockitoExtension.class)
class CombinerTest {

  private final Hyperspace hyperspace = new Hyperspace (3);

  private final Combiner combiner = new Combiner (hyperspace);

  @Test
  public void should_combine_1 () {
    BinaryVector vector = combiner
      .add (hyperspace.newVector (0, 0, 1))
      .reduce ();

    assertThat (vector.toString ()).isEqualTo ("001");
  }

  @Test
  public void should_combine_2 () {
    BinaryVector vector = combiner
      .add (hyperspace.newVector (0, 0, 1))
      .add (hyperspace.newVector (0, 1, 1))
      .reduce ();

    assertThat (vector.toString ()).isEqualTo ("010");
  }

  @Test
  public void should_combine_3 () {
    BinaryVector vector = combiner
      .add (hyperspace.newVector (0, 0, 1))
      .add (hyperspace.newVector (0, 1, 1))
      .add (hyperspace.newVector (1, 1, 1))
      .reduce ();

    assertThat (vector.toString ()).isEqualTo ("101");
  }
}