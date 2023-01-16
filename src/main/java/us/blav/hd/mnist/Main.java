package us.blav.hd.mnist;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import us.blav.hd.ClassifierTrainedModel;
import us.blav.hd.mnist.MNIST.Digit;
import us.blav.hd.util.Timer;

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
          AtomicLong duration = new AtomicLong ();
          try (Timer ignore = new Timer (d -> duration.set (d.toSeconds ()))) {
            double success = benchmark (dimensions);
            out.printf ("%d;%2.2f%%;%d%n", dimensions, success, duration.get ());
            out.flush ();
          }
        });
    }
  }

  private static double benchmark (int dimensions) {
    ClassifierTrainedModel<Digit, Integer> trained = MNIST.builder ()
      .dimensions (dimensions)
      .build ()
      .newModel ()
      .train ();

    try (Timer ignore = new Timer (duration -> System.out.printf ("inference took %ds%n", duration.toSeconds ()))) {
      return trained.computeAccuracy ();
    }
  }
}
