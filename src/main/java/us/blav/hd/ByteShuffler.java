package us.blav.hd;

import javax.inject.Inject;
import java.util.stream.IntStream;

import com.google.inject.assistedinject.Assisted;
import lombok.NonNull;

public class ByteShuffler extends AbstractShuffler {

  interface Factory {

    ByteShuffler create (Hyperspace hyperspace);

  }

  @Inject
  public ByteShuffler (@NonNull @Assisted Hyperspace hyperspace) {
    super (hyperspace, hyperspace.randomGenerator (), computeSize (hyperspace));
  }

  private static int computeSize (Hyperspace hyperspace) {
    int dimensions = hyperspace.dimensions ();
    if (dimensions % Byte.SIZE != 0)
      throw new IllegalArgumentException ("hyperspace dimension must be a multiple of " + Byte.SIZE);

    return dimensions / Byte.SIZE;
  }

  public BinaryVector shuffle (BinaryVector vector) {
    BinaryVector result = hyperspace.newZero ();
    IntStream.range (0, hyperspace.dimensions () / Byte.SIZE)
      .forEach (i -> result.bits ().setByteWord (mapping[i], vector.bits ().getByteWord (i)));

    return result;
  }
}
