package org.lilbrocodes.constructive.api.v1.anno;

import java.lang.annotation.*;

/**
 * Marks a class to be processed by Constructive.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Constructive {
    boolean builder() default false;
    boolean serializer() default false; // TODO
    boolean deserializer() default false; // TODO
}
