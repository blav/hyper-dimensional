package us.blav.hd.reca;

import com.google.common.annotations.VisibleForTesting;
import us.blav.hd.BinaryVector;
import us.blav.hd.Hyperspace;
import us.blav.hd.util.OpenBitSetEnh;

import static java.lang.Math.floorMod;
import static java.lang.Short.SIZE;
import static java.lang.Short.toUnsignedInt;

public class Transition {

  private final short[] transitions;

  private final Hyperspace hyperspace;

  private final Rule rule;

  public Transition (Hyperspace hyperspace, Rule rule) {
    if (hyperspace.dimensions () % Long.SIZE > 0)
      throw new IllegalArgumentException ("dimensions must be a multiple of " + Long.SIZE);

    this.hyperspace = hyperspace;
    this.rule = rule;
    int blocks = 1 << SIZE;
    transitions = new short[blocks * 4];
    for (int left = 0; left < 2; left++) {
      for (int right = 0; right < 2; right++) {
        for (int block = 0; block < blocks; block++) {
          OpenBitSetEnh in = new OpenBitSetEnh (SIZE * 3);
          in.set (SIZE - 1, left != 0);
          in.set (2 * SIZE, right != 0);
          in.setWord (1, block);
          transitions[(left + right * 2) * blocks + block] = (short) slowNext (in).getWord (1);
        }
      }
    }
  }

  @VisibleForTesting
  OpenBitSetEnh slowNext (OpenBitSetEnh bits) {
    int dimensions = SIZE * 3;
    OpenBitSetEnh next = new OpenBitSetEnh (dimensions);
    for (int i = 0; i < dimensions; i++)
      next.set (i, rule.getState (
        bits.get (floorMod (i - 1, dimensions)),
        bits.get (i),
        bits.get (floorMod (i + 1, dimensions))));

    return next;
  }

  public BinaryVector next (BinaryVector vector) {
    int blocks = 1 << SIZE;
    BinaryVector next = hyperspace.newZero ();
    OpenBitSetEnh current = vector.bits ();
    OpenBitSetEnh nextBits = next.bits ();
    int dimensions = hyperspace.dimensions ();
    for (int block = 0; block < dimensions / SIZE; block++) {
      int left = nextBits.get (floorMod (block * SIZE - 1, dimensions)) ? 1 : 0;
      int right = nextBits.get (floorMod ((block + 1) * SIZE, dimensions)) ? 1 : 0;
      nextBits.setShortWord (block, transitions[(left + right * 2) * blocks + toUnsignedInt (current.getShortWord (block))]);
    }

    return next;
  }
}
