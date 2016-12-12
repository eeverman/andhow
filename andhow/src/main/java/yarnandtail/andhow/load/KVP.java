package yarnandtail.andhow.load;

import org.apache.commons.lang3.StringUtils;

/**
 * Key-value pair
 * @author eeverman
 */
public class KVP {
	public static final KVP NULL_KVP = new KVP();
	
	private String name;
	private String value;
	
	private KVP() {}

	public KVP(String name) throws ParsingException {
		this.name = StringUtils.trimToNull(name);
		
		if (this.name == null) {
			throw new ParsingException("The key (parameter name) cannot be empty", name);
		}
	}

	public KVP(String name, String value) throws ParsingException {
		this.name = StringUtils.trimToNull(name);
		this.value = StringUtils.trimToNull(value);
		
		if (this.name == null) {
			throw new ParsingException("The key (parameter name) cannot be empty", name);
		}
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}
	
	/**
	 * Splits a key value pair String into its key and value using the passed delimiter.
	 * If the delimiter is not found, it is assumed that there is only a name
	 * and no value, i.e., its a flag type value.
	 * 
	 * An UnparsableKVPException is thrown if there is only whitespace before
	 * the delimiter or if the delimiter occurs more than once.
	 * 
	 * @param arg
	 * @param delimiter
	 * @return
	 * @throws ParsingException 
	 */
	public static KVP splitKVP(String arg, String delimiter) throws ParsingException {
		arg = StringUtils.trimToNull(arg);
		
		if (arg != null) {
			String[] ss = arg.split(delimiter);
			String name = null;
			String value = null;
			
			if (ss.length > 0) {
				name = StringUtils.trimToNull(ss[0]);
				if (name == null) {
					throw new ParsingException("The key (parameter name) cannot be empty", arg);
				}
			}

			if (ss.length > 1) {
				value = StringUtils.trimToNull(ss[1]);
			}

			if (ss.length < 3) {
				return new KVP(name, value);
			} else {
				throw new ParsingException("There can only be one key/name and one value - found an extra", arg);
			}
			
		} else {
			return KVP.NULL_KVP;
		}
	}
}
