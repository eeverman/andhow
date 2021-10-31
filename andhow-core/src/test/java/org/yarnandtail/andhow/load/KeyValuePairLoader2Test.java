package org.yarnandtail.andhow.load;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.*;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.util.AndHowUtil;
import org.yarnandtail.andhow.ztest.StrPropProps;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class KeyValuePairLoader2Test extends BaseForLoaderTests {

	protected static final String STRPROP_BASE = StrPropProps.class.getCanonicalName() + ".";

	PropertyConfigurationMutable appDef;
	ValidatedValuesWithContextMutable appValuesBuilder;

	@BeforeEach
	public void init() throws Exception {
		appValuesBuilder = new ValidatedValuesWithContextMutable();
		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();
		appDef = new PropertyConfigurationMutable(bns);

		registerClass(appDef, StrPropProps.class);
	}

	protected void registerClass(final PropertyConfigurationMutable def, final Class<?> clazz) throws IllegalAccessException {
		GroupProxy proxy = AndHowUtil.buildGroupProxy(clazz);
		List<NameAndProperty> naps = AndHowUtil.getProperties(clazz);
		naps.forEach(nap -> def.addProperty(proxy, nap.property));
	}

	protected void printValues(final LoaderValues result, final Class<?> clazz) throws IllegalAccessException {
		List<NameAndProperty> naps = AndHowUtil.getProperties(clazz);

		for (NameAndProperty nap : naps) {
			Object val = result.getExplicitValue(nap.property);

			if (val != null) {
				String strVal = val.toString();
				strVal = strVal.replace("\"", "\\\"");
				strVal = strVal.replace("\t", "\\t");
				strVal = strVal.replace("\n", "\\n");

				System.out.println("assertEquals(\"" + strVal +
						"\", result.getExplicitValue(" + clazz.getSimpleName() + "." + nap.fieldName + "));");

			} else {
				System.out.println("assertNull(" +
						"result.getExplicitValue(" + clazz.getSimpleName() + "." + nap.fieldName + "));");

			}

		}
	}

	@Test
	public void setEveryValue1() throws IllegalAccessException {
		List<String> args = new ArrayList();

		// Null OK | No Default | No Validations
		args.add(STRPROP_BASE + "PROP_0" + "=" + "  two \nwords  ");
		// Null OK | Has Default | No Validations
		args.add(STRPROP_BASE + "PROP_10" + "=" + "  \" words in space \"  ");

		//
		// Null OK | No Default | Has Validations
		args.add("StrPropProps.PROP_20" + "=" + "  StaR\tT ing  ");
		args.add(STRPROP_BASE + "PROP_21" + "=" + "\t\"star\tInG\"\t");
		args.add(STRPROP_BASE + "PROP_22" + "=" + " \"a b\" ");
		args.add(STRPROP_BASE + "PROP_23" + "=" + " \"A B\" ");

		//
		// Null OK | Has Default | Has Validations
		args.add("StrPropProps.PROP_30" + "=" + "  \tStaRs star inging\t  ");
		args.add(STRPROP_BASE + "PROP_31" + "=" + "\t  \"star star iNgiNG\"  \t");
		args.add(STRPROP_BASE + "PROP_32" + "=" + " \"a b\" ");
		args.add(STRPROP_BASE + "PROP_33" + "=" + " \"A b\" ");

		//
		// Not Null

		// Not Null | No Default | No Validations
		args.add("StrPropProps.PROP_100" + "=" + " \" \" ");
		// Not Null | Has Default | No Validations
		args.add(STRPROP_BASE + "PROP_110" + "=" + " \"\" ");

		//
		// Not Null | No Default | Has Validations
		args.add("StrPropProps.PROP_120" + "=" + "\t  StaR\tstar\ting  \t");
		args.add(STRPROP_BASE + "PROP_121" + "=" + "\"star\tstar\tNG\"");
		args.add(STRPROP_BASE + "PROP_122" + "=" + "\t\b\n\r\f \"a b\" \t\b\n\r\f");
		args.add(STRPROP_BASE + "PROP_123" + "=" + " \t\b\n\r\f\"a B\"\t\b\n\r\f ");


		//
		// Not Null | Has Default | Has Validations
		args.add("StrPropProps.PROP_130" + "=" + "\t\b\n\r\f StaRs star inging \t\b\n\r\f ");
		args.add(STRPROP_BASE + "PROP_131" + "=" + " \t\b\n\r\f star star iNgiNG \t\b\n\r\f ");
		args.add(STRPROP_BASE + "PROP_132" + "=" + " \"a b\" ");
		args.add(STRPROP_BASE + "PROP_133" + "=" + " \"A b\" ");

		//
		// Special Trimmers and Types
		args.add("StrPropProps.PROP_200" + "=" + "  \" space_n_quotes \" ");
		args.add(STRPROP_BASE + "PROP_210" + "=" + " \" upperCaseMe \" ");


		//
		KeyValuePairLoader kvpl = new KeyValuePairLoader();
		kvpl.setKeyValuePairs(args);
		LoaderValues result = kvpl.load(appDef, appValuesBuilder);

		//printValues(result, StrPropProps.class);
		assertEveryValue1(result);
	}

	@Test
	public void setEveryValue2() throws IllegalAccessException {
		List<String> args = new ArrayList();

		// Null OK | No Default | No Validations
		args.add(STRPROP_BASE + "PROP_0" + "=" + "  two \nwords  ");
		// Null OK | Has Default | No Validations
		args.add(STRPROP_BASE + "PROP_10" + "=" + "  \" words in space \"  ");

		//
		// Null OK | No Default | Has Validations
		args.add("StrPropProps.PROP_20" + "=" + "  StaR\tT ing  ");
		args.add(STRPROP_BASE + "PROP_21" + "=" + "\t\"star\tInG\"\t");
		args.add(STRPROP_BASE + "PROP_22" + "=" + " \"a b\" ");
		args.add(STRPROP_BASE + "PROP_23" + "=" + " \"A B\" ");

		//
		// Null OK | Has Default | Has Validations
		args.add("StrPropProps.PROP_30" + "=" + "  \tStaRs star inging\t  ");
		args.add(STRPROP_BASE + "PROP_31" + "=" + "\t  \"star star iNgiNG\"  \t");
		args.add(STRPROP_BASE + "PROP_32" + "=" + " \"a b\" ");
		args.add(STRPROP_BASE + "PROP_33" + "=" + " \"A b\" ");

		//
		// Not Null

		// Not Null | No Default | No Validations
		args.add("StrPropProps.PROP_100" + "=" + " \" \" ");
		// Not Null | Has Default | No Validations
		args.add(STRPROP_BASE + "PROP_110" + "=" + " \"\" ");

		//
		// Not Null | No Default | Has Validations
		args.add("StrPropProps.PROP_120" + "=" + "\t  StaR\tstar\ting  \t");
		args.add(STRPROP_BASE + "PROP_121" + "=" + "\"star\tstar\tNG\"");
		args.add(STRPROP_BASE + "PROP_122" + "=" + "\t\b\n\r\f \"a b\" \t\b\n\r\f");
		args.add(STRPROP_BASE + "PROP_123" + "=" + " \t\b\n\r\f\"a B\"\t\b\n\r\f ");


		//
		// Not Null | Has Default | Has Validations
		args.add("StrPropProps.PROP_130" + "=" + "\t\b\n\r\f StaRs star inging \t\b\n\r\f ");
		args.add(STRPROP_BASE + "PROP_131" + "=" + " \t\b\n\r\f star star iNgiNG \t\b\n\r\f ");
		args.add(STRPROP_BASE + "PROP_132" + "=" + " \"a b\" ");
		args.add(STRPROP_BASE + "PROP_133" + "=" + " \"A b\" ");

		//
		// Special Trimmers and Types
		args.add("StrPropProps.PROP_200" + "=" + "  \" space_n_quotes \" ");
		args.add(STRPROP_BASE + "PROP_210" + "=" + " \" upperCaseMe \" ");


		//
		KeyValuePairLoader kvpl = new KeyValuePairLoader();
		kvpl.setKeyValuePairs(args);
		LoaderValues result = kvpl.load(appDef, appValuesBuilder);

		//printValues(result, StrPropProps.class);
		assertEveryValue1(result);
	}





	public void assertEveryValue1(LoaderValues result) {
		assertEquals("two \nwords", result.getExplicitValue(StrPropProps.PROP_0));
		assertEquals(" words in space ", result.getExplicitValue(StrPropProps.PROP_10));

		assertEquals("StaR\tT ing", result.getExplicitValue(StrPropProps.PROP_20));
		assertEquals("star\tInG", result.getExplicitValue(StrPropProps.PROP_21));
		assertEquals("a b", result.getExplicitValue(StrPropProps.PROP_22));
		assertEquals("A B", result.getExplicitValue(StrPropProps.PROP_23));

		assertEquals("StaRs star inging", result.getExplicitValue(StrPropProps.PROP_30));
		assertEquals("star star iNgiNG", result.getExplicitValue(StrPropProps.PROP_31));
		assertEquals("a b", result.getExplicitValue(StrPropProps.PROP_32));
		assertEquals("A b", result.getExplicitValue(StrPropProps.PROP_33));

		assertEquals(" ", result.getExplicitValue(StrPropProps.PROP_100));
		assertEquals("", result.getExplicitValue(StrPropProps.PROP_110));

		assertEquals("StaR\tstar\ting", result.getExplicitValue(StrPropProps.PROP_120));
		assertEquals("star\tstar\tNG", result.getExplicitValue(StrPropProps.PROP_121));
		assertEquals("a b", result.getExplicitValue(StrPropProps.PROP_122));
		assertEquals("a B", result.getExplicitValue(StrPropProps.PROP_123));

		assertEquals("StaRs star inging", result.getExplicitValue(StrPropProps.PROP_130));
		assertEquals("star star iNgiNG", result.getExplicitValue(StrPropProps.PROP_131));
		assertEquals("a b", result.getExplicitValue(StrPropProps.PROP_132));
		assertEquals("A b", result.getExplicitValue(StrPropProps.PROP_133));

		assertEquals("\" space_n_quotes \"", result.getExplicitValue(StrPropProps.PROP_200));
		assertEquals(" UPPERCASEME ", result.getExplicitValue(StrPropProps.PROP_210));
	}

}
