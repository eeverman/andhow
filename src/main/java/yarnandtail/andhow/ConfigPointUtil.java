package yarnandtail.andhow;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;

/**
 *
 * @author eeverman
 */
public class ConfigPointUtil {
	public static List<String> EMPTY_STRING_LIST = Arrays.asList(ArrayUtils.EMPTY_STRING_ARRAY);
	
	/**
	 * Parses a string to a boolean.
	 * It just uses Apache BooleanUtils, but this codifies that this is how its done.
	 * @param value
	 * @return 
	 */
	public static boolean toBoolean(String value) {
		return BooleanUtils.toBoolean(value);
	}

}
