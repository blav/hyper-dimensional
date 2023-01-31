package us.blav.hd;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import lombok.NonNull;
import us.blav.hd.util.BitString;

public class Rotator {

  @NonNull
  private final Hyperspace hyperspace;

  private final int rotation;

  public interface Factory {

    Rotator create (@NonNull Hyperspace hyperspace, int rotation);

  }

  @Inject
  public Rotator (@NonNull @Assisted Hyperspace hyperspace, @Assisted int rotation) {
    this.hyperspace = hyperspace;
    this.rotation = rotation;
  }

  public BinaryVector rotate (BinaryVector vector) {
    if (vector.hyperspace () != hyperspace)
      throw new IllegalArgumentException ();

    int dimensions = hyperspace.dimensions ();
    if (rotation % dimensions == 0)
      return vector;

    BitString result = new BitString (dimensions);
    for (int i = 0; i < dimensions; i++)
      if (vector.bits ().get (i))
        result.fastSet ((i + rotation + dimensions) % dimensions);

    return new BinaryVector (hyperspace, result);
  }
}
