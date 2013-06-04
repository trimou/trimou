package org.trimou.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Internal component - annotated element may be subject of incompatible changes
 * in future releases - actually it's not supposed to be a part of a public
 * client API.
 *
 * @author Martin Kouba
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR,
		ElementType.FIELD, ElementType.METHOD, ElementType.TYPE })
@Documented
public @interface Internal {

}
