package us.blav.hd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.collections.api.list.primitive.IntList;

public class SDM2 {

  private class HardLocation {

    private final BinaryVector address;

    private final Bundler counters;

    private IntList neighbors;

    private HardLocation (BinaryVector address, Bundler counters) {
      this.address = address;
      this.counters = counters;
    }
  }

  private final List<HardLocation> locations;

  private final int neighborhood;

  public SDM2 (int size, int neighborhood) {
    locations = new ArrayList<> (size);
    this.neighborhood = neighborhood;
  }
}
