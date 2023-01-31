package us.blav.hd.util;

import java.io.Closeable;
import java.time.Duration;
import java.util.function.Consumer;

import lombok.NonNull;

import static java.lang.System.nanoTime;
import static java.time.Duration.ofNanos;

public class Timer implements Closeable {

  private final long start;

  private final Consumer<Duration> consumer;

  public Timer (@NonNull Consumer<Duration> onClose) {
    this.consumer = onClose;
    this.start = nanoTime ();
  }

  @Override
  public void close () {
    consumer.accept (ofNanos (nanoTime () - start));
  }
}
