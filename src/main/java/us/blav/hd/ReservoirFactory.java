package us.blav.hd;

import lombok.Getter;
import lombok.Value;

@Value
public class ReservoirFactory {

  @Getter
  Hyperspace hyperspace;

  @Getter
  int reservoirDepth;

  @Getter
  Hyperspace outputHyperspace;

  public ReservoirFactory (Hyperspace hyperspace, int reservoirDepth) {
    this.hyperspace = hyperspace;
    this.reservoirDepth = reservoirDepth;
    this.outputHyperspace = new Hyperspace (hyperspace.dimensions () * reservoirDepth);
  }

  public Reservoir newReservoir () {
    return new Reservoir (this);
  }
}
