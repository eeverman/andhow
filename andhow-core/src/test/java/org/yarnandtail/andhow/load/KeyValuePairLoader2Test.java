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
			System.out.println("assertEquals(\"" +
					result.getExplicitValue(nap.property) +
					"\", result.getExplicitValue(" + clazz.getSimpleName() + "." + nap.fieldName + "));");

		}
	}


	@Test
	public void setEveryValue1() throws IllegalAccessException {
		List<String> args = new ArrayList();

		final String strPropBase = StrPropProps.class.getCanonicalName() + ".";

		// Null OK | No Default | No Validations
		args.add(strPropBase + "PROP_0" + "=" + "  two words  ");
		// Null OK | Has Default | No Validations
		args.add(strPropBase + "PROP_10" + "=" + "  \" words in space \"  ");

		//
		// Null OK | No Default | Has Validations
		args.add("StrPropProps.PROP_20" + "=" + "  StaRing  ");
		args.add(strPropBase + "PROP_21" + "=" + " \t starInG \t ");
		args.add(strPropBase + "PROP_22" + "=" + "  baddadcad  ");
		args.add(strPropBase + "PROP_23" + "=" + "  ab  ");
		args.add(strPropBase + "PROP_24" + "=" + "  \taB\t  ");
		args.add(strPropBase + "PROP_25" + "=" + "\t  preorder  \t");
		args.add(strPropBase + "PROP_26" + "=" + "  pReorder  ");

		//
		// Null OK | Has Default | Has Validations
		args.add("StrPropProps.PROP_30" + "=" + "StaRing");
		args.add(strPropBase + "PROP_31" + "=" + "starInG");
		args.add(strPropBase + "PROP_32" + "=" + "baddadcad");
		args.add(strPropBase + "PROP_33" + "=" + "\"");
		args.add(strPropBase + "PROP_34" + "=" + "\"\"");
		args.add(strPropBase + "PROP_35" + "=" + "preorder");
		args.add(strPropBase + "PROP_36" + "=" + "pReorder");

		//
		// Not Null

		// Not Null | No Default | No Validations
		args.add("StrPropProps.PROP_100" + "=" + "\"\"");
		// Not Null | Has Default | No Validations
		args.add(strPropBase + "PROP_110" + "=" + "\"\"");

		//
		// Not Null | No Default | Has Validations
		args.add("StrPropProps.PROP_120" + "=" + "StaRing");
		args.add(strPropBase + "PROP_121" + "=" + "starInG");
		args.add(strPropBase + "PROP_122" + "=" + "baddadcad");
		args.add(strPropBase + "PROP_123" + "=" + "'");
		args.add(strPropBase + "PROP_124" + "=" + "aB");
		args.add(strPropBase + "PROP_125" + "=" + "preorder");
		args.add(strPropBase + "PROP_126" + "=" + "pReorder");

		//
		// These won't need much testing since they have default
		// Not Null | Has Default | Has Validations
		args.add("StrPropProps.PROP_130" + "=" + "StaRing");
		args.add(strPropBase + "PROP_131" + "=" + "starInG");
		args.add(strPropBase + "PROP_132" + "=" + "baddadcad");
		args.add(strPropBase + "PROP_133" + "=" + "ab");
		args.add(strPropBase + "PROP_134" + "=" + "\"\"");
		args.add(strPropBase + "PROP_135" + "=" + "preorder");
		args.add(strPropBase + "PROP_136" + "=" + "pReorder");

		//
		// Special Trimmers and Types
		args.add("StrPropProps.PROP_200" + "=" + "  \" space_n_quotes \" ");
		args.add(strPropBase + "PROP_210" + "=" + "upperCaseMe");


		//
		KeyValuePairLoader kvpl = new KeyValuePairLoader();
		kvpl.setKeyValuePairs(args);
		LoaderValues result = kvpl.load(appDef, appValuesBuilder);
		printValues(result, StrPropProps.class);
	}


	public void assertEveryValue1(LoaderValues result) {
		assertEquals("two words", result.getExplicitValue(StrPropProps.PROP_0));
		assertEquals(" words in space ", result.getExplicitValue(StrPropProps.PROP_10));
		assertEquals("StaRing", result.getExplicitValue(StrPropProps.PROP_20));
		assertEquals("starInG", result.getExplicitValue(StrPropProps.PROP_21));
		assertEquals("baddadcad", result.getExplicitValue(StrPropProps.PROP_22));
		assertEquals("ab", result.getExplicitValue(StrPropProps.PROP_23));
		assertEquals("aB", result.getExplicitValue(StrPropProps.PROP_24));
		assertEquals("preorder", result.getExplicitValue(StrPropProps.PROP_25));
		assertEquals("pReorder", result.getExplicitValue(StrPropProps.PROP_26));
		assertEquals("StaRing", result.getExplicitValue(StrPropProps.PROP_30));
		assertEquals("starInG", result.getExplicitValue(StrPropProps.PROP_31));
		assertEquals("baddadcad", result.getExplicitValue(StrPropProps.PROP_32));
		assertEquals("\"", result.getExplicitValue(StrPropProps.PROP_33));
		assertEquals("", result.getExplicitValue(StrPropProps.PROP_34));
		assertEquals("preorder", result.getExplicitValue(StrPropProps.PROP_35));
		assertEquals("pReorder", result.getExplicitValue(StrPropProps.PROP_36));
		assertEquals("", result.getExplicitValue(StrPropProps.PROP_100));
		assertEquals("", result.getExplicitValue(StrPropProps.PROP_110));
		assertEquals("StaRing", result.getExplicitValue(StrPropProps.PROP_120));
		assertEquals("starInG", result.getExplicitValue(StrPropProps.PROP_121));
		assertEquals("baddadcad", result.getExplicitValue(StrPropProps.PROP_122));
		assertEquals("'", result.getExplicitValue(StrPropProps.PROP_123));
		assertEquals("aB", result.getExplicitValue(StrPropProps.PROP_124));
		assertEquals("preorder", result.getExplicitValue(StrPropProps.PROP_125));
		assertEquals("pReorder", result.getExplicitValue(StrPropProps.PROP_126));
		assertEquals("StaRing", result.getExplicitValue(StrPropProps.PROP_130));
		assertEquals("starInG", result.getExplicitValue(StrPropProps.PROP_131));
		assertEquals("baddadcad", result.getExplicitValue(StrPropProps.PROP_132));
		assertEquals("ab", result.getExplicitValue(StrPropProps.PROP_133));
		assertEquals("", result.getExplicitValue(StrPropProps.PROP_134));
		assertEquals("preorder", result.getExplicitValue(StrPropProps.PROP_135));
		assertEquals("pReorder", result.getExplicitValue(StrPropProps.PROP_136));
		assertEquals("\" space_n_quotes \"", result.getExplicitValue(StrPropProps.PROP_200));
		assertEquals("UPPERCASEME", result.getExplicitValue(StrPropProps.PROP_210));
	}

}
