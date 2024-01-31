package us.blav.hd.mnist;

import jakarta.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.SneakyThrows;
import us.blav.hd.ByteEncoder;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;

@Singleton
public class DatasetLoader {

  public enum Dataset {

    train,
    t10k

  }

  public static class Digit {

    public static final int PIXEL_COUNT = 28 * 28;

    private final byte[] pixels;

    private final int label;

    public Digit (byte[] pixels, byte label) {
      this.pixels = pixels;
      this.label = Byte.toUnsignedInt (label);
    }

    public int pixel (int index) {
      checkRange (index);
      return Byte.toUnsignedInt (pixels[index]);
    }

    public byte pixel (int index, @NonNull ByteEncoder encoder) {
      checkRange (index);
      return encoder.encode (pixels[index]);
    }

    public int label () {
      return label;
    }

    private void checkRange (int index) {
      if (index < 0 || index >= PIXEL_COUNT)
        throw new IllegalArgumentException ();
    }
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
        byte[] digitBuffer = new byte[Digit.PIXEL_COUNT];
        byte[] labelBuffer = new byte[1];
        int read = digits.read (digitBuffer);
        if (read == 0)
          return null;

        if (read != Digit.PIXEL_COUNT)
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
}
