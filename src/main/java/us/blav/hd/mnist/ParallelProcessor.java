package us.blav.hd.mnist;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;

import static java.util.concurrent.TimeUnit.MINUTES;

public class ParallelProcessor<KEY, ELEMENT, RESULT> {

  private final Function<ELEMENT, KEY> keyMapper;

  private final int queueSize;

  private final Function<ELEMENT, RESULT> processor;

  private final Consumer<RESULT> output;

  private final ExecutorService executorService;

  private final Map<KEY, Processor> processors;

  private final Message poison;

  @Value
  @AllArgsConstructor
  private class Message {

    ELEMENT element;

  }

  private class Processor {

    private final BlockingQueue<Message> queue;

    private Processor () {
      queue = new ArrayBlockingQueue<> (queueSize);
      executorService.submit (() -> {
        while (true) {
          Message message = queue.take ();
          if (message == poison) {
            return null;
          } else {
            output.accept (processor.apply (message.getElement ()));
          }
        }
      });
    }

    @SneakyThrows (InterruptedException.class)
    private void process (Message message) {
      queue.put (message);
    }
  }

  @Builder
  private ParallelProcessor (
    @NonNull Function<ELEMENT, KEY> keyMapper,
    @NonNull Function<ELEMENT, RESULT> processor,
    @NonNull Consumer<RESULT> output,
    int threads,
    int queueSize
  ) {
    this.processor = processor;
    this.executorService = Executors.newFixedThreadPool (threads);
    this.output = output;
    this.keyMapper = keyMapper;
    this.queueSize = queueSize;
    this.processors = new ConcurrentHashMap<> ();
    this.poison = new Message (null);
  }

  public void process (ELEMENT element) {
    processors
      .computeIfAbsent (keyMapper.apply (element), key -> new Processor ())
      .process (new Message (element));
  }

  @SneakyThrows (InterruptedException.class)
  public boolean shutdown () {
    processors.values ().forEach (processor -> processor.process (poison));
    executorService.shutdown ();
    return executorService.awaitTermination (1, MINUTES);
  }
}
