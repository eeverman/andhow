package yarnandtail.andhow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;

/**
 *
 * @author eeverman
 */
public class ConfigPointUtil {
	public static final List<String> EMPTY_STRING_LIST = Collections.unmodifiableList(new ArrayList<String>());
	
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
