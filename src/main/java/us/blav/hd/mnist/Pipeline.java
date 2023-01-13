package us.blav.hd.mnist;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

public class Pipeline<KEY, ELEMENT, RESULT> {

  interface LaneFactory<KEY, ELEMENT, RESULT> {

    KEY getKey (ELEMENT element);

    Lane<ELEMENT, RESULT> createLane (KEY key);

  }

  interface Lane<ELEMENT, RESULT> {

    void submit (ELEMENT element);

    RESULT reduce ();

  }

  private class LaneProcessor {

    private final Lane<ELEMENT, RESULT> lane;

    private final BlockingQueue<Message> queue;

    private final Future<RESULT> reduced;

    private LaneProcessor (KEY key) {
      this.lane = laneFactory.createLane (key);
      this.queue = new ArrayBlockingQueue<> (queueSize);
      this.reduced = executorService.submit (() -> {
        while (true) {
          Message message = queue.take ();
          if (message == poison) {
            return lane.reduce ();
          } else {
            lane.submit (message.getElement ());
          }
        }
      });
    }

    @SneakyThrows (InterruptedException.class)
    public void accumulate (Message message) {
      this.queue.put (message);
    }

    @SneakyThrows ({ InterruptedException.class, ExecutionException.class })
    private RESULT getResult () {
      return reduced.get ();
    }
  }

  private class Message {

    @Getter
    private final ELEMENT element;

    private Message (ELEMENT element) {
      this.element = element;
    }
  }

  private final LaneFactory<KEY, ELEMENT, RESULT> laneFactory;

  private final int queueSize;

  private final Map<KEY, LaneProcessor> processors;

  private final ExecutorService executorService;

  private final Message poison;

  public Pipeline (@NonNull LaneFactory<KEY, ELEMENT, RESULT> laneFactory, int threads, int queueSize) {
    this.laneFactory = laneFactory;
    this.queueSize = queueSize;
    this.processors = new ConcurrentHashMap<> ();
    this.executorService = Executors.newFixedThreadPool (threads);
    this.poison = new Message (null);
  }

  @SneakyThrows (InterruptedException.class)
  public void accumulate (@NonNull ELEMENT element) {
    processors
      .computeIfAbsent (laneFactory.getKey (element), LaneProcessor::new)
      .accumulate (new Message (element));
  }

  public Map<KEY, RESULT> reduce () {
    processors.values ().forEach (processor -> processor.accumulate (poison));
    return processors.entrySet ().stream ()
      .map (entry -> Map.entry (entry.getKey (), entry.getValue ().getResult ()))
      .collect (Collectors.toMap (Entry::getKey, Entry::getValue));
  }
}
