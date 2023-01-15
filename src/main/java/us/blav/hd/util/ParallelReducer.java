package us.blav.hd.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;

public class ParallelReducer<KEY, ELEMENT, ACCUMULATOR, RESULT> {

  private class Accumulator {

    private final ACCUMULATOR accumulator;

    private final BlockingQueue<Message> queue;

    private final Future<ACCUMULATOR> reduced;

    private Accumulator (KEY key) {
      this.accumulator = accumulatorFactory.get ();
      this.queue = new ArrayBlockingQueue<> (queueSize);
      this.reduced = executorService.submit (() -> {
        while (true) {
          Message message = queue.take ();
          if (message == poison) {
            return accumulator;
          } else {
            combiner.accept (accumulator, message.getElement ());
          }
        }
      });
    }

    @SneakyThrows (InterruptedException.class)
    public void accumulate (Message message) {
      this.queue.put (message);
    }

    @SneakyThrows ({ InterruptedException.class, ExecutionException.class })
    private ACCUMULATOR getAccumulator () {
      return reduced.get ();
    }
  }

  @Value
  @AllArgsConstructor
  private class Message {

    ELEMENT element;

  }

  private final Function<ELEMENT, KEY> keyMapper;

  private final Supplier<ACCUMULATOR> accumulatorFactory;

  private final BiConsumer<ACCUMULATOR, ELEMENT> combiner;

  private final Function<Map<KEY, ACCUMULATOR>, RESULT> reducer;

  private final int queueSize;

  private final Map<KEY, Accumulator> processors;

  private final ExecutorService executorService;

  private final Message poison;

  @Builder
  private ParallelReducer (
    @NonNull Function<ELEMENT, KEY> keyMapper,
    @NonNull Supplier<ACCUMULATOR> accumulatorFactory,
    @NonNull BiConsumer<ACCUMULATOR, ELEMENT> combiner,
    @NonNull Function<Map<KEY, ACCUMULATOR>, RESULT> reducer,
    int threads,
    int queueSize
  ) {
    this.keyMapper = keyMapper;
    this.accumulatorFactory = accumulatorFactory;
    this.combiner = combiner;
    this.reducer = reducer;
    this.queueSize = queueSize;
    this.processors = new ConcurrentHashMap<> ();
    this.executorService = Executors.newFixedThreadPool (threads);
    this.poison = new Message (null);
  }

  public void accumulate (@NonNull ELEMENT element) {
    processors
      .computeIfAbsent (keyMapper.apply (element), Accumulator::new)
      .accumulate (new Message (element));
  }

  @SneakyThrows (InterruptedException.class)
  public RESULT reduce () {
    try {
      processors.values ().forEach (processor -> processor.accumulate (poison));
    } finally {
      executorService.shutdown ();
      //noinspection ResultOfMethodCallIgnored
      executorService.awaitTermination (1, TimeUnit.MINUTES);
    }

    return reducer.apply (processors.entrySet ().stream ()
      .map (entry -> Map.entry (entry.getKey (), entry.getValue ().getAccumulator ()))
      .collect (Collectors.toMap (Entry::getKey, Entry::getValue)));
  }
}
