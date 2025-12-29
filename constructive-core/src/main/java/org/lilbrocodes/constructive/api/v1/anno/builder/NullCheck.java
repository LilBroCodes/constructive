package org.lilbrocodes.constructive.api.v1.anno.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds a custom null check whenever checking if a field with a default is not set. Useful for places where you might want to restore
 * a set default based on some condition, like a string being blank. '%f' in the provided value will be replaced by the name of the marked
 * field at build time. If used on a method that is not marked by {@link Default}, this annotation will do nothing.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface NullCheck {
    String check();
}
