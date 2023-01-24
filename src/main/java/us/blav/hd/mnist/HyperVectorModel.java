package us.blav.hd.mnist;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.annotations.VisibleForTesting;
import lombok.Builder;
import lombok.Getter;
import us.blav.hd.BinaryVector;
import us.blav.hd.Bundler;
import us.blav.hd.ClassifierModel;
import us.blav.hd.Hyperspace;
import us.blav.hd.mnist.DatasetLoader.Digit;

import static java.util.stream.IntStream.range;
import static us.blav.hd.mnist.DatasetLoader.Dataset.t10k;
import static us.blav.hd.mnist.DatasetLoader.Dataset.train;
import static us.blav.hd.mnist.DatasetLoader.PIXEL_COUNT;

public class HyperVectorModel {

  @Getter
  private final Hyperspace hyperspace;

  private final List<BinaryVector> coordinates;

  private final List<BinaryVector> values;

  private final DatasetLoader loader;

  @Builder
  public HyperVectorModel (int dimensions) {
    loader = new DatasetLoader ();
    hyperspace = new Hyperspace (dimensions);
    coordinates = range (0, PIXEL_COUNT)
      .mapToObj (i -> hyperspace.newRandom ())
      .toList ();

    values = range (0, 256)
      .mapToObj (i -> hyperspace.newRandom ())
      .toList ();
  }

  public ClassifierModel<Digit, Integer> newModel () {
    Consumer<Duration> durationConsumer = duration -> System.out.printf (
      "training for %d dimensions took %ds%n", hyperspace.dimensions (), duration.toSeconds ());

    return ClassifierModel.<Digit, Integer>builder ()
      .hyperspace (hyperspace)
      .encoder (this::encode)
      .keyMapper (Digit::label)
      .trainDataset (() -> loader.load (train))
      .validateDataset (() -> loader.load (t10k))
      .trainingDuration (durationConsumer)
      .threadCount (10)
      .queueSize (10)
      .build ();
  }

  @VisibleForTesting
  BinaryVector encode (Digit digit) {
    Bundler bundler = hyperspace.newBundler ();
    range (0, PIXEL_COUNT).forEach (i ->
      bundler.add (hyperspace.newCombiner ()
        .add (values.get (digit.pixel (i)))
        .add (coordinates.get (i))
        .reduce ()));

    return bundler.reduce ();
  }
}
