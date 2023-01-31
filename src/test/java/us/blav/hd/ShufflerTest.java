package us.blav.hd;

import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith (MockitoExtension.class)
class ShufflerTest {

  @Mock
  private RandomGenerator randomGenerator;

  private Hyperspace hyperspace;

  private BitShuffler bitShuffler;

  @BeforeEach
  public void beforeEach () {
    when (randomGenerator.nextInt (anyInt ())).thenReturn (1);
    hyperspace = new Hyperspace (4, randomGenerator);
    bitShuffler = new BitShuffler (hyperspace);
  }

  @Test
  public void should_shuffle () {
    BinaryVector input = new BinaryVectorFactory ().newVector (hyperspace, 0, 1, 0, 1);
    assertThat (bitShuffler.shuffle (input).toString ())
      .isEqualTo ("0110");
  }
}