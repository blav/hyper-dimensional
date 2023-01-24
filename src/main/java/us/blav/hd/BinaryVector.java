package us.blav.hd;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.apache.lucene.util.OpenBitSet;
import us.blav.hd.util.OpenBitSetEnh;

import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

@Value
@Accessors (fluent = true)
@AllArgsConstructor
public class BinaryVector {

  Hyperspace hyperspace;

  OpenBitSetEnh bits;

  public BinaryVector (Hyperspace hyperspace) {
    this (hyperspace, new OpenBitSetEnh (hyperspace.dimensions ()));
  }

  public String toString () {
    return range (0, hyperspace ().dimensions ())
      .mapToObj (i -> bits ().get (i) ? "1" : "0")
      .collect (joining ());
  }
}
