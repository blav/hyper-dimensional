package us.blav.hd;

import jakarta.inject.Inject;
import java.lang.reflect.Field;
import java.util.Arrays;

import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static java.lang.reflect.Modifier.isStatic;
import static us.blav.hd.Injection.getInstance;

public class InjectionExtension implements BeforeEachCallback {

  @Override
  public void beforeEach (ExtensionContext extensionContext) {
    extensionContext.getTestInstance ().ifPresent (instance ->
      extensionContext.getTestClass ()
        .stream ()
        .map (Class::getDeclaredFields)
        .flatMap (Arrays::stream)
        .filter (field -> field.getAnnotation (Inject.class) != null)
        .filter (field -> ! isStatic (field.getModifiers ()))
        .peek (field -> field.setAccessible (true))
        .forEach (field -> inject (instance, field)));
  }

  @SneakyThrows (IllegalAccessException.class)
  private void inject (Object instance, Field field) {
    field.set (instance, getInstance (field.getType ()));
  }
}
