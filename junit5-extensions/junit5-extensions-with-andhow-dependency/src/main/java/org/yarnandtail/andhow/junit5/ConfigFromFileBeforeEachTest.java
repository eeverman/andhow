package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.yarnandtail.andhow.junit5.ext.ConfigFromFileBeforeEachTestExt;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Inherited
@ExtendWith(ConfigFromFileBeforeEachTestExt.class)
public @interface ConfigFromFileBeforeEachTest {
	String filePath();
}
