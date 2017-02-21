package org.yarnandtail.andhow.load;

import org.yarnandtail.andhow.ParsingException;
import org.yarnandtail.andhow.util.TextUtil;

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
		this.name = TextUtil.trimToNull(name);
		
		if (this.name == null) {
			throw new ParsingException("The key (parameter name) cannot be empty", name);
		}
	}

	public KVP(String name, String value) throws ParsingException {
		this.name = TextUtil.trimToNull(name);
		this.value = TextUtil.trimToNull(value);
		
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
	 * 
	 * If the delimiter is not found, it is assumed that there is only a name
	 * and no value, i.e., its a flag type value.  The key and value are only split
	 * on the first instance, subsequent delimiters are considered part of the
	 * value.
	 * 
	 * An ParsingException is thrown if there is only whitespace before
	 * the delimiter.
	 * 
	 * @param arg
	 * @param delimiter
	 * @return
	 * @throws ParsingException 
	 */
	public static KVP splitKVP(String arg, String delimiter) throws ParsingException {
		arg = TextUtil.trimToNull(arg);
		
		if (arg != null) {
			String[] ss = arg.split(delimiter, 2);
			String name = null;
			String value = null;
			
			if (ss.length > 0) {
				name = TextUtil.trimToNull(ss[0]);
				if (name == null) {
					throw new ParsingException("The key (parameter name) cannot be empty", arg);
				}
			}

			if (ss.length > 1) {
				value = ss[1];	//preserve spaces to be delt w/ by the trimmer
			}

			return new KVP(name, value);
			
		} else {
			return KVP.NULL_KVP;
		}
	}
}
