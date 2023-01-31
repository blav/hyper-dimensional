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
      "0100000000000000",
      "1010000000000000",
      "0001000000000000",
      "0010100000000000",
      "0100010000000000",
      "1010101000000000",
      "0000000100000000",
      "0000001010000000",
      "0000010001000000",
      "0000101010100000",
      "0001000000010000",
      "0010100000101000",
      "0100010001000100",
      "1010101010101010",
      "0000000000000001",
      "0000000000000011",
      "0000000000000110",
      "0000000000001111",
      "0000000000011000",
      "0000000000111100",
      "0000000001100110",
      "0000000011111111",
      "0000000110000000",
      "0000001111000000",
      "0000011001100000",
      "0000111111110000",
      "0001100000011000",
      "0011110000111100",
      "0110011001100110",
      "1111111111111111",
      "1000000000000000"
    );
  }
}