package org.yarnandtail.andhow.ztest;

import org.yarnandtail.andhow.api.ParsingException;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.property.TrimToNullTrimmer;
import org.yarnandtail.andhow.valuetype.BaseValueType;

/*
 *  | Series   |NullOK?| Default | Valid? |
 *  | 0  - 9   |   Y   |		N	   |    N	  |
 *  | 10 - 19  |   Y   |    Y    |    N	  |
 *  | 20 - 29  |   Y   |    N    |    Y   |
 *  | 30 - 39  |   Y   |    Y    |    Y   |
 *  | 100-109  |   N   |    N    |    N   |
 *  | 110-119  |   N   |    Y    |    N   |
 *  | 120-129  |   N   |    N    |    Y   |
 *  | 130-139  |   N   |    Y    |    Y   |
 *  | 200      | Alt. Trimmer             |
 *  | 210      | Alt. Type                |
 *  In Alias for 1st in each set
 *
 * Special combinations / considerations:
 * Default w/ quote
 * Validation w/ quote
 */
public class StrPropProps {
	//
	// Null OK | No Default | No Validations
	public static final StrProp PROP_0 = StrProp.builder().aliasIn("StrPropProps.PROP_0").build();

	//
	// Null OK | Has Default | No Validations
	public static final StrProp PROP_10 = StrProp.builder().defaultValue(" prop 10 ").build();

	//
	// Null OK | No Default | Has Validations
	public static final StrProp PROP_20 = StrProp.builder().startsWithIgnoringCase("star").endsWith("ing")
			.matches("[starSTARing \t]+").aliasIn("StrPropProps.PROP_20").build();
	public static final StrProp PROP_21 = StrProp.builder().startsWith("star").endsWithIgnoringCase("ing")
			.matches("[staringING \t]+").build();
	public static final StrProp PROP_22 = StrProp.builder().oneOf(" \"a b\" ", " cd ", "\"\"", " \"' ").build();
	public static final StrProp PROP_23 = StrProp.builder().oneOfIgnoringCase(" \"a b\" ", " cd ", "\"\"", " \"' ").build();


	//
	// Null OK | Has Default | Has Validations
	public static final StrProp PROP_30 = StrProp.builder().defaultValue("Star t ing")
			.startsWithIgnoringCase("star").endsWith("ing").matches("[starSTARing \t]+")
			.aliasIn("StrPropProps.PROP_30").build();
	public static final StrProp PROP_31 = StrProp.builder().defaultValue("star t InG")
			.startsWith("star").endsWithIgnoringCase("ing").matches("[staringING \t]+").build();
	public static final StrProp PROP_32 = StrProp.builder().defaultValue(" \"a b\" ")
			.oneOf(" \"a b\" ", " cd ", "\"\"", " \"' ").build();
	public static final StrProp PROP_33 = StrProp.builder().defaultValue(" \"A B\" ")
			.oneOfIgnoringCase(" \"a b\" ", " cd ", "\"\"", " \"' ").build();

	//
	// Not Null

	//
	// Not Null | No Default | No Validations
	public static final StrProp PROP_100 = StrProp.builder().notNull().aliasIn("StrPropProps.PROP_100").build();

	//
	// Not Null | Has Default | No Validations
	public static final StrProp PROP_110 = StrProp.builder().notNull().defaultValue(" \"prop 110\" ").build();

	//
	// Not Null | No Default | Has Validations
	public static final StrProp PROP_120 = StrProp.builder().notNull()
			.startsWithIgnoringCase("star").endsWith("ing").matches("[starSTARing \t]+").aliasIn("StrPropProps.PROP_120").build();
	public static final StrProp PROP_121 = StrProp.builder().notNull()
			.startsWith("star").endsWithIgnoringCase("ing").matches("[staringING \t]+").build();
	public static final StrProp PROP_122 = StrProp.builder().notNull()
			.oneOf(" \"a b\" ", " cd ", "\"\"", " \"' ").build();
	public static final StrProp PROP_123 = StrProp.builder().notNull()
			.oneOfIgnoringCase(" \"a b\" ", " cd ", "\"\"", " \"' ").build();


	//
	// These won't need much testing since they have default
	// Not Null | Has Default | Has Validations
	public static final StrProp PROP_130 = StrProp.builder().notNull().defaultValue("Star t ing")
			.startsWithIgnoringCase("star").endsWith("ing").matches("[starSTARing \t]+")
			.aliasIn("StrPropProps.PROP_130").build();
	public static final StrProp PROP_131 = StrProp.builder().notNull().defaultValue("star t InG")
			.startsWith("star").endsWithIgnoringCase("ing").matches("[staringING \t]+").build();
	public static final StrProp PROP_132 = StrProp.builder().notNull().defaultValue(" \"a b\" ")
			.oneOf(" \"a b\" ", " cd ", "\"\"", " \"' ").build();
	public static final StrProp PROP_133 = StrProp.builder().notNull().defaultValue(" \"A B\" ")
			.oneOfIgnoringCase(" \"a b\" ", " cd ", "\"\"", " \"' ").build();

	//
	// Special Trimmers and Types
	public static final StrProp PROP_200 = StrProp.builder().trimmer(TrimToNullTrimmer.instance())
			.aliasIn("StrPropProps.PROP_200").build();
	public static final StrProp PROP_210 = StrProp.builder().valueType(UpperCaseParser.instance())
			.matches("[A-Z ]+").aliasIn("StrPropProps.PROP_210").build();


	/**
	 * A custom String type that converts string values to UCase when it 'parses' them
	 */
	static public class UpperCaseParser extends BaseValueType<String> {
		private static final UpperCaseParser instance = new UpperCaseParser();
		private UpperCaseParser() {
			super(String.class);
		}

		public static UpperCaseParser instance() {
			return instance;
		}

		/**
		 * Returns Uppercase
		 */
		@Override
		public String parse(String sourceValue) throws ParsingException {
			return sourceValue.toUpperCase();
		}

		@Override
		public String cast(Object o) throws RuntimeException {
			return (String)o;
		}
	}
}


