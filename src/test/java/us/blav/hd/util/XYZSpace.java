package us.blav.hd.util;

import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class XYZSpace implements KDTree.KDSpace<XYZPoint> {

  private static final ImmutableList<Function<XYZPoint, Double>> GETTERS =
    ImmutableList.of (XYZPoint::getX, XYZPoint::getY, XYZPoint::getZ);

  @Override
  public int getK () {
    return 3;
  }

  @Override
  public double distance (XYZPoint o1, XYZPoint o2) {
    return sqrt (
      pow ((o1.getX () - o2.getX ()), 2) +
        pow ((o1.getY () - o2.getY ()), 2) +
        pow ((o1.getZ () - o2.getZ ()), 2));
  }

  @Override
  public double difference (XYZPoint o1, XYZPoint o2, int axis) {
    Function<XYZPoint, Double> getter = getter (axis);
    return getter.apply (o1) - getter.apply (o2);
  }

  private static Function<XYZPoint, Double> getter (int axis) {
    return GETTERS.get (axis);
  }
}
