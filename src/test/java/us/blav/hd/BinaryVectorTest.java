package us.blav.hd;

import org.apache.lucene.util.OpenBitSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith (MockitoExtension.class)
class BinaryVectorTest {

  private final Hyperspace hyperspace = new Hyperspace (3);

  @Test
  public void toString_should_serialize_bits () {
    OpenBitSet bitSet = new OpenBitSet (hyperspace.dimensions ());
    bitSet.set (0);
    bitSet.set (2);

    assertThat (new BinaryVector (hyperspace, bitSet).toString ()).isEqualTo ("101");
  }

}