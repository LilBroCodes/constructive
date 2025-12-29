package org.lilbrocodes.constructive.api.v1.anno.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field to not be a builder step, rather a parameter in the generated builder's
 * create method. Along with this, the null check for the field will be done at creation time
 * rather than at build time. If {@link HardRequire#nullable()} is enabled, no null check will be
 * made for the value.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface HardRequire {
    boolean require() default true;
    boolean nullable() default false;
}
