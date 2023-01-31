package us.blav.hd.mnist;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class MNISTModule implements Module {

  @Override
  public void configure (Binder binder) {
    binder.install (new FactoryModuleBuilder ().build (CellularModel.Factory.class));
    binder.install (new FactoryModuleBuilder ().build (HyperVectorModel.Factory.class));
  }
}
