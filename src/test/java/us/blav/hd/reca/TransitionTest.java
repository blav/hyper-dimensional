package us.blav.hd.reca;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import us.blav.hd.BinaryVector;
import us.blav.hd.Hyperspace;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith (MockitoExtension.class)
class TransitionTest {

  @Test
  public void should_init_transition () {
    Hyperspace hyperspace = new Hyperspace (16);
    Transition transition = new Transition (hyperspace, new Rule (3));
    BinaryVector vector = hyperspace.newRandom ();
    for (int i = 0; i < 32; i ++) {
      System.out.println (vector.toString ());
      vector = transition.next (vector);
    }
  }
}