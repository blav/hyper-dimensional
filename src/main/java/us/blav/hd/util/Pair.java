package us.blav.hd.util;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@AllArgsConstructor
@Accessors(fluent = true)
public class Pair<A, B> {

  A a;

  B b;

}
