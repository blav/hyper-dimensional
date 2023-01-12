package us.blav.hd;

import lombok.NonNull;
import org.apache.lucene.util.OpenBitSet;

public class Bundler implements Accumulator<Bundler> {

  private final int[] accumulator;

  private final Hyperspace hyperspace;

  private int count;

  public Bundler (Hyperspace hyperspace) {
    this.hyperspace = hyperspace;
    this.accumulator = new int[hyperspace.dimensions ()];
  }

  public Bundler add (@NonNull BinaryVector vector) {
    if (vector.hyperspace () != hyperspace)
      throw new IllegalArgumentException ();

    count++;
    for (int i = 0; i < hyperspace.dimensions (); i++)
      if (vector.bits ().get (i))
        accumulator[i]++;

    return this;
  }

  public BinaryVector reduce () {
    int dimensions = hyperspace.dimensions ();
    OpenBitSet result = new OpenBitSet (dimensions);
    int threshold = count / 2;
    boolean random = count % 2 == 0;
    for (int i = 0; i < dimensions; i++) {
      int currentCount = accumulator[i];
      if (currentCount > threshold) {
        result.fastSet (i);
        continue;
      }

      if (random && currentCount == threshold && hyperspace.randomGenerator ().nextBoolean ())
        result.fastSet (i);
    }

    return new BinaryVector (hyperspace, result);
  }
}
