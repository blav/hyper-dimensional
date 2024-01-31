package us.blav.hd;

import jakarta.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import us.blav.hd.util.KDTree.KDSpace;

public class BinaryKDSpace implements KDSpace<BinaryVector> {

  private final Hyperspace hyperspace;

  private final Hamming hamming;

  public interface Factory {

    BinaryKDSpace create (Hyperspace hyperspace);

  }

  @Inject
  public BinaryKDSpace (@Assisted Hyperspace hyperspace) {
    this.hyperspace = hyperspace;
    this.hamming = hyperspace.hamming ();
  }

  @Override
  public int getK () {
    return hyperspace.dimensions ();
  }

  @Override
  public double difference (BinaryVector o1, BinaryVector o2, int axis) {
    boolean b1 = o1.bits ().get (axis);
    boolean b2 = o2.bits ().get (axis);
    return b1 == b2 ? 0. : b1 ? 1. : - 1.;
  }

  @Override
  public double distance (BinaryVector v1, BinaryVector v2) {
    return hamming.apply (v1, v2);
  }
}
