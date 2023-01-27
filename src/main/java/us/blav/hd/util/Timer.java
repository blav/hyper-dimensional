package us.blav.hd.util;

import java.io.Closeable;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;

import lombok.NonNull;

import static java.util.Optional.ofNullable;

public class Timer implements Closeable {

  private final Instant start;

  private final Consumer<Duration> onClose;

  public Timer (@NonNull Consumer<Duration> onClose) {
    this.onClose = onClose;
    this.start = Clock.systemUTC ().instant ();
  }

  @Override
  public void close () {
    Instant now = Clock.systemUTC ().instant ();
    ofNullable (onClose)
      .ifPresent (consumer -> consumer.accept (Duration.between (start, now)));
  }
}
