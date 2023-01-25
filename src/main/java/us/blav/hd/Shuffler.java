package us.blav.hd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.IntStream;

import lombok.Getter;
import lombok.Value;

@Value
public class Shuffler {

  @Getter
  Hyperspace hyperspace;

  int[] mapping;

  public Shuffler (Hyperspace hyperspace) {
    this.hyperspace = hyperspace;
    int dimensions = hyperspace.dimensions ();
    this.mapping = new int[dimensions];
    List<Integer> mapping = IntStream.range (0, dimensions)
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
    IntStream.range (0, hyperspace.dimensions ()).forEach (i -> {
      if (vector.bits ().get (i))
        result.bits ().set (mapping[i]);
    });

    return result;
  }
}
