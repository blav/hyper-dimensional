package us.blav.hd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BinaryKDSpaceTest {

  // test the distance method
  @Test
  void distance () {
    // create a BinaryKDSpace
    Hyperspace hyperspace = new Hyperspace (3, new RandomGenerator ());
    BinaryKDSpace space = new BinaryKDSpace (hyperspace);

    // create two BinaryVectors
    BinaryVector v1 = new BinaryVector (hyperspace);
    BinaryVector v2 = new BinaryVector (hyperspace);

    // set the bits of the vectors
    v1.bits ().set (0, true);
    v1.bits ().set (1, false);
    v1.bits ().set (2, true);

    v2.bits ().set (0, false);
    v2.bits ().set (1, true);
    v2.bits ().set (2, false);

    // test the distance method
    assertEquals (1.0, space.distance (v1, v2));
  }
}