package us.blav.hd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@ExtendWith (MockitoExtension.class)
class BundlerTest {

  private final Hyperspace hyperspace = new Hyperspace (3);

  private final Bundler bundler = new Bundler (hyperspace);

  @Test
  public void should_bundle_1 () {
    BinaryVector vector = bundler
      .add (hyperspace.newVector (0, 1, 1))
      .reduce ();

    assertThat (vector.toString ()).isEqualTo ("011");
  }

  @Test
  public void should_bundle_2 () {
    int form = 0;
    int tries = 100;
    for (int i = 0; i < tries; i++) {
      BinaryVector vector = bundler
        .add (hyperspace.newVector (0, 0, 1))
        .add (hyperspace.newVector (0, 1, 1))
        .reduce ();

      String result = vector.toString ();
      if (result.equals ("011")) {
        form++;
      } else if (! result.equals ("001")) {
        fail ("invalid result " + result);
      }
    }

    assertThat (form)
      .isGreaterThan (40)
      .isLessThan (60);
  }

  @Test
  public void should_bundle_3 () {
    BinaryVector vector = bundler
      .add (hyperspace.newVector (0, 0, 0))
      .add (hyperspace.newVector (0, 0, 1))
      .add (hyperspace.newVector (0, 1, 1))
      .reduce ();

    assertThat (vector.toString ()).isEqualTo ("001");
  }
}