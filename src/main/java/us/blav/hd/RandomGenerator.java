package us.blav.hd;

import java.util.Random;

public class RandomGenerator {

  private final Random random;

  public RandomGenerator () {
    random = new Random ();
  }

  public boolean nextBoolean () {
    return random.nextBoolean ();
  }
}
