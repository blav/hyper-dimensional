package us.blav.hd;

import lombok.NonNull;
import org.apache.lucene.util.OpenBitSet;

import static java.util.stream.IntStream.range;

public class Cosine {

  private final Hyperspace hyperspace;

  public Cosine (Hyperspace hyperspace) {
    this.hyperspace = hyperspace;
  }

  public double apply (@NonNull BinaryVector a, @NonNull BinaryVector b) {
    if (a.hyperspace () != hyperspace || b.hyperspace () != hyperspace)
      throw new IllegalArgumentException ();

    OpenBitSet dist = (OpenBitSet) a.bits ().clone ();
    dist.xor (b.bits ());
    int dimensions = hyperspace.dimensions ();
    long differing = range (0, dimensions)
      .filter (dist::get)
      .count ();

    return (dimensions - 2 * differing) / (double) dimensions;
  }
}
