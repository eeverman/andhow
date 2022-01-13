package org.yarnandtail.andhow.load.std;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.internal.*;
import org.yarnandtail.andhow.load.util.JndiContextSupplier;
import org.yarnandtail.andhow.load.util.LoaderEnvironmentBuilder;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.FlagProp;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.sample.JndiLoaderSamplePrinter;
import org.yarnandtail.andhow.util.AndHowUtil;

import javax.naming.Name;
import javax.naming.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StdJndiLoaderTest {


	PropertyConfigurationMutable appDef;
	ValidatedValuesWithContextMutable appValuesBuilder;
	StdJndiLoader loader;
	LoaderEnvironmentBuilder leb;

	public interface SimpleParams {
		//Strings
		StrProp STR_BOB = StrProp.builder().aliasIn("String_Bob").aliasInAndOut("Stringy.Bob").defaultValue("bob").build();
		StrProp STR_NULL = StrProp.builder().aliasInAndOut("String_Null").build();

		//Flags
		FlagProp FLAG_FALSE = FlagProp.builder().defaultValue(false).build();
		FlagProp FLAG_TRUE = FlagProp.builder().defaultValue(true).build();
		FlagProp FLAG_NULL = FlagProp.builder().build();
	}

	@BeforeEach
	public void init() throws Exception {

		loader = new StdJndiLoader();
		leb = new LoaderEnvironmentBuilder();

		appValuesBuilder = new ValidatedValuesWithContextMutable();
		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();

		appDef = new PropertyConfigurationMutable(new CaseInsensitiveNaming());

		GroupProxy proxy = AndHowUtil.buildGroupProxy(StdJndiLoaderTest.SimpleParams.class);

		appDef.addProperty(proxy, StdJndiLoaderTest.SimpleParams.STR_BOB);
		appDef.addProperty(proxy, StdJndiLoaderTest.SimpleParams.STR_NULL);
		appDef.addProperty(proxy, StdJndiLoaderTest.SimpleParams.FLAG_FALSE);
		appDef.addProperty(proxy, StdJndiLoaderTest.SimpleParams.FLAG_TRUE);
		appDef.addProperty(proxy, StdJndiLoaderTest.SimpleParams.FLAG_NULL);

	}

	@Test
	public void reflexiveValuesReturnExpectedValues() {
		assertTrue(loader instanceof LookupLoader);
		assertEquals("JNDI properties in the system-wide JNDI context", loader.getSpecificLoadDescription());
		assertNull(loader.getLoaderDialect());
		assertEquals("JNDI", loader.getLoaderType());
		assertFalse(loader.isFlaggable());
		assertFalse(loader.isTrimmingRequiredForStringValues());
		assertEquals(StdJndiLoader.CONFIG.class, loader.getClassConfig());
		assertTrue(loader.getConfigSamplePrinter() instanceof JndiLoaderSamplePrinter);
		assertTrue(loader.getInstanceConfig().isEmpty());
		assertFalse(loader.isFailedEnvironmentAProblem());
		loader.releaseResources();	// should cause no error
	}

	@Test
	public void setFailedEnvironmentAProblemWorks() {
		loader.setFailedEnvironmentAProblem(true);
		assertTrue(loader.isFailedEnvironmentAProblem());
		loader.setFailedEnvironmentAProblem(false);
		assertFalse(loader.isFailedEnvironmentAProblem());
	}

	@Test
	public void testSplit() {

		//This is the default
		List<String> result = loader.split("java:comp/env/, \"\",");
		assertEquals(2, result.size());
		assertEquals("java:comp/env/", result.get(0));
		assertEquals("", result.get(1));

		//Should leave prefix completely unmodified (not add a trailing slash)
		result = loader.split(" comp/env , , \"_\",x/y/z");
		assertEquals(3, result.size());
		assertEquals("comp/env", result.get(0));
		assertEquals("_", result.get(1));
		assertEquals("x/y/z", result.get(2));

		//Should be able to indicate an empty string and whitespace w/ double quotes.
		result = loader.split(" comp/env , \"\" , \" \"");
		assertEquals(3, result.size());
		assertEquals("comp/env", result.get(0));
		assertEquals("", result.get(1));
		assertEquals(" ", result.get(2));
	}


	@Test
	public void noProblemIfJndiNotPresentByDefault() throws Exception {
		leb.setJndiContextSupplier(new JndiContextSupplier.EmptyJndiContextSupplier());

		LoaderValues result = loader.load(appDef, leb.toImmutable(), appValuesBuilder);

		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
	}

	@Test
	public void isProblemIfJndiNotPresentAndSetToRequired() throws Exception {
		leb.setJndiContextSupplier(new JndiContextSupplier.EmptyJndiContextSupplier());

		loader.setFailedEnvironmentAProblem(true);	// should create a Problem now w/ no JNDI context

		LoaderValues result = loader.load(appDef, leb.toImmutable(), appValuesBuilder);

		assertEquals(1, result.getProblems().size());
		assertTrue(result.getProblems().get(0) instanceof LoaderProblem.JndiContextMissing);
	}

	@Test
	public void notAnErrorIfJndiContextReturnsNullForLookup() throws NamingException {
		Context context = Mockito.mock(Context.class);
		Mockito.when(context.lookup(Mockito.anyString())).thenReturn(null);
		Mockito.when(context.lookup(Mockito.any(Name.class))).thenReturn(null);

		JndiContextWrapper wrap = Mockito.mock(JndiContextWrapper.class);
		Mockito.when(wrap.getContext()).thenReturn(context);

		LoaderEnvironment le = Mockito.mock(LoaderEnvironment.class);
		Mockito.when(le.getJndiContext()).thenReturn(wrap);

		LoaderValues result = loader.load(appDef, le, appValuesBuilder);
		assertEquals(0, result.getProblems().size());
		assertEquals(0L, result.getValues().stream().filter(p -> p.hasProblems()).count());
	}
}
