package org.yarnandtail.andhow.test.props;

import org.yarnandtail.andhow.api.ParsingException;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.property.TrimToNullTrimmer;
import org.yarnandtail.andhow.test.bulktest.PropExpectations;
import org.yarnandtail.andhow.valuetype.StrType;
import static org.yarnandtail.andhow.test.bulktest.RawValueType.*;
import static org.yarnandtail.andhow.test.props.StrPropProps.Conf.*;

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

	public static interface Conf {
		//
		// Null OK | No Default | No Validations
		StrProp PROP_0 = StrProp.builder().aliasIn("StrPropProps.PROP_0").build();

		//
		// Null OK | Has Default | No Validations
		StrProp PROP_10 = StrProp.builder().defaultValue(" prop 10 ").build();

		//
		// Null OK | No Default | Has Validations
		StrProp PROP_20 = StrProp.builder().startsWithIgnoringCase("star").endsWith("ing")
				.matches("[starSTARing \t]+").aliasIn("StrPropProps.PROP_20").build();
		StrProp PROP_21 = StrProp.builder().startsWith("star").endsWithIgnoringCase("ing")
				.matches("[staringING \t]+").build();
		StrProp PROP_22 = StrProp.builder().oneOf(" \"a b\" ", " cd ", "\"\"", " \"' ").build();
		StrProp PROP_23 = StrProp.builder().oneOfIgnoringCase(" \"a b\" ", " cd ", "\"\"", " \"' ").build();


		//
		// Null OK | Has Default | Has Validations
		StrProp PROP_30 = StrProp.builder().defaultValue("Star t ing")
				.startsWithIgnoringCase("star").endsWith("ing").matches("[starSTARing \t]+")
				.aliasIn("StrPropProps.PROP_30").build();
		StrProp PROP_31 = StrProp.builder().defaultValue("star t InG")
				.startsWith("star").endsWithIgnoringCase("ing").matches("[staringING \t]+").build();
		StrProp PROP_32 = StrProp.builder().defaultValue(" \"a b\" ")
				.oneOf(" \"a b\" ", " cd ", "\"\"", " \"' ").build();
		StrProp PROP_33 = StrProp.builder().defaultValue(" \"A B\" ")
				.oneOfIgnoringCase(" \"a b\" ", " cd ", "\"\"", " \"' ").build();

		//
		// Not Null

		//
		// Not Null | No Default | No Validations
		StrProp PROP_100 = StrProp.builder().notNull().aliasIn("StrPropProps.PROP_100").build();

		//
		// Not Null | Has Default | No Validations
		StrProp PROP_110 = StrProp.builder().notNull().defaultValue(" \"prop 110\" ").build();

		//
		// Not Null | No Default | Has Validations
		StrProp PROP_120 = StrProp.builder().notNull()
				.startsWithIgnoringCase("star").endsWith("ing").matches("[starSTARing \t]+").aliasIn("StrPropProps.PROP_120").build();
		StrProp PROP_121 = StrProp.builder().notNull()
				.startsWith("star").endsWithIgnoringCase("ing").matches("[staringING \t]+").build();
		StrProp PROP_122 = StrProp.builder().notNull()
				.oneOf(" \"a b\" ", " cd ", "\"\"", " \"' ").build();
		StrProp PROP_123 = StrProp.builder().notNull()
				.oneOfIgnoringCase(" \"a b\" ", " cd ", "\"\"", " \"' ").build();


		//
		// These won't need much testing since they have default
		// Not Null | Has Default | Has Validations
		StrProp PROP_130 = StrProp.builder().notNull().defaultValue("Star t ing")
				.startsWithIgnoringCase("star").endsWith("ing").matches("[starSTARing \t]+")
				.aliasIn("StrPropProps.PROP_130").build();
		StrProp PROP_131 = StrProp.builder().notNull().defaultValue("star t InG")
				.startsWith("star").endsWithIgnoringCase("ing").matches("[staringING \t]+").build();
		StrProp PROP_132 = StrProp.builder().notNull().defaultValue(" \"a b\" ")
				.oneOf(" \"a b\" ", " cd ", "\"\"", " \"' ").build();
		StrProp PROP_133 = StrProp.builder().notNull().defaultValue(" \"A B\" ")
				.oneOfIgnoringCase(" \"a b\" ", " cd ", "\"\"", " \"' ").build();

		//
		// Special Trimmers and Types
		StrProp PROP_200 = StrProp.builder().trimmer(TrimToNullTrimmer.instance())
				.aliasIn("StrPropProps.PROP_200").build();
		StrProp PROP_210 = StrProp.builder().valueType(new UpperCaseParser())
				.matches("[A-Z ]+").aliasIn("StrPropProps.PROP_210").build();
	}


	public static PropExpectations buildExpectations1() {

		PropExpectations exp = new PropExpectations(StrPropProps.Conf.class);

		// Null OK | No Default | No Validations
		exp.addStr(PROP_0).raw("  two \nwords  ").trimResult("two \nwords").noTrimSameAsRaw();

		// Null OK | Has Default | No Validations
		exp.addStr(PROP_10).raw("  \" words in space \"  ").trimResult(" words in space ").noTrimSameAsRaw();

		//
		// Null OK | No Default | Has Validations
		exp.addStr(PROP_20).raw("StaR\tT ing").trimResult("StaR\tT ing").noTrimResultIsSameAsTrim();
		exp.addStr(PROP_21).raw("\t\"star\tInG\"\t").trimResult("star\tInG").noTrimResultIsInvalidProb();
		exp.addStr(PROP_22).raw(" \" \"a b\" \" ").trimResult(" \"a b\" ").noTrimResultIsInvalidProb();
		exp.addStr(PROP_23).raw(" \" \"A B\" \" ").trimResult(" \"A B\" ").noTrimResultIsInvalidProb();

		//
		// Null OK | Has Default | Has Validations
		exp.addStr(PROP_30).raw("StaRs star inging").trimResult("StaRs star inging").noTrimResultIsSameAsTrim();
		exp.addStr(PROP_31).raw("\t  \"star star iNgiNG\"  \t").trimResult("star star iNgiNG").noTrimResultIsInvalidProb();
		exp.addStr(PROP_32).raw(" \" \"a b\" \" ").trimResult(" \"a b\" ").noTrimResultIsInvalidProb();
		exp.addStr(PROP_33).raw(" \" \"A b\" \" ").trimResult(" \"A b\" ").noTrimResultIsInvalidProb();

		//
		// Not Null

		// Not Null | No Default | No Validations
		exp.addStr(PROP_100).raw(" \" \" ").trimResult(" ").noTrimSameAsRaw();
		// Not Null | Has Default | No Validations
		exp.addStr(PROP_110).raw(" \"\" ").trimResult("").noTrimSameAsRaw();

		//
		// Not Null | No Default | Has Validations
		exp.addStr(PROP_120).raw("StaR\tstar\ting").trimResult("StaR\tstar\ting").noTrimResultIsSameAsTrim();
		exp.addStr(PROP_121).raw("\"star\tstar\tiNG\"").trimResult("star\tstar\tiNG").noTrimResultIsInvalidProb();
		exp.addStr(PROP_122).raw("\t\b\n\r\f\" \"a b\" \"\t\b\n\r\f").trimResult(" \"a b\" ").noTrimResultIsInvalidProb();
		exp.addStr(PROP_123).raw(" \t\b\n\r\f \" \"a B\" \" \t\b\n\r\f ").trimResult(" \"a B\" ").noTrimResultIsInvalidProb();


		//
		// Not Null | Has Default | Has Validations
		exp.addStr(PROP_130).raw("\t\b\n\r\f StaRs star inging \t\b\n\r\f ").trimResult("StaRs star inging").noTrimResultIsInvalidProb();
		exp.addStr(PROP_131).raw(" \t\b\n\r\f star star iNgiNG \t\b\n\r\f ").trimResult("star star iNgiNG").noTrimResultIsInvalidProb();
		exp.addStr(PROP_132).raw(" \" \"a b\" \" ").trimResult(" \"a b\" ").noTrimResultIsInvalidProb();
		exp.addStr(PROP_133).raw(" \" \"A b\" \" ").trimResult(" \"A b\" ").noTrimResultIsInvalidProb();

		//
		// Special Trimmers and Types
		exp.addStr(PROP_200).raw("  \" space_n_quotes \" ")
				.trimResult("\" space_n_quotes \"").noTrimResult("  \" space_n_quotes \" ");
		exp.addStr(PROP_210).raw(" \" upperCaseMe \" ")
				.trimResult(" UPPERCASEME ").noTrimResultIsInvalidProb();

		return exp;
	}


	public static PropExpectations buildExpectationsUnset() {

		PropExpectations exp = new PropExpectations(StrPropProps.Conf.class);

		// Null OK | No Default | No Validations
		exp.addStr(PROP_0).raw(SKIP.toString()).trimResult(null).noTrimResultIsSameAsTrim();

		// Null OK | Has Default | No Validations
		exp.addStr(PROP_10).raw(NO_VALUE.toString()).trimResult(null).noTrimResultIsSameAsTrim();

		//
		// Null OK | No Default | Has Validations
		exp.addStr(PROP_20).raw(NO_VALUE_OR_DELIMITER.toString()).trimResult(null).noTrimResultIsSameAsTrim();
		exp.addStr(PROP_21).raw(SKIP.toString()).trimResult(null).noTrimResultIsSameAsTrim();
		exp.addStr(PROP_22).raw(NO_VALUE.toString()).trimResult(null).noTrimResultIsSameAsTrim();
		exp.addStr(PROP_23).raw(NO_VALUE_OR_DELIMITER.toString()).trimResult(null).noTrimResultIsSameAsTrim();

		//
		// Null OK | Has Default | Has Validations
		exp.addStr(PROP_30).raw(SKIP.toString()).trimResult(null).noTrimResultIsSameAsTrim();
		exp.addStr(PROP_31).raw(NO_VALUE.toString()).trimResult(null).noTrimResultIsSameAsTrim();
		exp.addStr(PROP_32).raw(NO_VALUE_OR_DELIMITER.toString()).trimResult(null).noTrimResultIsSameAsTrim();
		exp.addStr(PROP_33).raw(SKIP.toString()).trimResult(null).noTrimResultIsSameAsTrim();

		//
		// Not Null

		// Not Null | No Default | No Validations
		exp.addStr(PROP_100).raw(SKIP.toString()).trimResultIsMissingProb().noTrimResultIsSameAsTrim();
		// Not Null | Has Default | No Validations
		exp.addStr(PROP_110).raw(NO_VALUE.toString()).trimResult(null).noTrimResultIsSameAsTrim();

		//
		// Not Null | No Default | Has Validations
		exp.addStr(PROP_120).raw(NO_VALUE_OR_DELIMITER.toString()).trimResultIsMissingProb().noTrimResultIsSameAsTrim();
		exp.addStr(PROP_121).raw(SKIP.toString()).trimResultIsMissingProb().noTrimResultIsSameAsTrim();
		exp.addStr(PROP_122).raw(NO_VALUE.toString()).trimResultIsMissingProb().noTrimResultIsSameAsTrim();
		exp.addStr(PROP_123).raw(NO_VALUE_OR_DELIMITER.toString()).trimResultIsMissingProb().noTrimResultIsSameAsTrim();


		//
		// Not Null | Has Default | Has Validations
		exp.addStr(PROP_130).raw(SKIP.toString()).trimResult(null).noTrimResultIsSameAsTrim();
		exp.addStr(PROP_131).raw(NO_VALUE.toString()).trimResult(null).noTrimResultIsSameAsTrim();
		exp.addStr(PROP_132).raw(NO_VALUE_OR_DELIMITER.toString()).trimResult(null).noTrimResultIsSameAsTrim();
		exp.addStr(PROP_133).raw(SKIP.toString()).trimResult(null).noTrimResultIsSameAsTrim();

		//
		// Special Trimmers and Types
		exp.addStr(PROP_200).raw(NO_VALUE.toString()).trimResult(null).noTrimResultIsSameAsTrim();
		exp.addStr(PROP_210).raw(NO_VALUE_OR_DELIMITER.toString()).trimResult(null).noTrimResultIsSameAsTrim();

		return exp;
	}

	public static PropExpectations buildInvalid1() {

		PropExpectations exp = new PropExpectations(StrPropProps.Conf.class);

		// Null OK | No Default | No Validations
		exp.addStr(PROP_0).raw("  two \nwords  ").trimResult("two \nwords").noTrimSameAsRaw();

		// Null OK | Has Default | No Validations
		exp.addStr(PROP_10).raw("  \" words in space \"  ").trimResult(" words in space ").noTrimSameAsRaw();

		//
		// Null OK | No Default | Has Validations
		exp.addStr(PROP_20).raw("  XXX  ").trimResultIsInvalidProb().noTrimResultIsSameAsTrim();
		exp.addStr(PROP_21).raw("\t\"Star\tInG\"\t").trimResultIsInvalidProb().noTrimResultIsSameAsTrim();
		exp.addStr(PROP_22).raw(" \" \"A B\" \" ").trimResultIsInvalidProb().noTrimResultIsSameAsTrim();
		exp.addStr(PROP_23).raw(" \" A B \" ").trimResultIsInvalidProb().noTrimResultIsSameAsTrim();

		//
		// Null OK | Has Default | Has Validations
		exp.addStr(PROP_30).raw("  \tStaRs star ingING\t  ").trimResultIsInvalidProb().noTrimResultIsSameAsTrim();
		exp.addStr(PROP_31).raw("\t  \"STAR star iNgiNG\"  \t").trimResultIsInvalidProb().noTrimResultIsSameAsTrim();
		exp.addStr(PROP_32).raw(" \" \"A B\" \" ").trimResultIsInvalidProb().noTrimResultIsSameAsTrim();
		exp.addStr(PROP_33).raw(" \" A b \" ").trimResultIsInvalidProb().noTrimResultIsSameAsTrim();

		//
		// Not Null

		// Not Null | No Default | No Validations
		exp.addStr(PROP_100).raw(" \" \" ").trimResult(" ").noTrimSameAsRaw();
		// Not Null | Has Default | No Validations
		exp.addStr(PROP_110).raw(" \"\" ").trimResult("").noTrimSameAsRaw();

		//
		// Not Null | No Default | Has Validations
		exp.addStr(PROP_120).raw("\t  XStaR\tstar\ting  \t").trimResultIsInvalidProb().noTrimResultIsSameAsTrim();
		exp.addStr(PROP_121).raw("\"Xstar\tstar\tiNG\"").trimResultIsInvalidProb().noTrimResultIsSameAsTrim();
		exp.addStr(PROP_122).raw("\t\b\n\r\f\" \"zzz\" \"\t\b\n\r\f").trimResultIsInvalidProb().noTrimResultIsSameAsTrim();
		exp.addStr(PROP_123).raw(" \t\b\n\r\f \" \"zzz\" \" \t\b\n\r\f ").trimResultIsInvalidProb().noTrimResultIsSameAsTrim();


		//
		// Not Null | Has Default | Has Validations
		exp.addStr(PROP_130).raw("\t\b\n\r\f StaRs star inginG \t\b\n\r\f ").trimResultIsInvalidProb().noTrimResultIsSameAsTrim();
		exp.addStr(PROP_131).raw(" \t\b\n\r\f Star star iNgiNG \t\b\n\r\f ").trimResultIsInvalidProb().noTrimResultIsSameAsTrim();
		exp.addStr(PROP_132).raw(" \" \"A b\" \" ").trimResultIsInvalidProb().noTrimResultIsSameAsTrim();
		exp.addStr(PROP_133).raw(" \" \"xxx\" \" ").trimResultIsInvalidProb().noTrimResultIsSameAsTrim();

		//
		// Special Trimmers and Types
		exp.addStr(PROP_200).raw("  \" space_n_quotes \" ")
				.trimResult("\" space_n_quotes \"").noTrimResult("  \" space_n_quotes \" ");
		exp.addStr(PROP_210).raw(" \" upperCaseMe \" ")
				.trimResult(" UPPERCASEME ").noTrimResultIsInvalidProb();

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


