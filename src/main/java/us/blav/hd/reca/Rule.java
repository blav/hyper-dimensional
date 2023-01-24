package us.blav.hd.reca;

import lombok.Getter;
import lombok.Value;

import static java.lang.Byte.SIZE;

@Value
@Getter
public class Rule {

  boolean[] rule;

  byte id;

  public Rule (byte id) {
    this.id = id;
    this.rule = new boolean[SIZE];
    for (int i = 0; i < SIZE; i++) {
      byte mask = (byte) (1 << i);
      rule[i] = (id & mask) == mask;
    }
  }

  public boolean getState (boolean left, boolean middle, boolean right) {
    return rule[(left ? 4 : 0) | (middle ? 2 : 0) | (right ? 1 : 0)];
  }
}
