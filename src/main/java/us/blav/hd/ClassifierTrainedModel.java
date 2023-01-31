package us.blav.hd;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import lombok.NonNull;
import us.blav.hd.util.ParallelProcessor;

public class ClassifierTrainedModel<ELEMENT, KEY extends Comparable<? super KEY>> {

  private final ClassifierModel<ELEMENT, KEY> model;

  private final AssociativeMemory<KEY> memory;

  public ClassifierTrainedModel (@NonNull ClassifierModel<ELEMENT, KEY> model, @NonNull Map<BinaryVector, KEY> classes) {
    this.model = model;
    this.memory = model.getHyperspace ()
      .<KEY>newAssociativeMemory ()
      .add (classes);
  }

  public KEY infer (ELEMENT element) {
    return memory.lookup (model.getEncoder ().apply (element)).getValue ();
  }

  public double computeAccuracy () {
    return computeAccuracy (Long.MAX_VALUE);
  }

  public double computeAccuracy (long limit) {
    AtomicInteger success = new AtomicInteger ();
    AtomicInteger count = new AtomicInteger ();

    ParallelProcessor<KEY, ELEMENT, Boolean> processor =
      ParallelProcessor.<KEY, ELEMENT, Boolean>builder ()
        .threads (model.getThreadCount ())
        .queueSize (model.getQueueSize ())
        .keyMapper (model.getKeyMapper ())
        .processor (digit -> infer (digit) == model.getKeyMapper ().apply (digit))
        .output (result -> {
          count.incrementAndGet ();
          if (result)
            success.incrementAndGet ();
        })
        .build ();

    try (Stream<ELEMENT> dataset = model.getValidationDataset ().get ()) {
      dataset
        .limit (limit)
        .forEach (processor::process);
    } finally {
      processor.shutdown ();
    }

    return 100. * success.get () / count.get ();
  }
}
