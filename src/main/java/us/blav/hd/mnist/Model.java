package us.blav.hd.mnist;

import java.util.List;
import java.util.Map.Entry;
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
    ParallelReducer<Integer, Digit, Bundler, List<BinaryVector>> parallelReducer = ParallelReducer.<Integer, Digit, Bundler, List<BinaryVector>>builder ()
      .threads (10)
      .queueSize (10)
      .keyMapper (Digit::label)
      .accumulatorFactory (hyperspace::newBundler)
      .combiner ((bundler, digit) -> bundler.add (encode (digit)))
      .reducer (map -> map.entrySet ().stream ()
        .sorted (Entry.comparingByKey ())
        .map (Entry::getValue)
        .map (Bundler::reduce)
        .collect (Collectors.toList ()))
      .build ();

    try (Timer ignore = new Timer (duration -> System.out.printf ("training took %ds%n", duration.toSeconds ()))) {
      new MNIST ().load (train).forEach (parallelReducer::accumulate);
      return new TrainedModel (this, parallelReducer.reduce ());
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
