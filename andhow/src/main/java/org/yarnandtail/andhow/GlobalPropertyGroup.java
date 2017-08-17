package org.yarnandtail.andhow;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *
 * @author ericeverman
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
@Inherited
public @interface GlobalPropertyGroup {
	
}
