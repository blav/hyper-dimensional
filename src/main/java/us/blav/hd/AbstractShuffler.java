package us.blav.hd;

import lombok.Getter;
import lombok.NonNull;

public abstract class AbstractShuffler {

  @Getter
  @NonNull
  protected final Hyperspace hyperspace;

  protected final int[] mapping;

  protected AbstractShuffler (@NonNull Hyperspace hyperspace, @NonNull RandomGenerator randomGenerator, int mappingSize) {
    this.hyperspace = hyperspace;
    this.mapping = createMapping (randomGenerator, mappingSize);
  }

  public abstract BinaryVector shuffle (BinaryVector vector);

  protected static int[] createMapping (RandomGenerator randomGenerator, int dimensions) {
    int[] mapping = new int[dimensions];
    for (int i = 0; i < dimensions; i++)
      mapping[i] = i;

    for (int i = dimensions; i > 1; i--)
      swap (mapping, i - 1, randomGenerator.nextInt (i));

    return mapping;
  }

  private static void swap (int[] mapping, int i, int j) {
    int tmp = mapping[i];
    mapping[i] = mapping[j];
    mapping[j] = tmp;
  }
}
