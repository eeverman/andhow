package org.yarnandtail.andhow.junit5;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.yarnandtail.andhow.junit5.ext.ConfigFromFileBaseExt;
import org.yarnandtail.andhow.junit5.ext.ConfigFromFileBeforeThisTestExt;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Inherited
@ExtendWith(ConfigFromFileBeforeThisTestExt.class)
public @interface ConfigFromFileBeforeThisTest {
	String filePath();
}
