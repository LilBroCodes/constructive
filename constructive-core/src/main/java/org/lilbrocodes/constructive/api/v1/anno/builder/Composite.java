package org.lilbrocodes.constructive.api.v1.anno.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.lilbrocodes.constructive.api.v1.anno.Constructive;

/**
 * If a field is marked as composite that the class of is also annotated with {@link Constructive}, then
 * instead of the field needing a value, it is settable using the builder of the field's class. If the annotated field's class is not annotated
 * as {@link Constructive}, an {@link IllegalStateException} will be thrown.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Composite {

}
