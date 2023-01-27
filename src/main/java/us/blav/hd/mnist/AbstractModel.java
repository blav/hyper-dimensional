package us.blav.hd.mnist;

import javax.inject.Inject;
import java.time.Duration;
import java.util.function.Consumer;

import lombok.Getter;
import us.blav.hd.BinaryVector;
import us.blav.hd.ClassifierModel;
import us.blav.hd.Hyperspace;
import us.blav.hd.mnist.DatasetLoader.Digit;

import static us.blav.hd.mnist.DatasetLoader.Dataset.t10k;
import static us.blav.hd.mnist.DatasetLoader.Dataset.train;

public abstract class AbstractModel {

  @Getter
  protected final Hyperspace hyperspace;

  @Inject
  protected DatasetLoader loader;

  protected AbstractModel (Hyperspace hyperspace) {
    this.hyperspace = hyperspace;
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

  protected abstract BinaryVector encode (Digit digit);

}
