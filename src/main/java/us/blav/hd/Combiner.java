package us.blav.hd;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import lombok.NonNull;
import us.blav.hd.util.BitString;

public class Combiner implements Accumulator<Combiner> {

  @NonNull
  private final Hyperspace hyperspace;

  private BitString accumulator;

  public interface Factory {

    Combiner create (@NonNull Hyperspace hyperspace);

  }

  @Inject
  public Combiner (@NonNull @Assisted Hyperspace hyperspace) {
    this.hyperspace = hyperspace;
  }

  public Combiner add (@NonNull BinaryVector vector) {
    if (vector.hyperspace () != hyperspace)
      throw new IllegalArgumentException ();

    if (accumulator == null) {
      accumulator = (BitString) vector.bits ().clone ();
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
