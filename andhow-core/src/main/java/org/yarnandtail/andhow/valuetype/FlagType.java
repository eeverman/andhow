package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.api.FlaggableType;
import org.yarnandtail.andhow.api.ParsingException;

/**
 * Metadata and parsing for a Boolean type which is never null, similar to a 'nix flag behavior.
 * <p>
 * This would be used to parse a command line argument that is true by its presence, e.g.:<br>
 * {@code java MyClass launch}<br>
 * If {@code launch} is an AndHow property using the FlagType (i.e. {@code FlagProp}), launch will
 * be considered true just by its presence.
 * <p>
 * This class is threadsafe and uses a singleton pattern to prevent multiple
 * instances since all users can safely use the same instance.
 */
public class FlagType extends BolType implements FlaggableType<Boolean> {

	private static final FlagType instance = new FlagType();

	protected FlagType() {	}

	/**
	 * Fetch the single, shared instance of this ValueType
	 * <p>
	 *
	 * @return An instance of the {@link #FlagType()}
	 */
	public static FlagType instance() {
		return instance;
	}

	@Override
	public Boolean parseFlag(final String sourceValue) throws ParsingException {
		if (sourceValue == null) {
			//Just the presence of the flag is enough to set true
			return true;
		} else {
			return parse(sourceValue);
		}
	}


}
