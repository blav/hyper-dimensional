package us.blav.hd.reca;

import javax.inject.Inject;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.assistedinject.Assisted;
import lombok.Getter;
import lombok.Value;
import us.blav.hd.BinaryVector;
import us.blav.hd.Hyperspace;
import us.blav.hd.util.BitString;

import static java.lang.Math.floorMod;
import static java.lang.Short.SIZE;
import static java.lang.Short.toUnsignedInt;

@Value
public class Transition {

  short[] transitions;

  @Getter
  Rule rule;

  @Inject
  public Transition (@Assisted Rule rule) {
    this.rule = rule;
    int blocks = 1 << SIZE;
    transitions = new short[blocks * 4];
    for (int left = 0; left < 2; left++) {
      for (int right = 0; right < 2; right++) {
        for (int block = 0; block < blocks; block++) {
          BitString in = new BitString (SIZE * 3);
          in.set (SIZE - 1, left != 0);
          in.set (2 * SIZE, right != 0);
          in.setShortWord (1, (short) block);
          transitions[(left + right * 2) * blocks + block] = slowNext (in).getShortWord (1);
        }
      }
    }
  }

  @VisibleForTesting
  BitString slowNext (BitString bits) {
    int dimensions = SIZE * 3;
    BitString next = new BitString (dimensions);
    for (int i = 0; i < dimensions; i++)
      next.set (i, rule.getState (
        bits.get (floorMod (i - 1, dimensions)),
        bits.get (i),
        bits.get (floorMod (i + 1, dimensions))));

    return next;
  }

  public BinaryVector next (BinaryVector vector) {
    Hyperspace hyperspace = vector.hyperspace ();
    if (hyperspace.dimensions () % SIZE > 0)
      throw new IllegalArgumentException ("dimensions must be a multiple of " + SIZE);

    int blocks = 1 << SIZE;
    BinaryVector next = hyperspace.newZero ();
    BitString current = vector.bits ();
    BitString nextBits = next.bits ();
    int dimensions = hyperspace.dimensions ();
    for (int block = 0; block < dimensions / SIZE; block++) {
      int left = nextBits.get (floorMod (block * SIZE - 1, dimensions)) ? 1 : 0;
      int right = nextBits.get (floorMod ((block + 1) * SIZE, dimensions)) ? 1 : 0;
      nextBits.setShortWord (block, transitions[(left + right * 2) * blocks + toUnsignedInt (current.getShortWord (block))]);
    }

    return next;
  }

  public interface Factory {

    Transition create (Rule rule);

  }
}
