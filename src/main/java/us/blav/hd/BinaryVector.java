package us.blav.hd;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import us.blav.hd.util.BitString;

import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

@Value
@Accessors (fluent = true)
@AllArgsConstructor
public class BinaryVector {

  Hyperspace hyperspace;

  BitString bits;

  public BinaryVector (Hyperspace hyperspace) {
    this (hyperspace, new BitString (hyperspace.dimensions ()));
  }

  public BinaryVector duplicate () {
    return new BinaryVector (hyperspace, (BitString) bits.duplicate ());
  }

  public String toString () {
    return range (0, hyperspace ().dimensions ())
      .mapToObj (i -> bits ().get (i) ? "1" : "0")
      .collect (joining ());
  }
}
