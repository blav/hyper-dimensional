package us.blav.hd.mnist;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import com.google.common.util.concurrent.AtomicDouble;
import us.blav.hd.ClassifierTrainedModel;
import us.blav.hd.mnist.CellularModel.Factory;
import us.blav.hd.mnist.DatasetLoader.Digit;
import us.blav.hd.reca.Rule;
import us.blav.hd.util.Timer;

import static us.blav.hd.Injection.getInstance;

public class Main {

  public static void main (String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println ("usage: Main /path/to/result/file");
      System.exit (1);
    }

    try (PrintWriter out = new PrintWriter (Files.newBufferedWriter (Paths.get (args[0])))) {
      out.println ("dimensions;success;duration");
      out.flush ();
      IntStream.of (100, 200, 500, 1000, 2000, 5000, 10000, 20000)
        .forEach (dimensions -> {
          AtomicReference<Duration> duration = new AtomicReference<> ();
          double success;
          try (Timer ignore = new Timer (duration::set)) {
            success = benchmark (dimensions);
          }

          out.printf ("%d;%2.2f%%;%d%n", dimensions, success, duration.get ().toSeconds ());
          out.flush ();
        });
    }
  }

  private static double benchmark (int dimensions) {
//    ClassifierTrainedModel<Digit, Integer> trained = HyperVectorModel.builder ()
//      .dimensions (dimensions)
//      .build ()
//      .newModel ()
//      .train (2000L);
    ClassifierTrainedModel<Digit, Integer> trained = getInstance (Factory.class)
      .create (10, new Rule (110))
      .newModel ()
      .train (10000L);

    AtomicDouble accuracy = new AtomicDouble ();
    Consumer<Duration> logger = duration -> System.out.printf (
      "inference for %d dimensions took %dms, accuracy %2.1f%%%n",
      dimensions, duration.toMillis (), accuracy.get ());

    try (Timer ignore = new Timer (logger)) {
      double a = trained.computeAccuracy (100L);
      accuracy.set (a);
      return a;
    }
  }
}
