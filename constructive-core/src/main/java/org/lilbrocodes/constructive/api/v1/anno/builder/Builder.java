package org.lilbrocodes.constructive.api.v1.anno.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field to use a nested builder for it instead of a direct setter. Currently, lists and maps are supported.
 * Is mutually exclusive with {@link HardRequire} (throws {@link IllegalStateException}). Marking it as optional with {@link Required#required()} as false does nothing.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Builder {

}
