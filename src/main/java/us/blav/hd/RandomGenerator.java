package us.blav.hd;

import java.util.Random;

import lombok.Getter;

public class RandomGenerator {

  @Getter
  private final Random random;

  public RandomGenerator () {
    random = new Random ();
  }

  public boolean nextBoolean () {
    return random.nextBoolean ();
  }

  public int nextInt (int bound) {
    return random.nextInt (bound);
  }
}
