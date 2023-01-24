package us.blav.hd;

import org.apache.lucene.util.OpenBitSet;
import us.blav.hd.util.OpenBitSetEnh;

public class Rotator {

  private final Hyperspace hyperspace;

  private final int rotation;

  public Rotator (Hyperspace hyperspace, int rotation) {
    this.hyperspace = hyperspace;
    this.rotation = rotation;
  }

  public BinaryVector rotate (BinaryVector vector) {
    if (vector.hyperspace () != hyperspace)
      throw new IllegalArgumentException ();

    int dimensions = hyperspace.dimensions ();
    if (rotation % dimensions == 0)
      return vector;

    OpenBitSetEnh result = new OpenBitSetEnh (dimensions);
    for (int i = 0; i < dimensions; i++)
      if (vector.bits ().get (i))
        result.fastSet ((i + rotation + dimensions) % dimensions);

    return new BinaryVector (hyperspace, result);
  }
}
