package us.blav.hd;

import javax.inject.Inject;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.assistedinject.Assisted;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import us.blav.hd.util.BitString;

import static java.util.stream.IntStream.range;

@Getter
@Accessors (fluent = true)
public class Hyperspace {

  private final int dimensions;

  private final RandomGenerator randomGenerator;

  private final Cosine cosine;

  private final Hamming hamming;

  public interface Factory {

    Hyperspace create (int dimensions);

  }

  private final Combiner.Factory combinerFactory;

  private final Bundler.Factory bundlerFactory;

  private final Rotator.Factory rotatorFactory;

  @VisibleForTesting
  public Hyperspace (int dimensions) {
    this (dimensions, new RandomGenerator (), null, null, null, null, null);
  }

  @Inject
  @VisibleForTesting
  Hyperspace (
    @Assisted int dimensions,
    RandomGenerator randomGenerator,
    Combiner.Factory combinerFactory,
    Bundler.Factory bundlerFactory,
    Rotator.Factory rotatorFactory,
    Cosine.Factory cosineFactory,
    Hamming.Factory hammingFactory
  ) {
    this.combinerFactory = combinerFactory;
    this.bundlerFactory = bundlerFactory;
    this.rotatorFactory = rotatorFactory;
    if (dimensions <= 0)
      throw new IllegalArgumentException ("dimensions must > 0");

    this.dimensions = dimensions;
    this.randomGenerator = randomGenerator;
    this.cosine = cosineFactory.create (this);
    this.hamming = hammingFactory.create (this);
  }

  public Combiner newCombiner () {
    return combinerFactory.create (this);
  }

  public Bundler newBundler () {
    return bundlerFactory.create (this);
  }

  public Rotator newRotator (int rotation) {
    return rotatorFactory.create (this, rotation);
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

    BitString result = new BitString (dimensions);
    range (0, dimensions)
      .filter (i -> bits[i] > 0)
      .forEach (result::fastSet);

    return new BinaryVector (this, result);
  }

  public BinaryVector newRandom () {
    BitString bits = new BitString (dimensions);
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
