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
public interface ParamDefinition<E extends Enum<E> & ParamDefinition> extends ParamDefinitionCore {
	
	static List<Enum> EMPTY_ENUM_LIST = Arrays.asList(new Enum[0]);
	static List<String> EMPTY_STRING_LIST = Arrays.asList(ArrayUtils.EMPTY_STRING_ARRAY);
	
	/**
	 * Returns a core storage object for the details of this parameter.
	 * This is the one method that ParamDefinition classes must implement.
	 * This is generally not used by application code, but simplifies user constructed
	 * ParamDefinitions because nearly all code can be contained in the 'core'
	 * object and nearly all public methods can be created as default methods in
	 * the interface.
	 * @return 
	 */
	public ParamDefinitionCore getCore();
	
	
	//What to do with these???
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
	 * Returns all instances (even non-real ones) as a list.
	 * A 'non-real' param is a placeholder one (named PLACEHOLDER by convention)
	 * that is used to pass an instance that has no meaning, but provides access
	 * to all Enum instances.
	 * 
	 * Used b/c Java has no interface for static methods.
	 * @return 
	 */
	List<E> getAllConfigParamsIncludingNonRealOnes();
	//
	//
	
	@Override
	default ParamType getParamType() {
		return getCore().getParamType();
	}
	
	@Override
	default String getFullName() {
		return getCore().getFullName();
	}
	
	@Override
	default String getShortDescription() {
		return getCore().getShortDescription();
	}
	
	@Override
	default String getHelpText() {
		return getCore().getHelpText();
	}

	@Override
	default List<String> getAlias() {
		return getCore().getAlias();
	}

	@Override
	default List<Enum> getPossibleValueEnums() {
		return getCore().getPossibleValueEnums();
	}
	
	@Override
	default List<String> getPossibleValues() {
		return getCore().getPossibleValues();
	}
	
	@Override
	default Object getDefaultValue() {
		return getCore().getDefaultValue();
	}
	
	@Override
	default boolean isReal() {
		return this.getParamType().isReal();
	}
	
	@Override
	default boolean isNotReal() {
		return this.getParamType().isNotReal();
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
