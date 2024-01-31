package us.blav.hd.util;

import lombok.Value;

@Value
public class XYZPoint {

  double x;

  double y;

  double z;

  /**
   * z is defaulted to zero.
   */
  public XYZPoint (double x, double y) {
    this.x = x;
    this.y = y;
    this.z = 0;
  }

  /**
   * Default constructor
   */
  public XYZPoint (double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode () {
    return 31 * (int) (this.getX () + this.getY () + this.getZ ());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals (Object obj) {
    if (obj == null)
      return false;
    if (! (obj instanceof XYZPoint xyzPoint))
      return false;

    if (Double.compare (this.getX (), xyzPoint.getX ()) != 0)
      return false;
    if (Double.compare (this.getY (), xyzPoint.getY ()) != 0)
      return false;
    return Double.compare (this.getZ (), xyzPoint.getZ ()) == 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString () {
    return "(" +
      getX () + ", " +
      getY () + ", " +
      getZ () +
      ")";
  }
}
