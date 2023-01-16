package us.blav.hd.mnist;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import com.google.common.annotations.VisibleForTesting;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import us.blav.hd.BinaryVector;
import us.blav.hd.Bundler;
import us.blav.hd.ClassifierModel;
import us.blav.hd.Hyperspace;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.IntStream.range;
import static java.util.stream.StreamSupport.stream;
import static us.blav.hd.mnist.MNIST.Dataset.t10k;
import static us.blav.hd.mnist.MNIST.Dataset.train;

public class MNIST {

  public static final int PIXEL_COUNT = 28 * 28;

  public enum Dataset {
    train,
    t10k

  }

  public static class Digit {

    private final byte[] pixels;

    private final int label;

    public Digit (byte[] pixels, byte label) {
      this.pixels = pixels;
      this.label = Byte.toUnsignedInt (label);
    }

    public int pixel (int index) {
      if (index < 0 || index >= PIXEL_COUNT)
        throw new IllegalArgumentException ();

      return Byte.toUnsignedInt (pixels[index]);
    }

    public int label () {
      return label;
    }
  }

  @Getter
  private final Hyperspace hyperspace;

  private final List<BinaryVector> coordinates;

  private final List<BinaryVector> values;

  @Builder
  public MNIST (int dimensions) {
    hyperspace = new Hyperspace (dimensions);
    coordinates = range (0, PIXEL_COUNT)
      .mapToObj (i -> hyperspace.newRandom ())
      .toList ();

    values = range (0, 256)
      .mapToObj (i -> hyperspace.newRandom ())
      .toList ();
  }

  public ClassifierModel<Digit, Integer> newModel () {
    return ClassifierModel.<Digit, Integer>builder ()
      .hyperspace (hyperspace)
      .encoder (this::encode)
      .keyMapper (Digit::label)
      .trainDataset (() -> load (train))
      .validateDataset (() -> load (t10k))
      .build ();
  }

  @SneakyThrows (IOException.class)
  public Stream<Digit> load (Dataset dataset) {
    ClassLoader loader = getClass ().getClassLoader ();
    InputStream digits = requireNonNull (loader.getResourceAsStream (format ("%s-images-idx3-ubyte", dataset)));
    if (digits.skip (16) != 16)
      throw new NoSuchElementException ();

    InputStream labels = requireNonNull (loader.getResourceAsStream (format ("%s-labels-idx1-ubyte", dataset)));
    if (labels.skip (8) != 8)
      throw new NoSuchElementException ();

    Iterator<Digit> iterator = new Iterator<> () {

      private Digit next = lookupNext ();

      @Override
      public boolean hasNext () {
        return next != null;
      }

      @Override
      public Digit next () {
        if (next == null)
          throw new NoSuchElementException ();

        Digit current = next;
        next = lookupNext ();
        return current;
      }

      @SneakyThrows (IOException.class)
      private Digit lookupNext () {
        byte[] digitBuffer = new byte[PIXEL_COUNT];
        byte[] labelBuffer = new byte[1];
        int read = digits.read (digitBuffer);
        if (read == 0)
          return null;

        if (read != PIXEL_COUNT)
          return null;

        if (labels.read (labelBuffer) != 1)
          throw new NoSuchElementException ();

        return new Digit (digitBuffer, labelBuffer[0]);
      }
    };

    return stream (spliteratorUnknownSize (iterator, ORDERED), false)
      .onClose (() -> {
        try {
          digits.close ();
          labels.close ();
        } catch (IOException e) {
          throw new UncheckedIOException (e);
        }
      });
  }

  @VisibleForTesting
  BinaryVector encode (Digit digit) {
    Bundler bundler = hyperspace.newBundler ();
    range (0, PIXEL_COUNT).forEach (i ->
      bundler.add (hyperspace.newCombiner ()
        .add (values.get (digit.pixel (i)))
        .add (coordinates.get (i))
        .reduce ()));

    return bundler.reduce ();
  }

}
