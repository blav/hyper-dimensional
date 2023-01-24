package us.blav.hd;

import com.google.common.annotations.VisibleForTesting;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.apache.lucene.util.OpenBitSet;
import us.blav.hd.util.OpenBitSetEnh;

import static java.util.stream.IntStream.range;

@Getter
@Accessors (fluent = true)
public class Hyperspace {

  private final int dimensions;

  private final RandomGenerator randomGenerator;

  private final Cosine cosine;

  private final Hamming hamming;

  public Hyperspace (int dimensions) {
    this (dimensions, new RandomGenerator ());
  }

  @VisibleForTesting
  Hyperspace (int dimensions, RandomGenerator randomGenerator) {
    if (dimensions <= 0)
      throw new IllegalArgumentException ("dimensions must > 0");

    this.dimensions = dimensions;
    this.randomGenerator = randomGenerator;
    this.cosine = new Cosine (this);
    this.hamming = new Hamming (this);
  }

  public Combiner newCombiner () {
    return new Combiner (this);
  }

  public Bundler newBundler () {
    return new Bundler (this);
  }

  public Rotator newRotator (int rotation) {
    return new Rotator (this, rotation);
  }

  public BinaryVector newZero () {
    return new BinaryVector (this);
  }

  public Cosine cosine () {
    return cosine;
  }

  public Hamming hamming () {
    return hamming;
  }

  public BinaryVector newVector (int... bits) {
    if (bits.length != dimensions)
      throw new IllegalArgumentException ();

    OpenBitSetEnh result = new OpenBitSetEnh (dimensions);
    range (0, dimensions)
      .filter (i -> bits[i] > 0)
      .forEach (result::fastSet);

    return new BinaryVector (this, result);
  }

  public BinaryVector newRandom () {
    OpenBitSetEnh bits = new OpenBitSetEnh (dimensions);
    range (0, dimensions)
      .filter (i -> randomGenerator.nextBoolean ())
      .forEach (bits::fastSet);

    return new BinaryVector (this, bits);
  }

  public void checkCardinality (@NonNull BinaryVector vector) {
    if (vector.hyperspace () != this)
      throw new RuntimeException ("vector lies in another space");
  }
}
