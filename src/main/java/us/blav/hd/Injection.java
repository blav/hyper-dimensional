package us.blav.hd;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.NonNull;
import us.blav.hd.MainModule;

public class Injection {

  private static final Injector injector = Guice.createInjector (new MainModule ());

  public static <T> T getInstance (@NonNull Class<T> instanceClass) {
    return injector.getInstance (instanceClass);
  }
}
