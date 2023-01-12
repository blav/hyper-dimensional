package us.blav.hd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith (MockitoExtension.class)
class RotatorTest {

  private final Hyperspace hyperspace = new Hyperspace (3);

  @Test
  public void should_rotate_0 () {
    BinaryVector input = hyperspace.newVector (0, 0, 1);
    BinaryVector rotate = new Rotator (hyperspace, 0).rotate (input);

    assertThat (rotate).isSameAs (input);
  }

  @Test
  public void should_rotate_1 () {
    BinaryVector rotate = new Rotator (hyperspace, 1)
      .rotate (hyperspace.newVector (0, 0, 1));

    assertThat (rotate.toString ()).isEqualTo ("100");
  }

  @Test
  public void should_rotate_2 () {
    BinaryVector rotate = new Rotator (hyperspace, 2)
      .rotate (hyperspace.newVector (0, 0, 1));

    assertThat (rotate.toString ()).isEqualTo ("010");
  }

  @Test
  public void should_rotate_3 () {
    BinaryVector input = hyperspace.newVector (0, 0, 1);
    BinaryVector rotate = new Rotator (hyperspace, 3)
      .rotate (input);

    assertThat (rotate).isSameAs (input);
  }

  @Test
  public void should_rotate_4 () {
    BinaryVector rotate = new Rotator (hyperspace, 4)
      .rotate (hyperspace.newVector (0, 0, 1));

    assertThat (rotate.toString ()).isEqualTo ("100");
  }
}