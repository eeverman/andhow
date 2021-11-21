package org.yarnandtail.andhow.test.props;

import org.yarnandtail.andhow.property.*;
import org.yarnandtail.andhow.test.bulktest.PropExpectations;
import org.yarnandtail.andhow.valuetype.FlagType;
import static org.yarnandtail.andhow.test.bulktest.RawValueType.*;
import static org.yarnandtail.andhow.test.props.FlagPropProps.Conf.*;

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

	public static interface Conf {
		//
		// Not Null

		//
		// Not Null | No Default | No Validations
		FlagProp PROP_100 = FlagProp.builder().aliasIn("FlagPropProps.PROP_100").build();

		//
		// Not Null | Has Default | No Validations
		FlagProp PROP_110 = FlagProp.builder().defaultValue(true).build();
		FlagProp PROP_111 = FlagProp.builder().defaultValue(false).build();

		//
		// Special Trimmers and Types
		FlagProp PROP_200 = FlagProp.builder()
				.valueType(new XOParser()).build();
	}

	public static PropExpectations buildExpectations1() {

		PropExpectations exp = new PropExpectations(FlagPropProps.Conf.class);

		// Null OK | No Default | No Validations
		exp.addFlag(PROP_100).raw(NO_VALUE).trimIsNull().flagResult(true);

		// Not Null | Has Default | No Validations
		exp.addFlag(PROP_110).raw(NO_VALUE_OR_DELIMITER).trimIsNull().flagResult(true);
		exp.addFlag(PROP_111).raw(NO_VALUE).trimIsNull().flagResult(true);

		// Special type is true on 'X' only (though should still be true if present)
		exp.addFlag(PROP_200).raw(NO_VALUE_OR_DELIMITER).trimIsNull().flagResult(true);

		return exp;
	}

	public static PropExpectations buildExpectations2() {

		PropExpectations exp = new PropExpectations(FlagPropProps.Conf.class);

		// Null OK | No Default | No Validations
		exp.addFlag(PROP_100).raw(" \t\b\n\r\f ").trimIsNull().flagResult(true);

		// Not Null | Has Default | No Validations
		exp.addFlag(PROP_110).raw("\t\b\n\r\f ").trimIsNull().flagResult(true);
		exp.addFlag(PROP_111).raw(" \t\b\n\r\f").trimIsNull().flagResult(true);

		// Special type is true on 'X' only (though should still be true if present)
		exp.addFlag(PROP_200).raw(" \t\b\n\r\f\t\b\n\r\f ").trimIsNull().flagResult(true);

		return exp;
	}

	public static PropExpectations buildExpectations3() {

		PropExpectations exp = new PropExpectations(FlagPropProps.Conf.class);

		// Null OK | No Default | No Validations
		exp.addFlag(PROP_100).raw(" true ").trimResult(true).flagResultIsSameAsTrim();

		// Not Null | Has Default | No Validations
		exp.addFlag(PROP_110).raw(" yes ").trimResult(true).flagResultIsSameAsTrim();
		exp.addFlag(PROP_111).raw(" y ").trimResult(true).flagResultIsSameAsTrim();

		// Special type is true on 'X' only (though should still be true if present)
		exp.addFlag(PROP_200).raw(" X ").trimResult(true).flagResultIsSameAsTrim();

		return exp;
	}

	public static PropExpectations buildExpectations4() {

		PropExpectations exp = new PropExpectations(FlagPropProps.Conf.class);

		// Null OK | No Default | No Validations
		exp.addFlag(PROP_100).raw(" false ").trimResult(false).flagResultIsSameAsTrim();

		// Not Null | Has Default | No Validations
		exp.addFlag(PROP_110).raw(" xxx ").trimResult(false).flagResultIsSameAsTrim();
		exp.addFlag(PROP_111).raw("  1234.5725\"  ").trimResult(false).flagResultIsSameAsTrim();

		// Special type is true on 'X' only (though should still be true if present)
		exp.addFlag(PROP_200).raw(" O ").trimResult(false).flagResultIsSameAsTrim();

		return exp;
	}

	public static PropExpectations buildExpectationsUnset() {

		PropExpectations exp = new PropExpectations(FlagPropProps.Conf.class);

		//
		// Nulls are returned b/c we are checking the explicit value.
		// Defaults are considered when calling getValue().

		// Null OK | No Default | No Validations
		exp.addFlag(PROP_100).raw(SKIP).trimIsNull().flagResultIsSameAsTrim();

		// Not Null | Has Default | No Validations
		exp.addFlag(PROP_110).raw(SKIP).trimIsNull().flagResultIsSameAsTrim();
		exp.addFlag(PROP_111).raw(SKIP).trimIsNull().flagResultIsSameAsTrim();

		// Special type is true on 'X' only (though should still be true if present)
		exp.addFlag(PROP_200).raw(SKIP).trimIsNull().flagResultIsSameAsTrim();

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

	}
}
