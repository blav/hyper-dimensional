package us.blav.hd;

import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import us.blav.hd.util.ParallelReducer;
import us.blav.hd.util.Timer;

public class ClassifierModel<ELEMENT, KEY extends Comparable<? super KEY>> {

  @Getter
  private final Hyperspace hyperspace;

  @Getter
  private final Function<ELEMENT, BinaryVector> encoder;

  @Getter
  private final Function<ELEMENT, KEY> keyMapper;

  private final Supplier<Stream<ELEMENT>> trainDataset;

  @Getter
  private final Supplier<Stream<ELEMENT>> validateDataset;

  private final Consumer<Duration> trainingDuration;

  @Getter
  private final int threadCount;

  @Getter
  private final int queueSize;


  @Builder
  private ClassifierModel (
    @NonNull Hyperspace hyperspace,
    @NonNull Function<ELEMENT, BinaryVector> encoder,
    @NonNull Function<ELEMENT, KEY> keyMapper,
    @NonNull Supplier<Stream<ELEMENT>> trainDataset,
    @NonNull Supplier<Stream<ELEMENT>> validateDataset,
    Consumer<Duration> trainingDuration,
    int threadCount,
    int queueSize
  ) {
    this.hyperspace = hyperspace;
    this.encoder = encoder;
    this.keyMapper = keyMapper;
    this.trainDataset = trainDataset;
    this.validateDataset = validateDataset;
    this.trainingDuration = trainingDuration;
    this.threadCount = threadCount;
    this.queueSize = queueSize;
  }

  public ClassifierTrainedModel<ELEMENT, KEY> train () {
    return train (Long.MAX_VALUE);
  }

  public ClassifierTrainedModel<ELEMENT, KEY> train (long limit) {
    ParallelReducer<KEY, ELEMENT, Bundler, Map<KEY, BinaryVector>> parallelReducer =
      ParallelReducer.<KEY, ELEMENT, Bundler, Map<KEY, BinaryVector>>builder ()
        .threads (threadCount)
        .queueSize (queueSize)
        .keyMapper (keyMapper)
        .accumulatorFactory (hyperspace::newBundler)
        .combiner ((bundler, digit) -> bundler.add (encoder.apply (digit)))
        .reducer (map -> map.entrySet ().stream ()
          .sorted (Entry.comparingByKey ())
          .map (entry -> Map.entry (entry.getKey (), entry.getValue ().reduce ()))
          .collect (Collectors.toMap (Entry::getKey, Entry::getValue)))
        .build ();

    try (
      Stream<ELEMENT> dataset = trainDataset.get ();
      Timer ignore = new Timer (trainingDuration)
    ) {
      dataset
        .limit (limit)
        .forEach (parallelReducer::accumulate);
      return new ClassifierTrainedModel<> (this, parallelReducer.reduce ());
    }
  }
}
