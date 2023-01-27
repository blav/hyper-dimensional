package us.blav.hd.reca;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class ReCAModule implements Module {

  @Override
  public void configure (Binder binder) {
    binder.install (new FactoryModuleBuilder ().build (TransitionFactory.class));
  }
}
