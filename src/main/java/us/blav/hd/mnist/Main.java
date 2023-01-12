package us.blav.hd.mnist;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import org.checkerframework.common.value.qual.IntRange;
import us.blav.hd.util.Timer;

import static us.blav.hd.mnist.MNIST.Dataset.t10k;

public class Main {

  public static void main (String[] args) throws IOException {
    try (PrintWriter out = new PrintWriter (Files.newBufferedWriter (Paths.get ("/Users/dbr/dev/perso/hyper-dimensional/result.txt")))) {
      out.println ("dimensions;success;duration");
      out.flush ();
      IntStream.of (100, 200, 500, 1000, 2000, 5000, 10000, 20000)
        .forEach (dimensions -> {
          double success;
          AtomicLong duration = new AtomicLong ();
          try (Timer ignore = new Timer (d -> duration.set (d.toSeconds ()))) {
            success = benchmark (dimensions);
          }

          out.printf ("%d;%2.2f%%;%d%n", dimensions, success, duration.get ());
          out.flush ();
        });
    }
  }

  private static double benchmark (int dimensions) {
    TrainedModel trained = new Model (dimensions).train ();
    AtomicInteger success = new AtomicInteger ();
    AtomicInteger count = new AtomicInteger ();
    try (Timer ignore = new Timer (duration -> System.out.printf ("inference took %ds%n", duration.toSeconds ()))) {
      new MNIST ().load (t10k).forEach (digit -> {
        int inferred = trained.infer (digit);
        count.incrementAndGet ();
        System.out.printf ("inferring image %d%n", count.get ());
        if (digit.label () == inferred)
          success.incrementAndGet ();
      });
    }

    return 100. * success.get () / count.get ();
  }
}
