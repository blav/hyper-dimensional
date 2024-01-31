package us.blav.hd;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith ({ MockitoExtension.class, InjectionExtension.class })
class SparseDistributedMemoryTest {

  @Inject
  private Hyperspace.Factory factory;

  @Test
  public void test () {
    Hyperspace hyperspace = factory.create (50);
    SparseDistributedMemory memory = hyperspace.newSparseDistributedMemory (10000);

    BinaryVector vector1 = hyperspace.newRandom ();
    int count = 10;
    memory.select (vector1, count).write ();

    BinaryVector vector2 = hyperspace.newRandom ();
    memory.select (vector2, count).write ();

    BinaryVector vector3 = hyperspace.newRandom ();
    memory.select (vector3, count).write ();

    System.out.println (hyperspace.hamming ().apply (vector1, memory.select (vector1, count).read ()));
    System.out.println (hyperspace.hamming ().apply (vector1, memory.select (vector2, count).read ()));
    System.out.println (hyperspace.hamming ().apply (vector1, memory.select (vector3, count).read ()));
  }
}