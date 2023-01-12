package us.blav.hd;

import lombok.NonNull;

public interface Accumulator<A extends Accumulator<A>> {

  A add (@NonNull BinaryVector vector);

  BinaryVector reduce ();

}
