package org.lilbrocodes.constructive.api.v1.anno.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as required. All fields are required by default.
 * If a field is marked as not required (required with a false {@link Required#required()}),
 * it's value will be allowed to be null at runtime. Otherwise, an exception will be thrown
 * if no value was provided before calling build().
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Required {
    boolean required() default true;
}
