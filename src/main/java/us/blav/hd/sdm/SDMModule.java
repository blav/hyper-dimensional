package us.blav.hd.sdm;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.sun.jna.Native;

public class SDMModule implements Module {

  @Override
  public void configure (Binder binder) {
    binder.bind (SDMLibrary.class)
      .toProvider (() -> Native.load ("sdm", SDMLibrary.class))
      .in (Singleton.class);
  }
}
