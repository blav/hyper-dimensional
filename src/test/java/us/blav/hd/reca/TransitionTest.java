package us.blav.hd.reca;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import us.blav.hd.BinaryVector;
import us.blav.hd.Hyperspace;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith (MockitoExtension.class)
class TransitionTest {

  @Test
  public void should_init_transition () {
    Hyperspace hyperspace = new Hyperspace (16);
    Transition transition = new Transition (new Rule (90));
    BinaryVector vector = hyperspace.newZero ();
    vector.bits ().set (0);
    ArrayList<String> result = new ArrayList<> ();
    for (int i = 0; i < 32; i++) {
      result.add (vector.toString ());
      vector = transition.next (vector);
    }

    assertThat (result).containsExactly (
      "1000000000000000",
      "1100000000000000",
      "1010000000000000",
      "1011000000000000",
      "1010100000000000",
      "1010110000000000",
      "1010101000000000",
      "1010101100000000",
      "1010101010000000",
      "1010101011000000",
      "1010101010100000",
      "1010101010110000",
      "1010101010101000",
      "1010101010101100",
      "1010101010101010",
      "1010101010101011",
      "1010101010101010",
      "1010101010101011",
      "1010101010101010",
      "1010101010101011",
      "1010101010101010",
      "1010101010101011",
      "1010101010101010",
      "1010101010101011",
      "1010101010101010",
      "1010101010101011",
      "1010101010101010",
      "1010101010101011",
      "1010101010101010",
      "1010101010101011",
      "1010101010101010",
      "1010101010101011"
    );
  }
}