package us.blav.hd;

import lombok.Getter;
import lombok.NonNull;

public abstract class InputEncoder<INPUT> {

  @Getter
  protected final Hyperspace hyperspace;

  protected InputEncoder (Hyperspace hyperspace) {
    this.hyperspace = hyperspace;
  }

  public abstract BinaryVector encode (@NonNull INPUT input);

}
