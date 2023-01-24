package us.blav.hd;

import lombok.NonNull;
import org.apache.lucene.util.OpenBitSet;
import us.blav.hd.util.OpenBitSetEnh;

public class Combiner implements Accumulator<Combiner> {

  private final Hyperspace hyperspace;

  private OpenBitSetEnh accumulator;

  public Combiner (Hyperspace hyperspace) {
    this.hyperspace = hyperspace;
  }

  public Combiner add (@NonNull BinaryVector vector) {
    if (vector.hyperspace () != hyperspace)
      throw new IllegalArgumentException ();

    if (accumulator == null) {
      accumulator = (OpenBitSetEnh) vector.bits ().clone ();
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
