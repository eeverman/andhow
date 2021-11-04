package org.yarnandtail.andhow.zTestGroups;

import org.yarnandtail.andhow.api.ParsingException;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.valuetype.IntType;

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

	//
	// Null OK | No Default | No Validations
	public static final IntProp PROP_0 = IntProp.builder().aliasIn("IntPropProps.PROP_0").build();

	//
	// Null OK | Has Default | No Validations
	public static final IntProp PROP_10 = IntProp.builder().defaultValue(10).build();

	//
	// Null OK | No Default | Has Validations
	public static final IntProp PROP_20 = IntProp.builder().greaterThanOrEqualTo(-5).lessThan(1000)
			.aliasIn("IntPropProps.PROP_20").build();
	public static final IntProp PROP_21 = IntProp.builder().greaterThan(-5).lessThanOrEqualTo(1000)
			.build();

	//
	// Null OK | Has Default | Has Validations
	public static final IntProp PROP_30 = IntProp.builder().defaultValue(30)
			.greaterThanOrEqualTo(-5).lessThan(1000)
			.aliasIn("IntPropProps.PROP_30").build();
	public static final IntProp PROP_31 = IntProp.builder().defaultValue(31)
			.greaterThan(-5).lessThanOrEqualTo(1000).build();

	//
	// Not Null

	//
	// Not Null | No Default | No Validations
	public static final IntProp PROP_100 = IntProp.builder().notNull().aliasIn("IntPropProps.PROP_100").build();

	//
	// Not Null | Has Default | No Validations
	public static final IntProp PROP_110 = IntProp.builder().notNull().defaultValue(110).build();

	//
	// Not Null | No Default | Has Validations
	public static final IntProp PROP_120 = IntProp.builder().notNull()
			.greaterThanOrEqualTo(-5).lessThan(1000).aliasIn("IntPropProps.PROP_120").build();
	public static final IntProp PROP_121 = IntProp.builder().notNull()
			.greaterThan(-5).lessThanOrEqualTo(1000).build();


	//
	// These won't need much testing since they have default
	// Not Null | Has Default | Has Validations
	public static final IntProp PROP_130 = IntProp.builder().notNull().defaultValue(130)
			.greaterThanOrEqualTo(-5).lessThan(1000)
			.aliasIn("IntPropProps.PROP_130").build();
	public static final IntProp PROP_131 = IntProp.builder().notNull().defaultValue(131)
			.greaterThan(-5).lessThanOrEqualTo(1000).build();

	//
	// Special Trimmers and Types
	public static final IntProp PROP_210 = IntProp.builder().valueType(new PlusOneType())
			.greaterThanOrEqualTo(-5).lessThan(1000).aliasIn("IntPropProps.PROP_210").build();


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


