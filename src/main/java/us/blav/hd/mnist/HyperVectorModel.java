package us.blav.hd.mnist;

import java.util.List;

import lombok.Builder;
import us.blav.hd.BinaryVector;
import us.blav.hd.Bundler;
import us.blav.hd.Hyperspace;
import us.blav.hd.mnist.DatasetLoader.Digit;

import static java.util.stream.IntStream.range;
import static us.blav.hd.mnist.DatasetLoader.PIXEL_COUNT;

public class HyperVectorModel extends AbstractModel {

  private final List<BinaryVector> coordinates;

  private final List<BinaryVector> values;

  @Builder
  public HyperVectorModel (int dimensions) {
    super (new Hyperspace (dimensions));
    coordinates = range (0, PIXEL_COUNT)
      .mapToObj (i -> hyperspace.newRandom ())
      .toList ();

    values = range (0, 256)
      .mapToObj (i -> hyperspace.newRandom ())
      .toList ();
  }

  @Override
  protected BinaryVector encode (Digit digit) {
    Bundler bundler = hyperspace.newBundler ();
    range (0, PIXEL_COUNT).forEach (i ->
      bundler.add (hyperspace.newCombiner ()
        .add (values.get (digit.pixel (i)))
        .add (coordinates.get (i))
        .reduce ()));

    return bundler.reduce ();
  }
}
