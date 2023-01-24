package us.blav.hd;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import lombok.NonNull;
import us.blav.hd.util.ParallelProcessor;

public class ClassifierTrainedModel<ELEMENT, KEY extends Comparable<? super KEY>> {

  private final Map<KEY, BinaryVector> classes;

  private final ClassifierModel<ELEMENT, KEY> model;

  public ClassifierTrainedModel (@NonNull ClassifierModel<ELEMENT, KEY> model, @NonNull Map<KEY, BinaryVector> classes) {
    this.classes = classes;
    this.model = model;
  }

  public KEY infer (ELEMENT element) {
    BinaryVector vector = model.getEncoder ().apply (element);
    Double similarity = null;
    KEY nearest = null;
    Metric metric = model.getHyperspace ().cosine ();
    for (Entry<KEY, BinaryVector> entry : classes.entrySet ()) {
      double s = metric.apply (vector, entry.getValue ());
      if (similarity == null || s > similarity) {
        similarity = s;
        nearest = entry.getKey ();
      }
    }

    return nearest;
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

    try (Stream<ELEMENT> dataset = model.getValidateDataset ().get ();) {
      dataset
        .limit (limit)
        .forEach (processor::process);
    } finally {
      processor.shutdown ();
    }

    return 100. * success.get () / count.get ();
  }
}
