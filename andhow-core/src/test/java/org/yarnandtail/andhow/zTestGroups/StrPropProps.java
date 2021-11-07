package org.yarnandtail.andhow.zTestGroups;

import org.yarnandtail.andhow.api.ParsingException;
import org.yarnandtail.andhow.internal.RequirementProblem;
import org.yarnandtail.andhow.internal.ValueProblem;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.property.TrimToNullTrimmer;
import org.yarnandtail.andhow.valuetype.StrType;
import static org.yarnandtail.andhow.zTestGroups.RawValueType.*;

/*
 *  Key for values
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
	public static final StrProp PROP_210 = StrProp.builder().valueType(new UpperCaseParser())
			.matches("[A-Z ]+").aliasIn("StrPropProps.PROP_210").build();



	public static PropExpectations buildExpectations1() {

		PropExpectations exp = new PropExpectations();

		// Null OK | No Default | No Validations
		exp.add(PROP_0).raw("  two \nwords  ").trimResult("two \nwords").noTrimSameAsRaw();

		// Null OK | Has Default | No Validations
		exp.add(PROP_10).raw("  \" words in space \"  ").trimResult(" words in space ").noTrimSameAsRaw();

		//
		// Null OK | No Default | Has Validations
		exp.add(PROP_20).raw("  StaR\tT ing  ").trimResult("StaR\tT ing").noTrimSameAsRaw();
		exp.add(PROP_21).raw("\t\"star\tInG\"\t").trimResult("star\tInG").noTrimSameAsRaw();
		exp.add(PROP_22).raw(" \" \"a b\" \" ").trimResult(" \"a b\" ").noTrimSameAsRaw();
		exp.add(PROP_23).raw(" \" \"A B\" \" ").trimResult(" \"A B\" ").noTrimSameAsRaw();

		//
		// Null OK | Has Default | Has Validations
		exp.add(PROP_30).raw("  \tStaRs star inging\t  ").trimResult("StaRs star inging").noTrimSameAsRaw();
		exp.add(PROP_31).raw("\t  \"star star iNgiNG\"  \t").trimResult("star star iNgiNG").noTrimSameAsRaw();
		exp.add(PROP_32).raw(" \" \"a b\" \" ").trimResult(" \"a b\" ").noTrimSameAsRaw();
		exp.add(PROP_33).raw(" \" \"A b\" \" ").trimResult(" \"A b\" ").noTrimSameAsRaw();

		//
		// Not Null

		// Not Null | No Default | No Validations
		exp.add(PROP_100).raw(" \" \" ").trimResult(" ").noTrimSameAsRaw();
		// Not Null | Has Default | No Validations
		exp.add(PROP_110).raw(" \"\" ").trimResult("").noTrimSameAsRaw();

		//
		// Not Null | No Default | Has Validations
		exp.add(PROP_120).raw("\t  StaR\tstar\ting  \t").trimResult("StaR\tstar\ting").noTrimSameAsRaw();
		exp.add(PROP_121).raw("\"star\tstar\tiNG\"").trimResult("star\tstar\tiNG").noTrimSameAsRaw();
		exp.add(PROP_122).raw("\t\b\n\r\f\" \"a b\" \"\t\b\n\r\f").trimResult(" \"a b\" ").noTrimSameAsRaw();
		exp.add(PROP_123).raw(" \t\b\n\r\f \" \"a B\" \" \t\b\n\r\f ").trimResult(" \"a B\" ").noTrimSameAsRaw();


		//
		// Not Null | Has Default | Has Validations
		exp.add(PROP_130).raw("\t\b\n\r\f StaRs star inging \t\b\n\r\f ").trimResult("StaRs star inging").noTrimSameAsRaw();
		exp.add(PROP_131).raw(" \t\b\n\r\f star star iNgiNG \t\b\n\r\f ").trimResult("star star iNgiNG").noTrimSameAsRaw();
		exp.add(PROP_132).raw(" \" \"a b\" \" ").trimResult(" \"a b\" ").noTrimSameAsRaw();
		exp.add(PROP_133).raw(" \" \"A b\" \" ").trimResult(" \"A b\" ").noTrimSameAsRaw();

		//
		// Special Trimmers and Types
		exp.add(PROP_200).raw("  \" space_n_quotes \" ")
				.trimResult("\" space_n_quotes \"").noTrimResult("  \" space_n_quotes \" ");
		exp.add(PROP_210).raw(" \" upperCaseMe \" ")
				.trimResult(" UPPERCASEME ").noTrimResult(" \" UPPERCASEME \" ");

		return exp;
	}


	public static PropExpectations buildExpectationsUnset() {

		PropExpectations exp = new PropExpectations();

		// Null OK | No Default | No Validations
		exp.add(PROP_0).raw(SKIP.toString()).trimResult(null).noTrimSameAsTrim();

		// Null OK | Has Default | No Validations
		exp.add(PROP_10).raw(NO_VALUE.toString()).trimResult(null).noTrimSameAsTrim();

		//
		// Null OK | No Default | Has Validations
		exp.add(PROP_20).raw(NO_VALUE_OR_DELIMITER.toString()).trimResult(null).noTrimSameAsTrim();
		exp.add(PROP_21).raw(SKIP.toString()).trimResult(null).noTrimSameAsTrim();
		exp.add(PROP_22).raw(NO_VALUE.toString()).trimResult(null).noTrimSameAsTrim();
		exp.add(PROP_23).raw(NO_VALUE_OR_DELIMITER.toString()).trimResult(null).noTrimSameAsTrim();

		//
		// Null OK | Has Default | Has Validations
		exp.add(PROP_30).raw(SKIP.toString()).trimResult(null).noTrimSameAsTrim();
		exp.add(PROP_31).raw(NO_VALUE.toString()).trimResult(null).noTrimSameAsTrim();
		exp.add(PROP_32).raw(NO_VALUE_OR_DELIMITER.toString()).trimResult(null).noTrimSameAsTrim();
		exp.add(PROP_33).raw(SKIP.toString()).trimResult(null).noTrimSameAsTrim();

		//
		// Not Null

		// Not Null | No Default | No Validations
		exp.add(PROP_100).raw(SKIP.toString()).trimResult(RequirementProblem.NonNullPropertyProblem.class).noTrimSameAsTrim();
		// Not Null | Has Default | No Validations
		exp.add(PROP_110).raw(NO_VALUE.toString()).trimResult(null).noTrimSameAsTrim();

		//
		// Not Null | No Default | Has Validations
		exp.add(PROP_120).raw(NO_VALUE_OR_DELIMITER.toString()).trimResult(RequirementProblem.NonNullPropertyProblem.class).noTrimSameAsTrim();
		exp.add(PROP_121).raw(SKIP.toString()).trimResult(RequirementProblem.NonNullPropertyProblem.class).noTrimSameAsTrim();
		exp.add(PROP_122).raw(NO_VALUE.toString()).trimResult(RequirementProblem.NonNullPropertyProblem.class).noTrimSameAsTrim();
		exp.add(PROP_123).raw(NO_VALUE_OR_DELIMITER.toString()).trimResult(RequirementProblem.NonNullPropertyProblem.class).noTrimSameAsTrim();


		//
		// Not Null | Has Default | Has Validations
		exp.add(PROP_130).raw(SKIP.toString()).trimResult(null).noTrimSameAsTrim();
		exp.add(PROP_131).raw(NO_VALUE.toString()).trimResult(null).noTrimSameAsTrim();
		exp.add(PROP_132).raw(NO_VALUE_OR_DELIMITER.toString()).trimResult(null).noTrimSameAsTrim();
		exp.add(PROP_133).raw(SKIP.toString()).trimResult(null).noTrimSameAsTrim();

		//
		// Special Trimmers and Types
		exp.add(PROP_200).raw(NO_VALUE.toString()).trimResult(null).noTrimSameAsTrim();
		exp.add(PROP_210).raw(NO_VALUE_OR_DELIMITER.toString()).trimResult(null).noTrimSameAsTrim();

		return exp;
	}

	public static PropExpectations buildInvalid1() {

		PropExpectations exp = new PropExpectations();

		// Null OK | No Default | No Validations
		exp.add(PROP_0).raw("  two \nwords  ").trimResult("two \nwords").noTrimSameAsRaw();

		// Null OK | Has Default | No Validations
		exp.add(PROP_10).raw("  \" words in space \"  ").trimResult(" words in space ").noTrimSameAsRaw();

		//
		// Null OK | No Default | Has Validations
		exp.add(PROP_20).raw("  XXX  ").trimResult(ValueProblem.InvalidValueProblem.class).noTrimSameAsTrim();
		exp.add(PROP_21).raw("\t\"Star\tInG\"\t").trimResult(ValueProblem.InvalidValueProblem.class).noTrimSameAsTrim();
		exp.add(PROP_22).raw(" \" \"A B\" \" ").trimResult(ValueProblem.InvalidValueProblem.class).noTrimSameAsTrim();
		exp.add(PROP_23).raw(" \" A B \" ").trimResult(ValueProblem.InvalidValueProblem.class).noTrimSameAsTrim();

		//
		// Null OK | Has Default | Has Validations
		exp.add(PROP_30).raw("  \tStaRs star ingING\t  ").trimResult(ValueProblem.InvalidValueProblem.class).noTrimSameAsTrim();
		exp.add(PROP_31).raw("\t  \"STAR star iNgiNG\"  \t").trimResult(ValueProblem.InvalidValueProblem.class).noTrimSameAsTrim();
		exp.add(PROP_32).raw(" \" \"A B\" \" ").trimResult(ValueProblem.InvalidValueProblem.class).noTrimSameAsTrim();
		exp.add(PROP_33).raw(" \" A b \" ").trimResult(ValueProblem.InvalidValueProblem.class).noTrimSameAsTrim();

		//
		// Not Null

		// Not Null | No Default | No Validations
		exp.add(PROP_100).raw(" \" \" ").trimResult(" ").noTrimSameAsRaw();
		// Not Null | Has Default | No Validations
		exp.add(PROP_110).raw(" \"\" ").trimResult("").noTrimSameAsRaw();

		//
		// Not Null | No Default | Has Validations
		exp.add(PROP_120).raw("\t  XStaR\tstar\ting  \t").trimResult(ValueProblem.InvalidValueProblem.class).noTrimSameAsTrim();
		exp.add(PROP_121).raw("\"Xstar\tstar\tiNG\"").trimResult(ValueProblem.InvalidValueProblem.class).noTrimSameAsTrim();
		exp.add(PROP_122).raw("\t\b\n\r\f\" \"zzz\" \"\t\b\n\r\f").trimResult(ValueProblem.InvalidValueProblem.class).noTrimSameAsTrim();
		exp.add(PROP_123).raw(" \t\b\n\r\f \" \"zzz\" \" \t\b\n\r\f ").trimResult(ValueProblem.InvalidValueProblem.class).noTrimSameAsTrim();


		//
		// Not Null | Has Default | Has Validations
		exp.add(PROP_130).raw("\t\b\n\r\f StaRs star inginG \t\b\n\r\f ").trimResult(ValueProblem.InvalidValueProblem.class).noTrimSameAsTrim();
		exp.add(PROP_131).raw(" \t\b\n\r\f Star star iNgiNG \t\b\n\r\f ").trimResult(ValueProblem.InvalidValueProblem.class).noTrimSameAsTrim();
		exp.add(PROP_132).raw(" \" \"A b\" \" ").trimResult(" \"a b\" ").trimResult(ValueProblem.InvalidValueProblem.class).noTrimSameAsTrim();
		exp.add(PROP_133).raw(" \" \"xxx\" \" ").trimResult(" \"A b\" ").trimResult(ValueProblem.InvalidValueProblem.class).noTrimSameAsTrim();

		//
		// Special Trimmers and Types
		exp.add(PROP_200).raw("  \" space_n_quotes \" ")
				.trimResult("\" space_n_quotes \"").noTrimResult("  \" space_n_quotes \" ");
		exp.add(PROP_210).raw(" \" upperCaseMe \" ")
				.trimResult(" UPPERCASEME ").noTrimResult(" \" UPPERCASEME \" ");

		return exp;
	}


	/**
	 * A custom String type that converts string values to UCase when it 'parses' them
	 */
	static public class UpperCaseParser extends StrType {

		private UpperCaseParser() {
			super();
		}

		/**
		 * Returns Uppercase
		 */
		@Override
		public String parse(String sourceValue) throws ParsingException {
			return sourceValue.toUpperCase();
		}

	}
}


