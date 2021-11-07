package org.yarnandtail.andhow.zTestGroups;

import org.yarnandtail.andhow.api.ParsingException;
import org.yarnandtail.andhow.property.*;
import org.yarnandtail.andhow.valuetype.BaseValueType;
import org.yarnandtail.andhow.valuetype.FlagType;
import static org.yarnandtail.andhow.zTestGroups.RawValueType.*;

/*
 *  Key for values
 *  | Series   |NullOK?| Default | Valid? |
 *  | 0  - 99  | NA: Flags cannot be null |
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
public class FlagPropProps {

	//
	// Not Null

	//
	// Not Null | No Default | No Validations
	public static final FlagProp PROP_100 = FlagProp.builder().aliasIn("FlagPropProps.PROP_100").build();

	//
	// Not Null | Has Default | No Validations
	public static final FlagProp PROP_110 = FlagProp.builder().defaultValue(true).build();
	public static final FlagProp PROP_111 = FlagProp.builder().defaultValue(false).build();

	//
	// Special Trimmers and Types
	public static final FlagProp PROP_200 = FlagProp.builder()
			.valueType(new XOParser()).build();


	public static PropExpectations buildExpectations1() {

		PropExpectations exp = new PropExpectations();

		// Null OK | No Default | No Validations
		exp.add(PROP_100).raw(NO_VALUE.toString()).trimResult(true).noTrimSameAsTrim();

		// Not Null | Has Default | No Validations
		exp.add(PROP_110).raw(NO_VALUE_OR_DELIMITER.toString()).trimResult(true).noTrimSameAsTrim();
		exp.add(PROP_111).raw(NO_VALUE.toString()).trimResult(true).noTrimSameAsTrim();

		// Special type is true on 'X' only (though should still be true if present)
		exp.add(PROP_200).raw(NO_VALUE_OR_DELIMITER.toString()).trimResult(true).noTrimSameAsTrim();

		return exp;
	}

	public static PropExpectations buildExpectations2() {

		PropExpectations exp = new PropExpectations();

		// Null OK | No Default | No Validations
		exp.add(PROP_100).raw(" \t\b\n\r\f ").trimResult(true).noTrimSameAsTrim();

		// Not Null | Has Default | No Validations
		exp.add(PROP_110).raw("\t\b\n\r\f ").trimResult(true).noTrimSameAsTrim();
		exp.add(PROP_111).raw(" \t\b\n\r\f").trimResult(true).noTrimSameAsTrim();

		// Special type is true on 'X' only (though should still be true if present)
		exp.add(PROP_200).raw(" \t\b\n\r\f\t\b\n\r\f ").trimResult(true).noTrimSameAsTrim();

		return exp;
	}

	public static PropExpectations buildExpectations3() {

		PropExpectations exp = new PropExpectations();

		// Null OK | No Default | No Validations
		exp.add(PROP_100).raw(" true ").trimResult(true).noTrimSameAsTrim();

		// Not Null | Has Default | No Validations
		exp.add(PROP_110).raw(" yes ").trimResult(true).noTrimSameAsTrim();
		exp.add(PROP_111).raw(" y ").trimResult(true).noTrimSameAsTrim();

		// Special type is true on 'X' only (though should still be true if present)
		exp.add(PROP_200).raw(" X ").trimResult(true).noTrimSameAsTrim();

		return exp;
	}

	public static PropExpectations buildExpectations4() {

		PropExpectations exp = new PropExpectations();

		// Null OK | No Default | No Validations
		exp.add(PROP_100).raw(" false ").trimResult(false).noTrimSameAsTrim();

		// Not Null | Has Default | No Validations
		exp.add(PROP_110).raw(" xxx ").trimResult(false).noTrimSameAsTrim();
		exp.add(PROP_111).raw("  1234.5725\"  ").trimResult(false).noTrimSameAsTrim();

		// Special type is true on 'X' only (though should still be true if present)
		exp.add(PROP_200).raw(" O ").trimResult(false).noTrimSameAsTrim();

		return exp;
	}

	public static PropExpectations buildExpectationsUnset() {

		PropExpectations exp = new PropExpectations();

		//
		// Nulls are returned b/c we are checking the explicit value.
		// Defaults are considered when calling getValue().

		// Null OK | No Default | No Validations
		exp.add(PROP_100).raw(SKIP.toString()).trimResult(null).noTrimSameAsTrim();

		// Not Null | Has Default | No Validations
		exp.add(PROP_110).raw(SKIP.toString()).trimResult(null).noTrimSameAsTrim();
		exp.add(PROP_111).raw(SKIP.toString()).trimResult(null).noTrimSameAsTrim();

		// Special type is true on 'X' only (though should still be true if present)
		exp.add(PROP_200).raw(SKIP.toString()).trimResult(null).noTrimSameAsTrim();

		return exp;
	}

		/**
		 * A custom parser that considers X to be true and O to be false.
		 */
	static public class XOParser extends FlagType {

		public XOParser() {
			super();
		}

		@Override
		public Boolean parse(String sourceValue) {
			if (sourceValue == null || "X".equals(sourceValue)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean isParsable(String sourceValue) {
			return (sourceValue == null) && (sourceValue.equals("X") || sourceValue.equals("O"));
		}
	}
}
