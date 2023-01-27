package us.blav.hd.reca;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import us.blav.hd.reca.Transition.Factory;

public class ReCAModule implements Module {

  @Override
  public void configure (Binder binder) {
    binder.install (new FactoryModuleBuilder ().build (Factory.class));
  }
}
