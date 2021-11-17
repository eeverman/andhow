package org.yarnandtail.andhow.load;

import org.junit.jupiter.api.BeforeEach;
import org.yarnandtail.andhow.api.GroupProxy;
import org.yarnandtail.andhow.internal.PropertyConfigurationMutable;
import org.yarnandtail.andhow.internal.ValidatedValuesWithContextMutable;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.*;
import org.yarnandtail.andhow.util.AndHowUtil;

/**
 * This is intended to be a base class for tests that directly test a single loader.
 * As such, tests cannot apply default values or detect missing required values since
 * that happens only after all loaders have run and higher level logic is applied.
 * Thus, none of the example parameters have defaults or required flags.
 *
 * @author eeverman
 */
public class BaseForLoaderTests {
	protected PropertyConfigurationMutable appDef;
	protected ValidatedValuesWithContextMutable appValuesBuilder;

	@BeforeEach
	public void init() throws Exception {
		appValuesBuilder = new ValidatedValuesWithContextMutable();
		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();
		appDef = new PropertyConfigurationMutable(bns);
		GroupProxy proxy = AndHowUtil.buildGroupProxy(SimpleParams.class);

		appDef.addProperty(proxy, SimpleParams.STR_BOB);
		appDef.addProperty(proxy, SimpleParams.STR_NULL);
		appDef.addProperty(proxy, SimpleParams.STR_ENDS_WITH_XXX);

		appDef.addProperty(proxy, SimpleParams.LNG_TIME);
		appDef.addProperty(proxy, SimpleParams.INT_NUMBER);
		appDef.addProperty(proxy, SimpleParams.DBL_NUMBER);

		appDef.addProperty(proxy, SimpleParams.FLAG_FALSE);
		appDef.addProperty(proxy, SimpleParams.FLAG_TRUE);
		appDef.addProperty(proxy, SimpleParams.FLAG_NULL);

	}

	public interface SimpleParams {
		//Strings
		StrProp STR_BOB = StrProp.builder().aliasIn("String_Bob").aliasInAndOut("Stringy.Bob").defaultValue("bob").build();
		StrProp STR_NULL = StrProp.builder().aliasInAndOut("String_Null").build();
		StrProp STR_ENDS_WITH_XXX = StrProp.builder().endsWith("XXX").build();

		//Some Numbers
		LngProp LNG_TIME = LngProp.builder().aliasIn("lngIn").build();
		IntProp INT_NUMBER = IntProp.builder().aliasIn("intIn").build();
		DblProp DBL_NUMBER = DblProp.builder().aliasIn("dblIn").build();

		//Flags
		FlagProp FLAG_FALSE = FlagProp.builder().build();
		FlagProp FLAG_TRUE = FlagProp.builder().build();
		FlagProp FLAG_NULL = FlagProp.builder().build();
	}
}
