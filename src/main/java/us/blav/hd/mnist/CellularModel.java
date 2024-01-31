package us.blav.hd.mnist;

import jakarta.inject.Inject;
import java.util.stream.IntStream;

import com.google.inject.assistedinject.Assisted;
import us.blav.hd.BinaryVector;
import us.blav.hd.Bundler;
import us.blav.hd.Combiner;
import us.blav.hd.GrayEncoder;
import us.blav.hd.Hyperspace;
import us.blav.hd.mnist.DatasetLoader.Digit;
import us.blav.hd.reca.Rule;
import us.blav.hd.reca.Transition;

import static us.blav.hd.mnist.DatasetLoader.Digit.PIXEL_COUNT;

public class CellularModel extends AbstractModel {

  private final GrayEncoder grayEncoder;

  private final Transition transition;

  private final int reservoirDepth;

  private final BinaryVector[] features;

  interface Factory {

    CellularModel create (int reservoirDepth, Rule rule);

  }

  @Inject
  protected CellularModel (
    @Assisted int reservoirDepth,
    @Assisted Rule rule,
    GrayEncoder grayEncoder,
    Transition.Factory transitionFactory,
    Hyperspace.Factory hyperspaceFactory
  ) {
    super (hyperspaceFactory.create (PIXEL_COUNT));
    this.reservoirDepth = reservoirDepth;
    this.grayEncoder = grayEncoder;
    this.transition = transitionFactory.create (rule);
    this.features = IntStream.range (0, PIXEL_COUNT)
      .mapToObj (ignore -> hyperspace.newRandom ())
      .toArray (BinaryVector[]::new);
  }

  @Override
  protected BinaryVector encode (Digit digit) {
    byte[] input = new byte[PIXEL_COUNT];
    IntStream.range (0, PIXEL_COUNT).forEach (i -> input[i] = digit.pixel (i, grayEncoder));
    Bundler reservoir = hyperspace.newBundler ();
    BinaryVector current = hyperspace.newZero ();
    for (int i = 0; i < Byte.SIZE; i++) {
      byte mask = (byte) (1 << i);
      BinaryVector vector = hyperspace.newZero ();
      for (int j = 0; j < PIXEL_COUNT; j++)
        vector.bits ().set (j, (input[j] & mask) > 0);

      Combiner combiner = hyperspace.newCombiner ();
      combiner.add (transition.next (vector));
      combiner.add (current);
      current = combiner.reduce ();
      reservoir.add (current);
    }

    for (int i = 0; i < reservoirDepth; i++) {
      current = transition.next (current);
      reservoir.add (current);
    }

    BinaryVector encoded = reservoir.reduce ();
    Bundler bundler = hyperspace.newBundler ();
    IntStream.range (0, PIXEL_COUNT).forEach (i -> {
      if (encoded.bits ().get (i))
        bundler.add (features[i]);
    });

    return bundler.reduce ();
  }
}
