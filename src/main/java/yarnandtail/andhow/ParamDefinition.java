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
public interface ParamDefinition<E extends Enum<E> & ParamDefinition> {
	
	static List<Enum> EMPTY_ENUM_LIST = Arrays.asList(new Enum[0]);
	static List<String> EMPTY_STRING_LIST = Arrays.asList(ArrayUtils.EMPTY_STRING_ARRAY);
	
	public ParamType getParamType();
	
	/**
	 * Name of the entire set of parameters, i.e., for this entire enum and all its values.
	 * @return May be empty, but not null.
	 */
	public String getEntireSetName();
	
	/**
	 * Name of the entire set of parameters, i.e., for this entire enum and all its values.
	 * @return May be empty, but not null.
	 */
	public String getEntireSetDescription();
	
	/**
	 * Long-form option name.  Similar to the full name nix options (dashes not required).
	 * @return 
	 */
	public String getFullName();
	
	/**
	 * A short sentence description.
	 * @return 
	 */
	public String getShortDescription();
	
	/**
	 * Added details that might be shown if the user requests help.
	 * Assume that the short description is already shown.
	 * @return 
	 */
	public String getHelpText();

	
	/**
	 * Alias (short) form.  Similar to nix single letter options (dashes not required).
	 * @return 
	 */
	public List<String> getAlias();

	
	/**
	 * Returns all instances (even non-real ones) as a list.
	 * A 'non-real' param is a placeholder one (named PLACEHOLDER by convention)
	 * that is used to pass an instance that has no meaning, but provides access
	 * to all Enum instances.
	 * 
	 * Used b/c Java has no interface for static methods.
	 * @return 
	 */
	List<E> getAllConfigParamsIncludingNonRealOnes();
	
	/**
	 * Some parameters may have a defined set of possible values, which
	 * are specified as a list of enums.  This returns that list.
	 * 
	 * @return A non-null list of Enums.
	 */
	List<Enum> getPossibleValueEnums();
	
	/**
	 * If the parameter is unspecified, the effective value is considered to be this default value.
	 * For name-value pairs, this will be a string.
	 * Flags, which are normally considered to be true if specified, can be
	 * converted to be true _unless_ specified w/ a false value by setting a
	 * Boolean False here.
	 * @return 
	 */
	Object getDefaultValue();
	
	
	/**
	 * Some parameters may have a defined set of possible values.
	 * If that is the case, this will get that list as a list of strings.
	 * 
	 * @return a non-null String list.
	 */
	default List<String> getPossibleValues() {
		List<Enum> allowedValueEnumList = getPossibleValueEnums();
		List<String> domainStrings = new ArrayList(allowedValueEnumList.size());
		for (Enum e : allowedValueEnumList) {
			domainStrings.add(e.toString());
		}
		
		return domainStrings;
	}
	

	
	/**
	 * Returns an array of instances, excluding any that are isNotReal()
	 * @return 
	 */
	default List<E> getConfigParams() {
		
		ArrayList<E> list = new ArrayList(getAllConfigParamsIncludingNonRealOnes());
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getParamType().isNotReal()) {
				list.remove(i);
			}
		}
		
		return Collections.unmodifiableList(list);
	}
	
	/**
	 * True if the passed name or alias matches this instance.
	 * Case insensitive, but this method will not trim the incoming argument.
	 * @param nameOrAlias
	 * @return 
	 */
	default boolean isMatch(String nameOrAlias) {
		
		if (nameOrAlias != null) {
			if (nameOrAlias.equalsIgnoreCase(getFullName())) {
				return true;
			} else {
				for (String a : getAlias()) {
					if (nameOrAlias.equalsIgnoreCase(a)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Same as find(), but astatic implementation so you don't need an instance.
	 * @param nameOrAlias
	 * @return 
	 */
	default E find(String nameOrAlias) {

		nameOrAlias = StringUtils.trimToNull(nameOrAlias);
		
		if (nameOrAlias != null) {
			for (E cp : getConfigParams()) {

				if (cp.isMatch(nameOrAlias)) {
					return cp;
				}
			}
		}
		
		return null;
	}
	
	
}
