package us.blav.hd;

import jakarta.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import lombok.NonNull;
import us.blav.hd.util.BitString;

public class Bundler implements Accumulator<Bundler> {

  private final int[] accumulator;

  @NonNull
  private final Hyperspace hyperspace;

  private int count;

  public interface Factory {

    Bundler create (@NonNull Hyperspace hyperspace);

  }

  @Inject
  public Bundler (@NonNull @Assisted Hyperspace hyperspace) {
    this.hyperspace = hyperspace;
    this.accumulator = new int[hyperspace.dimensions ()];
  }

  public Bundler add (@NonNull BinaryVector vector) {
    if (vector.hyperspace ().dimensions () != hyperspace.dimensions ())
      throw new IllegalArgumentException ();

    count++;
    for (int i = 0; i < hyperspace.dimensions (); i++)
      if (vector.bits ().get (i))
        accumulator[i]++;

    return this;
  }

  public double counter (int dimension) {
    return accumulator[dimension] / (double) hyperspace.dimensions ();
  }

  public BinaryVector reduce () {
    int dimensions = hyperspace.dimensions ();
    BitString result = new BitString (dimensions);
    int threshold = count / 2;
    boolean random = count % 2 == 0;
    for (int i = 0; i < dimensions; i++) {
      int currentCount = accumulator[i];
      if (currentCount > threshold) {
        result.set (i);
        continue;
      }

      if (random && currentCount == threshold && hyperspace.randomGenerator ().nextBoolean ())
        result.set (i);
    }

    return new BinaryVector (hyperspace, result);
  }
}
