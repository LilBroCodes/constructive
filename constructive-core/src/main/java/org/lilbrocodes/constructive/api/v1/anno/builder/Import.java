package org.lilbrocodes.constructive.api.v1.anno.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adds an option to add a custom package, optionally marked as static. Useful for default values where a variable type that isn't
 * imported otherwise is used, as default values that use the field's default value are not parsed and do not have imports handled
 * automatically. There are no plans to implement this.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Import {
    String path();
    boolean isStatic() default false;
}
