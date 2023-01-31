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

}