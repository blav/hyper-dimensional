package us.blav.hd;

import lombok.NonNull;

public interface Metric {

  double apply (@NonNull BinaryVector a, @NonNull BinaryVector b);

  static void ensureDimensions (Hyperspace hyperspace, @NonNull BinaryVector a, @NonNull BinaryVector b) {
    if (a.hyperspace () != hyperspace || b.hyperspace () != hyperspace)
      throw new IllegalArgumentException ();
  }
}
