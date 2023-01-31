package us.blav.hd;

import javax.inject.Inject;
import java.util.stream.IntStream;

import com.google.inject.assistedinject.Assisted;
import lombok.NonNull;
import us.blav.hd.util.BitHacks;

import static us.blav.hd.Metric.ensureDimensions;

public class Cosine implements Metric {

  @NonNull
  private final Hyperspace hyperspace;

  private final BitHacks hacks;

  public interface Factory {

    Cosine create (@NonNull Hyperspace hyperspace);

  }

  @Inject
  public Cosine (@NonNull @Assisted Hyperspace hyperspace, BitHacks hacks) {
    this.hyperspace = hyperspace;
    this.hacks = hacks;
  }

  @Override
  public double apply (@NonNull BinaryVector a, @NonNull BinaryVector b) {
    ensureDimensions (hyperspace, a, b);
    int dimensions = hyperspace.dimensions ();
    long differing = IntStream.range (0, a.bits ().getNumWords ())
      .map (i -> hacks.countSet (a.bits ().getLongWord (i) ^ b.bits ().getLongWord (i)))
      .sum ();

    return (dimensions - 2 * differing) / (double) dimensions;
  }
}
