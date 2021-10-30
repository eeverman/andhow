package org.yarnandtail.andhow.ztest;

import org.yarnandtail.andhow.api.ParsingException;
import org.yarnandtail.andhow.api.ValueType;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.property.TrimToNullTrimmer;
import org.yarnandtail.andhow.valuetype.BaseValueType;
import org.yarnandtail.andhow.valuetype.StrType;

/*
 *            Has Default | Has Validations
 *  Null OK		N	N	0	Alias for 1 in each set
 *  Null OK		Y	N	10
 *  Null OK		N	Y	20
 *  Null OK		Y	Y	30
 *  Not Null		N	N	100
 *  Not Null		Y	N	110
 *  Not Null		N	Y	120
 *  Not Null		Y	Y	130
 *
 *  Alt Trimmer 200
 *  Alt Type 210
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
	public static final StrProp PROP_10 = StrProp.builder().defaultValue("prop10").build();

	//
	// Null OK | No Default | Has Validations
	public static final StrProp PROP_20 = StrProp.builder().startsWithIgnoringCase("star").endsWith("ing").aliasIn("StrPropProps.PROP_20").build();
	public static final StrProp PROP_21 = StrProp.builder().startsWith("star").endsWithIgnoringCase("ing").build();
	public static final StrProp PROP_22 = StrProp.builder().matches("[abcd]+").build();
	public static final StrProp PROP_23 = StrProp.builder().oneOf("ab", "cd", "\"", "'").build();
	public static final StrProp PROP_24 = StrProp.builder().oneOfIgnoringCase("ab", "cd", "").build();
	public static final StrProp PROP_25 = StrProp.builder().startsWith("pre").build();
	public static final StrProp PROP_26 = StrProp.builder().startsWithIgnoringCase("pre").build();

	//
	// Null OK | Has Default | Has Validations
	public static final StrProp PROP_30 = StrProp.builder().defaultValue("Starting").startsWithIgnoringCase("star").endsWith("ing").aliasIn("StrPropProps.PROP_30").build();
	public static final StrProp PROP_31 = StrProp.builder().defaultValue("startInG").startsWith("star").endsWithIgnoringCase("ing").build();
	public static final StrProp PROP_32 = StrProp.builder().defaultValue("dab").matches("[abcd]+").build();
	public static final StrProp PROP_33 = StrProp.builder().defaultValue("cd").oneOf("ab", "cd", "\"", "'").build();
	public static final StrProp PROP_34 = StrProp.builder().defaultValue("Cd").oneOfIgnoringCase("ab", "cd", "").build();
	public static final StrProp PROP_35 = StrProp.builder().defaultValue("preorder").startsWith("pre").build();
	public static final StrProp PROP_36 = StrProp.builder().defaultValue("Preorder").startsWithIgnoringCase("pre").build();

	//
	// Not Null

	//
	// Not Null | No Default | No Validations
	public static final StrProp PROP_100 = StrProp.builder().notNull().aliasIn("StrPropProps.PROP_100").build();

	//
	// Not Null | Has Default | No Validations
	public static final StrProp PROP_110 = StrProp.builder().notNull().defaultValue("prop110").build();

	//
	// Not Null | No Default | Has Validations
	public static final StrProp PROP_120 = StrProp.builder().notNull().startsWithIgnoringCase("star").endsWith("ing").aliasIn("StrPropProps.PROP_120").build();
	public static final StrProp PROP_121 = StrProp.builder().notNull().startsWith("star").endsWithIgnoringCase("ing").build();
	public static final StrProp PROP_122 = StrProp.builder().notNull().matches("[abcd]+").build();
	public static final StrProp PROP_123 = StrProp.builder().notNull().oneOf("ab", "cd", "\"", "'").build();
	public static final StrProp PROP_124 = StrProp.builder().notNull().oneOfIgnoringCase("ab", "cd", "").build();
	public static final StrProp PROP_125 = StrProp.builder().notNull().startsWith("pre").build();
	public static final StrProp PROP_126 = StrProp.builder().notNull().startsWithIgnoringCase("pre").build();

	//
	// These won't need much testing since they have default
	// Not Null | Has Default | Has Validations
	public static final StrProp PROP_130 = StrProp.builder().notNull().defaultValue("Starting").startsWithIgnoringCase("star").endsWith("ing").aliasIn("StrPropProps.PROP_130").build();
	public static final StrProp PROP_131 = StrProp.builder().notNull().defaultValue("startInG").startsWith("star").endsWithIgnoringCase("ing").build();
	public static final StrProp PROP_132 = StrProp.builder().notNull().defaultValue("dab").matches("[abcd]+").build();
	public static final StrProp PROP_133 = StrProp.builder().notNull().defaultValue("cd").oneOf("ab", "cd", "\"", "'").build();
	public static final StrProp PROP_134 = StrProp.builder().notNull().defaultValue("Cd").oneOfIgnoringCase("ab", "cd", "").build();
	public static final StrProp PROP_135 = StrProp.builder().notNull().defaultValue("preorder").startsWith("pre").build();
	public static final StrProp PROP_136 = StrProp.builder().notNull().defaultValue("Preorder").startsWithIgnoringCase("pre").build();

	//
	// Special Trimmers and Types
	public static final StrProp PROP_200 = StrProp.builder().trimmer(TrimToNullTrimmer.instance()).aliasIn("StrPropProps.PROP_200").build();
	public static final StrProp PROP_210 = StrProp.builder().valueType(UpperCaseParser.instance())
			.matches("[A-Z]+").aliasIn("StrPropProps.PROP_210").build();


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


