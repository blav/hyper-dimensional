package us.blav.hd;

import javax.inject.Singleton;

import lombok.NonNull;
import us.blav.hd.util.BitString;

import static java.util.stream.IntStream.range;

@Singleton
public class BinaryVectorFactory {

  public BinaryVector newRandom (@NonNull Hyperspace hyperspace) {
    int dimensions = hyperspace.dimensions ();
    BitString bits = new BitString (dimensions);
    range (0, dimensions)
      .filter (i -> hyperspace.randomGenerator ().nextBoolean ())
      .forEach (bits::fastSet);

    return new BinaryVector (hyperspace, bits);
  }

  public BinaryVector newVector (@NonNull Hyperspace hyperspace, int... bits) {
    int dimensions = hyperspace.dimensions ();
    if (bits.length != dimensions)
      throw new IllegalArgumentException ();

    BitString result = new BitString (dimensions);
    range (0, dimensions)
      .filter (i -> bits[i] > 0)
      .forEach (result::fastSet);

    return new BinaryVector (hyperspace, result);
  }
}
