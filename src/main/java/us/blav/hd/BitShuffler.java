package us.blav.hd;

import jakarta.inject.Inject;
import java.util.stream.IntStream;

import com.google.inject.assistedinject.Assisted;
import lombok.NonNull;

public class BitShuffler extends AbstractShuffler {

  public interface Factory {

    BitShuffler create (@SuppressWarnings ("unused") Hyperspace hyperspace);

  }

  @Inject
  public BitShuffler (@NonNull @Assisted Hyperspace hyperspace) {
    super (hyperspace, hyperspace.randomGenerator (), hyperspace.dimensions ());
  }

  @Override
  public BinaryVector shuffle (BinaryVector vector) {
    BinaryVector result = hyperspace.newZero ();
    IntStream.range (0, hyperspace.dimensions ()).forEach (i -> {
      if (vector.bits ().get (i))
        result.bits ().set (mapping[i]);
    });

    return result;
  }
}
