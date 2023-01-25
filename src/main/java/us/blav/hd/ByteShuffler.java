package us.blav.hd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.IntStream;

import lombok.Getter;
import lombok.Value;

@Value
public class ByteShuffler {

  @Getter
  Hyperspace hyperspace;

  int[] mapping;

  public ByteShuffler (Hyperspace hyperspace) {
    int dimensions = hyperspace.dimensions ();
    if (dimensions % Byte.SIZE != 0)
      throw new IllegalArgumentException ("hyperspace dimension must be a multiple of " + Byte.SIZE);

    int size = dimensions / Byte.SIZE;
    this.hyperspace = hyperspace;
    this.mapping = new int[size];
    List<Integer> mapping = IntStream.range (0, size)
      .boxed ()
      .collect (Collector.of (ArrayList::new, List::add, (a, b) -> {
        a.addAll (b);
        return a;
      }));

    Collections.shuffle (mapping, hyperspace.randomGenerator ().getRandom ());
    mapping.forEach (i -> this.mapping[i] = mapping.get (i));
  }

  public BinaryVector shuffle (BinaryVector vector) {
    BinaryVector result = hyperspace.newZero ();
    IntStream.range (0, hyperspace.dimensions () / Byte.SIZE)
      .forEach (i -> result.bits ().setByteWord (mapping[i], vector.bits ().getByteWord (i)));

    return result;
  }
}
