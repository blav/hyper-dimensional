package us.blav.hd;

import org.apache.lucene.util.OpenBitSet;

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

    OpenBitSet result = new OpenBitSet (dimensions);
    for (int i = 0; i < dimensions; i++)
      if (vector.bits ().get (i))
        result.fastSet ((i + rotation + dimensions) % dimensions);

    return new BinaryVector (hyperspace, result);
  }
}
