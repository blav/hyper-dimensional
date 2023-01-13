package us.blav.hd;

import lombok.NonNull;

import static java.util.stream.IntStream.range;
import static us.blav.hd.Metric.ensureDimensions;

public class Hamming implements Metric {

  private final Hyperspace hyperspace;

  public Hamming (Hyperspace hyperspace) {
    this.hyperspace = hyperspace;
  }

  @Override
  public double apply (@NonNull BinaryVector a, @NonNull BinaryVector b) {
    ensureDimensions (hyperspace, a, b);
    return 1. * range (0, hyperspace.dimensions ())
      .filter (i -> a.bits ().get (i) == b.bits ().get (i))
      .sum () / hyperspace.dimensions ();
  }
}
