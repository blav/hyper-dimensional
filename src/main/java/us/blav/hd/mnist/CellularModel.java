package us.blav.hd.mnist;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import us.blav.hd.BinaryVector;
import us.blav.hd.Bundler;
import us.blav.hd.ByteShuffler;
import us.blav.hd.Hyperspace;
import us.blav.hd.Reservoir;
import us.blav.hd.ReservoirFactory;
import us.blav.hd.mnist.DatasetLoader.Digit;
import us.blav.hd.reca.Rule;
import us.blav.hd.reca.Transition;
import us.blav.hd.util.GrayEncoder;
import us.blav.hd.util.OpenBitSetEnh;

import static us.blav.hd.mnist.DatasetLoader.PIXEL_COUNT;

public class CellularModel extends AbstractModel {

  private final GrayEncoder grayEncoder;

  private final Transition transition;

  private final List<ByteShuffler> byteShufflers;

  private final ReservoirFactory reservoirFactory;

  private final Hyperspace digitHyperspace;

  private final int reservoirDepth;

  protected CellularModel (int reservoirDepth, Rule rule, int shufflers) {
    super (new Hyperspace (PIXEL_COUNT * 8 * shufflers));
    this.reservoirDepth = reservoirDepth;
    this.grayEncoder = new GrayEncoder ();
    this.transition = new Transition (hyperspace, rule);
    this.digitHyperspace = new Hyperspace (PIXEL_COUNT * 8);
    this.reservoirFactory = new ReservoirFactory (digitHyperspace, shufflers);
    this.byteShufflers = IntStream.range (0, shufflers - 1)
      .mapToObj (i -> new ByteShuffler (digitHyperspace))
      .collect(Collectors.toList());;
  }

  @Override
  protected BinaryVector encode (Digit digit) {
    BinaryVector current = digitHyperspace.newZero ();
    for (int i = 0; i < PIXEL_COUNT; i++) {
      OpenBitSetEnh bits = current.bits ();
      bits.setByteWord (i, grayEncoder.encode ((byte) digit.pixel (i)));
    }

    Reservoir reservoir = reservoirFactory.newReservoir ();
    reservoir.set (0, current);
    for (int i = 0; i < byteShufflers.size (); i ++)
      reservoir.set (i + 1, byteShufflers.get (i).shuffle (current));

    BinaryVector input = reservoir.concat ();
    Bundler bundler = hyperspace.newBundler ();
    for (int d = 0; d < reservoirDepth; d++) {
      bundler.add (input);
      input = transition.next (input);
    }

    return bundler.reduce ();
  }
}
