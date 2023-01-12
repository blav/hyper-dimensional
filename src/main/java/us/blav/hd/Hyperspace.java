package us.blav.hd;

import com.google.common.annotations.VisibleForTesting;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.lucene.util.OpenBitSet;

import static java.util.stream.IntStream.range;

@Getter
@Accessors (fluent = true)
public class Hyperspace {

  private final int dimensions;

  private final RandomGenerator randomGenerator;

  private final Cosine cosine;


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

  public BinaryVector newVector (int... bits) {
    if (bits.length != dimensions)
      throw new IllegalArgumentException ();

    OpenBitSet result = new OpenBitSet (dimensions);
    range (0, dimensions)
      .filter (i -> bits[i] > 0)
      .forEach (result::fastSet);

    return new BinaryVector (this, result);
  }

  public BinaryVector newRandom () {
    OpenBitSet bits = new OpenBitSet (dimensions);
    range (0, dimensions)
      .filter (i -> randomGenerator.nextBoolean ())
      .forEach (bits::fastSet);

    return new BinaryVector (this, bits);
  }
}
