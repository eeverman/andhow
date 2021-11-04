package org.yarnandtail.andhow.zTestGroups;

import org.yarnandtail.andhow.api.ParsingException;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.property.TrimToNullTrimmer;
import org.yarnandtail.andhow.valuetype.StrType;

import java.util.List;

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



	public static PropExpectations buildEveryStrPropValue1() {

		PropExpectations exp = new PropExpectations();

		// Null OK | No Default | No Validations
		exp.add(PROP_0).rawStrings("%NAME%=" + "  two \nwords  ")
				.trimResult("two \nwords").noTrimResult("  two \nwords  ");

		// Null OK | Has Default | No Validations
		exp.add(PROP_10).rawStrings("%NAME%=" + "  \" words in space \"  ")
				.trimResult(" words in space ").noTrimResult("  \" words in space \"  ");

		//
		// Null OK | No Default | Has Validations
		exp.add(PROP_20).rawStrings("%NAME%=" + "  StaR\tT ing  ")
				.trimResult("StaR\tT ing").noTrimResult("  StaR\tT ing  ");
		exp.add(PROP_21).rawStrings("%NAME%=" + "\t\"star\tInG\"\t")
				.trimResult("star\tInG").noTrimResult("\t\"star\tInG\"\t");
		exp.add(PROP_22).rawStrings("%NAME%=" + " \" \"a b\" \" ")
				.trimResult(" \"a b\" ").noTrimResult(" \" \"a b\" \" ");
		exp.add(PROP_23).rawStrings("%NAME%=" + " \" \"A B\" \" ")
				.trimResult(" \"A B\" ").noTrimResult(" \" \"A B\" \" ");

		//
		// Null OK | Has Default | Has Validations
		exp.add(PROP_30).rawStrings("%NAME%=" + "  \tStaRs star inging\t  ")
				.trimResult("StaRs star inging").noTrimResult("  \tStaRs star inging\t  ");
		exp.add(PROP_31).rawStrings("%NAME%=" + "\t  \"star star iNgiNG\"  \t")
				.trimResult("star star iNgiNG").noTrimResult("\t  \"star star iNgiNG\"  \t");
		exp.add(PROP_32).rawStrings("%NAME%=" + " \" \"a b\" \" ")
				.trimResult(" \"a b\" ").noTrimResult(" \" \"a b\" \" ");
		exp.add(PROP_33).rawStrings("%NAME%=" + " \" \"A b\" \" ")
				.trimResult(" \"A b\" ").noTrimResult(" \" \"A b\" \" ");

		//
		// Not Null

		// Not Null | No Default | No Validations
		exp.add(PROP_100).rawStrings("%NAME%=" + " \" \" ").trimResult(" ").noTrimResult(" \" \" ");
		// Not Null | Has Default | No Validations
		exp.add(PROP_110).rawStrings("%NAME%=" + " \"\" ").trimResult("").noTrimResult(" \"\" ");

		//
		// Not Null | No Default | Has Validations
		exp.add(PROP_120).rawStrings("%NAME%=" + "\t  StaR\tstar\ting  \t")
				.trimResult("StaR\tstar\ting").noTrimResult("\t  StaR\tstar\ting  \t");
		exp.add(PROP_121).rawStrings("%NAME%=" + "\"star\tstar\tiNG\"")
				.trimResult("star\tstar\tiNG").noTrimResult("\"star\tstar\tiNG\"");
		exp.add(PROP_122).rawStrings("%NAME%=" + "\t\b\n\r\f\" \"a b\" \"\t\b\n\r\f")
				.trimResult(" \"a b\" ").noTrimResult("\t\b\n\r\f\" \"a b\" \"\t\b\n\r\f");
		exp.add(PROP_123).rawStrings("%NAME%=" + " \t\b\n\r\f \" \"a B\" \" \t\b\n\r\f ")
				.trimResult(" \"a B\" ").noTrimResult(" \t\b\n\r\f \" \"a B\" \" \t\b\n\r\f ");


		//
		// Not Null | Has Default | Has Validations
		exp.add(PROP_130).rawStrings("%NAME%=" + "\t\b\n\r\f StaRs star inging \t\b\n\r\f ")
				.trimResult("StaRs star inging").noTrimResult("\t\b\n\r\f StaRs star inging \t\b\n\r\f ");
		exp.add(PROP_131).rawStrings("%NAME%=" + " \t\b\n\r\f star star iNgiNG \t\b\n\r\f ")
				.trimResult("star star iNgiNG").noTrimResult(" \t\b\n\r\f star star iNgiNG \t\b\n\r\f ");
		exp.add(PROP_132).rawStrings("%NAME%=" + " \" \"a b\" \" ")
				.trimResult(" \"a b\" ").noTrimResult(" \" \"a b\" \" ");
		exp.add(PROP_133).rawStrings("%NAME%=" + " \" \"A b\" \" ")
				.trimResult(" \"A b\" ").noTrimResult(" \" \"A b\" \" ");

		//
		// Special Trimmers and Types
		exp.add(PROP_200).rawStrings("%NAME%=" + "  \" space_n_quotes \" ")
				.trimResult("\" space_n_quotes \"").noTrimResult("  \" space_n_quotes \" ");
		exp.add(PROP_210).rawStrings("%NAME%=" + " \" upperCaseMe \" ")
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


