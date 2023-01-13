package us.blav.hd;

import lombok.NonNull;
import org.apache.lucene.util.OpenBitSet;

public class Combiner implements Accumulator<Combiner> {

  private final Hyperspace hyperspace;

  private OpenBitSet accumulator;

  public Combiner (Hyperspace hyperspace) {
    this.hyperspace = hyperspace;
  }

  public Combiner add (@NonNull BinaryVector vector) {
    if (vector.hyperspace () != hyperspace)
      throw new IllegalArgumentException ();

    if (accumulator == null) {
      accumulator = (OpenBitSet) vector.bits ().clone ();
    } else {
      accumulator.xor (vector.bits ());
    }

    return this;
  }

  public BinaryVector reduce () {
    if (accumulator == null)
      throw new IllegalStateException ("call add before calling reduce");

    BinaryVector vector = new BinaryVector (hyperspace, accumulator);
    accumulator = null;
    return vector;
  }
}