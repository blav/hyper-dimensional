package us.blav.hd.mnist;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.Getter;
import us.blav.hd.BinaryVector;
import us.blav.hd.Bundler;
import us.blav.hd.Hyperspace;
import us.blav.hd.mnist.MNIST.Digit;
import us.blav.hd.util.Timer;

import static java.util.stream.IntStream.range;
import static us.blav.hd.mnist.MNIST.Dataset.train;
import static us.blav.hd.mnist.MNIST.PIXEL_COUNT;

public class Model {

  @Getter
  private final Hyperspace hyperspace;

  private final List<BinaryVector> coordinates;

  private final List<BinaryVector> values;


  public Model (int dimensions) {
    hyperspace = new Hyperspace (dimensions);
    coordinates = range (0, PIXEL_COUNT)
      .mapToObj (i -> hyperspace.newRandom ())
      .toList ();

    values = range (0, 256)
      .mapToObj (i -> hyperspace.newRandom ())
      .toList ();
  }

  public TrainedModel train () {
    try (Timer ignore = new Timer (duration -> System.out.printf ("training took %d%n", duration.toSeconds ()))) {
      List<Bundler> classes = range (0, 10)
        .mapToObj (i -> hyperspace.newBundler ())
        .toList ();

      AtomicInteger count = new AtomicInteger ();
      new MNIST ().load (train).forEach (digit -> {
        classes.get (digit.label ()).add (encode (digit));
        System.out.printf ("image %d%n", count.incrementAndGet ());
      });

      List<BinaryVector> trainedClasses = classes.stream ()
        .map (Bundler::reduce)
        .collect (Collectors.toList ());

      return new TrainedModel (this, trainedClasses);
    }
  }

  public BinaryVector encode (Digit digit) {
    Bundler bundler = hyperspace.newBundler ();
    range (0, PIXEL_COUNT).forEach (i ->
      bundler.add (hyperspace.newCombiner ()
        .add (values.get (digit.pixel (i)))
        .add (coordinates.get (i))
        .reduce ()));

    return bundler.reduce ();
  }
}
