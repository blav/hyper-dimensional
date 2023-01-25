package us.blav.hd;

import lombok.Getter;
import lombok.Value;

@Value
public class Reservoir {


  @Getter
  ReservoirFactory factory;

  BinaryVector[] reservoir;

  public Reservoir (ReservoirFactory factory) {
    this.factory = factory;
    this.reservoir = new BinaryVector[factory.getReservoirDepth ()];
  }

  public void set (int index, BinaryVector vector) {
    reservoir[index] = vector;
  }

  public BinaryVector concat () {
    int dimensions = factory.getHyperspace ().dimensions ();
    BinaryVector result = factory.getOutputHyperspace ().newZero ();
    int depth = factory.getReservoirDepth ();
    if (dimensions % Long.SIZE == 0) {
      int size = dimensions / Long.SIZE;
      for (int i = 0; i < depth; i++)
        for (int j = 0; j < size; j++)
          result.bits ().setWord (i * size + j, reservoir[i].bits ().getWord (j));
    } else if (dimensions % Short.SIZE == 0) {
      int size = dimensions / Short.SIZE;
      for (int i = 0; i < depth; i++)
        for (int j = 0; j < size; j++)
          result.bits ().setShortWord (i * size + j, reservoir[i].bits ().getShortWord (j));
    } else if (dimensions % Byte.SIZE == 0) {
      int size = dimensions / Byte.SIZE;
      for (int i = 0; i < depth; i++)
        for (int j = 0; j < size; j++)
          result.bits ().setByteWord (i * size + j, reservoir[i].bits ().getByteWord (j));
    } else {
      for (int i = 0; i < depth; i++)
        for (long j = 0; j < dimensions; j++)
          result.bits ().set ((long) dimensions * i + j, reservoir[i].bits ().get (j));
    }

    return result;
  }
}
