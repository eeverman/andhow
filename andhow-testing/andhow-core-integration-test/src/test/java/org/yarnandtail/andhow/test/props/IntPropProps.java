package org.yarnandtail.andhow.test.props;

import org.yarnandtail.andhow.api.ParsingException;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.test.bulktest.PropExpectations;
import org.yarnandtail.andhow.valuetype.IntType;

import static org.yarnandtail.andhow.test.bulktest.RawValueType.*;
import static org.yarnandtail.andhow.test.props.IntPropProps.Conf.*;

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
public class IntPropProps {

	public static interface Conf {
		//
		// Null OK | No Default | No Validations
		IntProp PROP_0 = IntProp.builder().aliasIn("IntPropProps.PROP_0").build();

		//
		// Null OK | Has Default | No Validations
		IntProp PROP_10 = IntProp.builder().defaultValue(10).build();

		//
		// Null OK | No Default | Has Validations
		IntProp PROP_20 = IntProp.builder().greaterThanOrEqualTo(-5).lessThan(1000)
				.aliasIn("IntPropProps.PROP_20").build();
		IntProp PROP_21 = IntProp.builder().greaterThan(-5).lessThanOrEqualTo(1000)
				.build();

		//
		// Null OK | Has Default | Has Validations
		IntProp PROP_30 = IntProp.builder().defaultValue(30)
				.greaterThanOrEqualTo(-5).lessThan(1000)
				.aliasIn("IntPropProps.PROP_30").build();
		IntProp PROP_31 = IntProp.builder().defaultValue(31)
				.greaterThan(-5).lessThanOrEqualTo(1000).build();

		//
		// Not Null

		//
		// Not Null | No Default | No Validations
		IntProp PROP_100 = IntProp.builder().notNull().aliasIn("IntPropProps.PROP_100").build();

		//
		// Not Null | Has Default | No Validations
		IntProp PROP_110 = IntProp.builder().notNull().defaultValue(110).build();

		//
		// Not Null | No Default | Has Validations
		IntProp PROP_120 = IntProp.builder().notNull()
				.greaterThanOrEqualTo(-5).lessThan(1000).aliasIn("IntPropProps.PROP_120").build();
		IntProp PROP_121 = IntProp.builder().notNull()
				.greaterThan(-5).lessThanOrEqualTo(1000).build();


		//
		// Not Null | Has Default | Has Validations
		IntProp PROP_130 = IntProp.builder().notNull().defaultValue(130)
				.greaterThanOrEqualTo(-5).lessThan(1000)
				.aliasIn("IntPropProps.PROP_130").build();
		IntProp PROP_131 = IntProp.builder().notNull().defaultValue(131)
				.greaterThan(-5).lessThanOrEqualTo(1000).build();

		//
		// Special Trimmers and Types
		IntProp PROP_210 = IntProp.builder().valueType(new PlusOneType())
				.greaterThanOrEqualTo(-5).lessThan(1000).aliasIn("IntPropProps.PROP_210").build();
	}


	public static PropExpectations buildExpectations1() {

		// Reminder:  For non-String based Properties, all loaders trim values,
		// so trim & untrim should be the same.
		
		PropExpectations exp = new PropExpectations(IntPropProps.Conf.class);

		// Null OK | No Default | No Validations
		exp.add(PROP_0).raw("  0  ").trimResult(0);

		// Null OK | Has Default | No Validations
		exp.add(PROP_10).raw("  10  ").trimResult(10);

		//
		// Null OK | No Default | Has Validations
		exp.add(PROP_20).raw("  20  ").trimResult(20);
		exp.add(PROP_21).raw("\t21\t").trimResult(21);

		//
		// Null OK | Has Default | Has Validations
		exp.add(PROP_30).raw(" \t\b\n\r\f 30 \t\b\n\r\f ").trimResult(30);
		exp.add(PROP_31).raw(" \t\b\n\r\f 31 \t\b\n\r\f ").trimResult(31);

		//
		// Not Null

		// Not Null | No Default | No Validations
		exp.add(PROP_100).raw(" 100 ").trimResult(100);
		// Not Null | Has Default | No Validations
		exp.add(PROP_110).raw(" 110 ").trimResult(110);

		//
		// Not Null | No Default | Has Validations
		exp.add(PROP_120).raw("\t  120 \t").trimResult(120);
		exp.add(PROP_121).raw("121").trimResult(121);

		//
		// Not Null | Has Default | Has Validations
		exp.add(PROP_130).raw("\t\b\n\r\f 130 \t\b\n\r\f ").trimResult(130);
		exp.add(PROP_131).raw(" \t\b\n\r\f 131 \t\b\n\r\f ").trimResult(131);

		//
		// Special Trimmers and Types
		exp.add(PROP_210).raw(" 210 ").trimResult(211);

		return exp;
	}


	public static PropExpectations buildExpectationsUnset() {

		// Reminder:  For non-String based Properties, all loaders trim values,
		// so trim & untrim should be the same.

		PropExpectations exp = new PropExpectations(IntPropProps.Conf.class);

		// Null OK | No Default | No Validations
		exp.add(PROP_0).raw(SKIP.toString()).trimResult(null);

		// Null OK | Has Default | No Validations
		exp.add(PROP_10).raw(NO_VALUE.toString()).trimResult(null);

		//
		// Null OK | No Default | Has Validations
		exp.add(PROP_20).raw(NO_VALUE_OR_DELIMITER.toString()).trimResult(null);
		exp.add(PROP_21).raw(SKIP.toString()).trimResult(null);

		//
		// Null OK | Has Default | Has Validations
		exp.add(PROP_30).raw(SKIP.toString()).trimResult(null);
		exp.add(PROP_31).raw(NO_VALUE.toString()).trimResult(null);

		//
		// Not Null

		// Not Null | No Default | No Validations
		exp.add(PROP_100).raw(SKIP.toString()).trimResultIsMissingProb();
		// Not Null | Has Default | No Validations
		exp.add(PROP_110).raw(NO_VALUE.toString()).trimResult(null);

		//
		// Not Null | No Default | Has Validations
		exp.add(PROP_120).raw(NO_VALUE_OR_DELIMITER.toString()).trimResultIsMissingProb();
		exp.add(PROP_121).raw(SKIP.toString()).trimResultIsMissingProb();


		//
		// Not Null | Has Default | Has Validations
		exp.add(PROP_130).raw(NO_VALUE.toString()).trimResult(null);
		exp.add(PROP_131).raw(NO_VALUE_OR_DELIMITER.toString()).trimResult(null);

		//
		// Special Trimmers and Types
		exp.add(PROP_210).raw(SKIP.toString()).trimResult(null);

		return exp;
	}


	public static PropExpectations buildInvalid1() {

		// Reminder:  For non-String based Properties, all loaders trim values,
		// so trim & untrim should be the same.

		PropExpectations exp = new PropExpectations(IntPropProps.Conf.class);

		// Null OK | No Default | No Validations
		exp.add(PROP_0).raw("  0  ").trimResult(0);

		// Null OK | Has Default | No Validations
		exp.add(PROP_10).raw("  10  ").trimResult(10);

		//
		// Null OK | No Default | Has Validations
		exp.add(PROP_20).raw("  -6  ").trimResultIsInvalidProb();
		exp.add(PROP_21).raw("\t1001\t").trimResultIsInvalidProb();

		//
		// Null OK | Has Default | Has Validations
		exp.add(PROP_30).raw(" \t\b\n\r\f 1000 \t\b\n\r\f ").trimResultIsInvalidProb();
		exp.add(PROP_31).raw(" \t\b\n\r\f -5 \t\b\n\r\f ").trimResultIsInvalidProb();

		//
		// Not Null

		// Not Null | No Default | No Validations
		exp.add(PROP_100).raw(" 100 ").trimResult(100);
		// Not Null | Has Default | No Validations
		exp.add(PROP_110).raw(" 110 ").trimResult(110);

		//
		// Not Null | No Default | Has Validations
		exp.add(PROP_120).raw("\t  1000 \t").trimResultIsInvalidProb();
		exp.add(PROP_121).raw("-5").trimResultIsInvalidProb();

		//
		// Not Null | Has Default | Has Validations
		exp.add(PROP_130).raw("\t\b\n\r\f 1000 \t\b\n\r\f ").trimResultIsInvalidProb();
		exp.add(PROP_131).raw(" \t\b\n\r\f -5 \t\b\n\r\f ").trimResultIsInvalidProb();

		//
		// Special Trimmers and Types
		exp.add(PROP_210).raw(" 999 ").trimResultIsInvalidProb();

		return exp;
	}


	/**
	 * A custom String type that converts string values to UCase when it 'parses' them
	 */
	static public class PlusOneType extends IntType {

		/**
		 * Returns value + 1
		 */
		@Override
		public Integer parse(String sourceValue) throws ParsingException {
			Integer orgVal = super.parse(sourceValue);

			if (orgVal != null) {
				return orgVal + 1;
			} else {
				return null;
			}
		}

	}
}


