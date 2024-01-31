package us.blav.hd;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import us.blav.hd.mnist.MNISTModule;
import us.blav.hd.reca.ReCAModule;
import us.blav.hd.sdm.SDMModule;

public class MainModule implements Module {

  @Override
  public void configure (Binder binder) {
    binder.install (new ReCAModule ());
    binder.install (new MNISTModule ());
    binder.install (new SDMModule ());
    binder.install (new FactoryModuleBuilder ().build (Hyperspace.Factory.class));
    binder.install (new FactoryModuleBuilder ().build (Bundler.Factory.class));
    binder.install (new FactoryModuleBuilder ().build (Combiner.Factory.class));
    binder.install (new FactoryModuleBuilder ().build (Rotator.Factory.class));
    binder.install (new FactoryModuleBuilder ().build (Cosine.Factory.class));
    binder.install (new FactoryModuleBuilder ().build (Hamming.Factory.class));
    binder.install (new FactoryModuleBuilder ().build (BitShuffler.Factory.class));
    binder.install (new FactoryModuleBuilder ().build (ByteShuffler.Factory.class));
    binder.install (new FactoryModuleBuilder ().build (BinaryKDSpace.Factory.class));
    binder.install (new FactoryModuleBuilder ().build (SparseDistributedMemory.Factory.class));
  }
}
