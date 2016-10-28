package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Interface for an enum representing command line arguments and/or configuration parameters.
 * @author eeverman
 */
public interface ConfigParamDefinition<E extends Enum<E> & ConfigParamDefinition> extends ParamDefinition<E> {
	
	/**
	 * The type of system recognized config param
	 * @return 
	 */
	ConfigParamType getConfigParamType();
	
}
