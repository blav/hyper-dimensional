package us.blav.hd;

import lombok.NonNull;

import static java.util.stream.IntStream.range;
import static us.blav.hd.Metric.ensureDimensions;

public class Cosine implements Metric {

  private final Hyperspace hyperspace;

  public Cosine (Hyperspace hyperspace) {
    this.hyperspace = hyperspace;
  }

  @Override
  public double apply (@NonNull BinaryVector a, @NonNull BinaryVector b) {
    ensureDimensions (hyperspace, a, b);
    int dimensions = hyperspace.dimensions ();
    long differing = range (0, dimensions)
      .filter (i -> a.bits ().get (i) != b.bits ().get (i))
      .count ();

    return (dimensions - 2 * differing) / (double) dimensions;
  }
}
