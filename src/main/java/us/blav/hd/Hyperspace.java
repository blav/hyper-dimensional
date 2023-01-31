package us.blav.hd;

import javax.inject.Inject;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.assistedinject.Assisted;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import us.blav.hd.util.BitHacks;

@Getter
@Accessors (fluent = true)
public class Hyperspace {

  private final int dimensions;

  private final RandomGenerator randomGenerator;

  private final Cosine cosine;

  private final Hamming hamming;

  private final Combiner.Factory combinerFactory;

  private final Bundler.Factory bundlerFactory;

  private final Rotator.Factory rotatorFactory;

  private final BinaryVectorFactory binaryVectorFactory;

  public interface Factory {

    Hyperspace create (int dimensions);

  }

  private final BitShuffler.Factory bitShufflerFactory;

  private final ByteShuffler.Factory byteShufflerFactory;

  private final AssociativeMemory.Factory associativeMemoryFactory;

  @VisibleForTesting
  public Hyperspace (int dimensions) {
    this (dimensions, new RandomGenerator ());
  }

  @VisibleForTesting
  public Hyperspace (int dimensions, RandomGenerator randomGenerator) {
    this (dimensions, randomGenerator,
      Combiner::new, Bundler::new, Rotator::new, h -> new Cosine (h, new BitHacks ()),
      h -> new Hamming (h, new BitHacks ()), BitShuffler::new, ByteShuffler::new,
      new AssociativeMemory.Factory (), new BinaryVectorFactory ());
  }

  @Inject
  Hyperspace (
    @Assisted int dimensions,
    @NonNull RandomGenerator randomGenerator,
    @NonNull Combiner.Factory combinerFactory,
    @NonNull Bundler.Factory bundlerFactory,
    @NonNull Rotator.Factory rotatorFactory,
    @NonNull Cosine.Factory cosineFactory,
    @NonNull Hamming.Factory hammingFactory,
    @NonNull BitShuffler.Factory bitShufflerFactory,
    @NonNull ByteShuffler.Factory byteShufflerFactory,
    @NonNull AssociativeMemory.Factory associativeMemoryFactory,
    @NonNull BinaryVectorFactory binaryVectorFactory
  ) {
    if (dimensions <= 0)
      throw new IllegalArgumentException ("dimensions must > 0");

    this.bitShufflerFactory = bitShufflerFactory;
    this.byteShufflerFactory = byteShufflerFactory;
    this.combinerFactory = combinerFactory;
    this.bundlerFactory = bundlerFactory;
    this.rotatorFactory = rotatorFactory;
    this.binaryVectorFactory = binaryVectorFactory;
    this.dimensions = dimensions;
    this.randomGenerator = randomGenerator;
    this.cosine = cosineFactory.create (this);
    this.hamming = hammingFactory.create (this);
    this.associativeMemoryFactory = associativeMemoryFactory;
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

  public BitShuffler newBitShuffler () {
    return bitShufflerFactory.create (this);
  }

  public ByteShuffler newByteShuffler () {
    return byteShufflerFactory.create (this);
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
    return binaryVectorFactory.newVector (this, bits);
  }

  public BinaryVector newRandom () {
    return binaryVectorFactory.newRandom (this);
  }

  public <METADATA> AssociativeMemory<METADATA> newAssociativeMemory () {
    return associativeMemoryFactory.create (this);
  }
}
