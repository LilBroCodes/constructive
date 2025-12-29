package org.lilbrocodes.constructive.api.v1.anno.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sets the default value for a field to the return value of the given method. The method must not have
 * any arguments. If no method is provided, but the marked field has a value, that will be used instead.
 * If neither is provided, an {@link IllegalStateException} will be thrown.
 * <br>
 * If both are provided, Constructor will prefer the field initializer over the passed method name.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Default {
    String method() default "";
}
