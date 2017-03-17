package org.yarnandtail.andhow.load;

import java.time.LocalDateTime;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.AndHowTestBase;
import static org.yarnandtail.andhow.AndHowTestBase.reloader;
import org.yarnandtail.andhow.SimpleParams;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.api.PropertyValue;
import org.yarnandtail.andhow.internal.AndHowUtil;
import org.yarnandtail.andhow.internal.LoaderProblem;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;

/**
 *
 * @author ericeverman
 */
public class JndiLoaderTest extends AndHowTestBase {
	
	@Test
	public void testSplit() {
		JndiLoader loader = new JndiLoader();
		
		//This is the default
		List<String> result = loader.split("comp/env/, \"\"");
		assertEquals(2, result.size());
		assertEquals("comp/env/", result.get(0));
		assertEquals("", result.get(1));
		
		//Should add a trailing slash if missing
		result = loader.split(" comp/env , \"_\",x/y/z");
		assertEquals(3, result.size());
		assertEquals("comp/env/", result.get(0));
		assertEquals("_/", result.get(1));
		assertEquals("x/y/z/", result.get(2));
	}


	@Test
	public void testHappyPathFromStringsCompEnvAsURIs() throws Exception {
		
		SimpleNamingContextBuilder jndi = AndHowTestBase.getJndi();
		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();
		
		jndi.bind("java:comp/env/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.STR_BOB)), "test");
		jndi.bind("java:comp/env/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.STR_NULL)), "not_null");
		jndi.bind("java:comp/env/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_TRUE)), "false");
		jndi.bind("java:comp/env/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_FALSE)), "true");
		jndi.bind("java:comp/env/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_NULL)), "TRUE");
		jndi.bind("java:comp/env/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.INT_TEN)), "-999");
		jndi.bind("java:comp/env/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.INT_NULL)), "999");
		jndi.bind("java:comp/env/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.LNG_TEN)), "-999");
		jndi.bind("java:comp/env/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.LNG_NULL)), "999");
		jndi.bind("java:comp/env/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.LDT_2007_10_01)), "2007-11-02T00:00");
		jndi.bind("java:comp/env/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.LDT_NULL)), "2007-11-02T00:00");
		jndi.activate();
		
		AndHow.builder()
				.loader(new JndiLoader())
				.group(SimpleParams.class)
				.reloadForNonPropduction(reloader);
		
		assertEquals("test", SimpleParams.STR_BOB.getValue());
		assertEquals("not_null", SimpleParams.STR_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
		assertEquals(new Integer(-999), SimpleParams.INT_TEN.getValue());
		assertEquals(new Integer(999), SimpleParams.INT_NULL.getValue());
		assertEquals(new Long(-999), SimpleParams.LNG_TEN.getValue());
		assertEquals(new Long(999), SimpleParams.LNG_NULL.getValue());
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), SimpleParams.LDT_2007_10_01.getValue());
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), SimpleParams.LDT_NULL.getValue());
	}
	
	@Test
	public void testHappyPathFromStringsCompEnvAsClasspath() throws Exception {
		
		SimpleNamingContextBuilder jndi = AndHowTestBase.getJndi();

		jndi.bind("java:comp/env/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.STR_BOB), "test");
		jndi.bind("java:comp/env/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.STR_NULL), "not_null");
		jndi.bind("java:comp/env/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_TRUE), "false");
		jndi.bind("java:comp/env/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_FALSE), "true");
		jndi.bind("java:comp/env/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_NULL), "TRUE");
		jndi.bind("java:comp/env/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.INT_TEN), "-999");
		jndi.bind("java:comp/env/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.INT_NULL), "999");
		jndi.bind("java:comp/env/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.LNG_TEN), "-999");
		jndi.bind("java:comp/env/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.LNG_NULL), "999");
		jndi.bind("java:comp/env/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.LDT_2007_10_01), "2007-11-02T00:00");
		jndi.bind("java:comp/env/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.LDT_NULL), "2007-11-02T00:00");
		jndi.activate();
		
		AndHow.builder()
				.loader(new JndiLoader())
				.group(SimpleParams.class)
				.reloadForNonPropduction(reloader);
		
		assertEquals("test", SimpleParams.STR_BOB.getValue());
		assertEquals("not_null", SimpleParams.STR_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
		assertEquals(new Integer(-999), SimpleParams.INT_TEN.getValue());
		assertEquals(new Integer(999), SimpleParams.INT_NULL.getValue());
		assertEquals(new Long(-999), SimpleParams.LNG_TEN.getValue());
		assertEquals(new Long(999), SimpleParams.LNG_NULL.getValue());
		assertEquals(new Long(-999), SimpleParams.LNG_TEN.getValue());
		assertEquals(new Long(999), SimpleParams.LNG_NULL.getValue());
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), SimpleParams.LDT_2007_10_01.getValue());
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), SimpleParams.LDT_NULL.getValue());
	}
	
	@Test
	public void testHappyPathFromStringsFromAddedNonStdPaths() throws Exception {
		
		SimpleNamingContextBuilder jndi = AndHowTestBase.getJndi();
		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();
		
		jndi.bind("java:/test/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.STR_BOB)), "test");
		jndi.bind("java:/test/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.STR_NULL), "not_null");
		jndi.bind("java:test/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_TRUE), "false");
		jndi.bind("java:test/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_FALSE)), "true");
		jndi.bind("java:test/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_NULL), "TRUE");
		jndi.bind("java:myapp/root/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.INT_TEN), "-999");
		//This should still work
		jndi.bind("java:comp/env/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.INT_NULL), "999");
		jndi.activate();
		
		AndHow.builder()
				.loader(new FixedValueLoader(new PropertyValue(JndiLoader.CONFIG.ADDED_JNDI_ROOTS, "/test,    test  ,   myapp/root")))
				.loader(new JndiLoader())
				.group(SimpleParams.class)
				.reloadForNonPropduction(reloader);
		
		assertEquals("test", SimpleParams.STR_BOB.getValue());
		assertEquals("not_null", SimpleParams.STR_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
		assertEquals(new Integer(-999), SimpleParams.INT_TEN.getValue());
		assertEquals(new Integer(999), SimpleParams.INT_NULL.getValue());
	}
	
	@Test
	public void testHappyPathFromStringsFromAddedAndReplacementNonStdPaths() throws Exception {
		
		SimpleNamingContextBuilder jndi = AndHowTestBase.getJndi();
		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();
		
		jndi.bind("java:zip/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.STR_BOB)), "test");
		jndi.bind("java:xy/z/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.STR_NULL), "not_null");
		jndi.bind("java:/test/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_TRUE)), "false");
		jndi.bind("java:test/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_FALSE), "true");
		jndi.bind("java:test/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_NULL), "TRUE");
		jndi.bind("java:myapp/root/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.INT_TEN), "-999");
		//This should NOT work
		jndi.bind("java:comp/env/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.INT_NULL), "999");
		jndi.activate();
		
		AndHow.builder()
				.loader(new FixedValueLoader(
						new PropertyValue(JndiLoader.CONFIG.STANDARD_JNDI_ROOTS, "zip,xy/z/"),
						new PropertyValue(JndiLoader.CONFIG.ADDED_JNDI_ROOTS, "/test,    test  ,   myapp/root")
				))
				.loader(new JndiLoader())
				.group(SimpleParams.class)
				.reloadForNonPropduction(reloader);
		
		assertEquals("test", SimpleParams.STR_BOB.getValue());
		assertEquals("not_null", SimpleParams.STR_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
		assertEquals(new Integer(-999), SimpleParams.INT_TEN.getValue());
		assertNull(SimpleParams.INT_NULL.getValue());
	}
	
	@Test
	public void testHappyPathFromObjectsCompEnv() throws Exception {
		
		SimpleNamingContextBuilder jndi = AndHowTestBase.getJndi();
		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();
		
		jndi.bind("java:comp/env/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.STR_BOB)), "test");
		jndi.bind("java:comp/env/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.STR_NULL)), "not_null");
		jndi.bind("java:comp/env/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_TRUE), Boolean.FALSE);
		jndi.bind("java:comp/env/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_FALSE), Boolean.TRUE);
		jndi.bind("java:comp/env/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_NULL)), Boolean.TRUE);
		jndi.bind("java:comp/env/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.INT_TEN)), new Integer(-999));
		jndi.bind("java:comp/env/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.INT_NULL), new Integer(999));
		jndi.bind("java:comp/env/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.LNG_TEN)), new Long(-999));
		jndi.bind("java:comp/env/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.LNG_NULL), new Long(999));
		jndi.bind("java:comp/env/" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.LDT_2007_10_01)), LocalDateTime.parse("2007-11-02T00:00"));
		jndi.bind("java:comp/env/" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.LDT_NULL), LocalDateTime.parse("2007-11-02T00:00"));
		
		jndi.activate();
		
		AndHow.builder()
				.loader(new JndiLoader())
				.group(SimpleParams.class)
				.reloadForNonPropduction(reloader);
		
		
		assertEquals("test", SimpleParams.STR_BOB.getValue());
		assertEquals("not_null", SimpleParams.STR_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
		assertEquals(new Integer(-999), SimpleParams.INT_TEN.getValue());
		assertEquals(new Integer(999), SimpleParams.INT_NULL.getValue());
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), SimpleParams.LDT_2007_10_01.getValue());
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), SimpleParams.LDT_NULL.getValue());
	}
	
	@Test
	public void testHappyPathFromObjectsRoot() throws Exception {
		
		SimpleNamingContextBuilder jndi = AndHowTestBase.getJndi();
		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();
		
		//switching values slightly to make sure we are reading the correct ones
		jndi.bind("java:" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.STR_BOB), "test2");
		jndi.bind("java:" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.STR_NULL)), "not_null2");
		jndi.bind("java:" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_TRUE), Boolean.FALSE);
		jndi.bind("java:" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_FALSE)), Boolean.TRUE);
		jndi.bind("java:" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_NULL), Boolean.TRUE);
		jndi.bind("java:" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.INT_TEN), new Integer(-9999));
		jndi.bind("java:" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.INT_NULL)), new Integer(9999));
		
		jndi.activate();
		
		AndHow.builder()
				.loader(new JndiLoader())
				.group(SimpleParams.class)
				.reloadForNonPropduction(reloader);
		
		
		assertEquals("test2", SimpleParams.STR_BOB.getValue());
		assertEquals("not_null2", SimpleParams.STR_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
		assertEquals(new Integer(-9999), SimpleParams.INT_TEN.getValue());
		assertEquals(new Integer(9999), SimpleParams.INT_NULL.getValue());
	}
	

	//
	//
	// Non-HappyPath
	//
	
	@Test
	public void testDuplicateValues() throws Exception {
		
		SimpleNamingContextBuilder jndi = AndHowTestBase.getJndi();
		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();
		
		//switching values slightly to make sure we are reading the correct ones
		jndi.bind("java:" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.STR_BOB), "test2");
		jndi.bind("java:" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.STR_BOB)), "not_null2");

		jndi.activate();
		
		try {
			AndHow.builder()
					.loader(new JndiLoader())
					.group(SimpleParams.class)
					.reloadForNonPropduction(reloader);
		
			fail("Should not reach this point");
			
		} catch (AppFatalException e) {
			List<LoaderProblem> lps = e.getProblems().filter(LoaderProblem.class);
			
			assertEquals(1, lps.size());
			assertTrue(lps.get(0) instanceof LoaderProblem.DuplicatePropertyLoaderProblem);
			
		}
		
	}
	

	@Test
	public void testObjectConversionErrors() throws Exception {
		
		SimpleNamingContextBuilder jndi = AndHowTestBase.getJndi();
		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();
		
		jndi.bind("java:" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.INT_TEN), new Long(-9999));
		jndi.bind("java:" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.INT_NULL)), new Float(22));
		jndi.bind("java:" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.LNG_TEN), new Integer(-9999));
		jndi.activate();
		
		try {
			AndHow.builder()
					.loader(new JndiLoader())
					.group(SimpleParams.class)
					.reloadForNonPropduction(reloader);
		
			fail("Should not reach this point");
			
		} catch (AppFatalException e) {
			List<LoaderProblem> lps = e.getProblems().filter(LoaderProblem.class);
			
			assertEquals(3, lps.size());
			assertTrue(lps.get(0) instanceof LoaderProblem.ObjectConversionValueProblem);
			assertEquals(SimpleParams.INT_TEN, lps.get(0).getBadValueCoord().getProperty());
			assertTrue(lps.get(1) instanceof LoaderProblem.ObjectConversionValueProblem);
			assertEquals(SimpleParams.INT_NULL, lps.get(1).getBadValueCoord().getProperty());
			assertTrue(lps.get(2) instanceof LoaderProblem.ObjectConversionValueProblem);
			assertEquals(SimpleParams.LNG_TEN, lps.get(2).getBadValueCoord().getProperty());
		}
	}
	
	@Test
	public void testStringConversionErrors() throws Exception {
		
		SimpleNamingContextBuilder jndi = AndHowTestBase.getJndi();
		CaseInsensitiveNaming bns = new CaseInsensitiveNaming();
		
		jndi.bind("java:" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.INT_TEN), "234.567");
		jndi.bind("java:" + 
				bns.getUriName(AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.INT_NULL)), "Apple");
		jndi.bind("java:" + AndHowUtil.getCanonicalName(SimpleParams.class, SimpleParams.LNG_TEN), "234.567");
		jndi.activate();
		
		try {
			AndHow.builder()
					.loader(new JndiLoader())
					.group(SimpleParams.class)
					.reloadForNonPropduction(reloader);
		
			fail("Should not reach this point");
			
		} catch (AppFatalException e) {
			List<LoaderProblem> vps = e.getProblems().filter(LoaderProblem.class);
			
			assertEquals(3, vps.size());
			assertTrue(vps.get(0) instanceof LoaderProblem.StringConversionLoaderProblem);
			assertEquals(SimpleParams.INT_TEN, vps.get(0).getBadValueCoord().getProperty());
			assertTrue(vps.get(1) instanceof LoaderProblem.StringConversionLoaderProblem);
			assertEquals(SimpleParams.INT_NULL, vps.get(1).getBadValueCoord().getProperty());
			assertTrue(vps.get(2) instanceof LoaderProblem.StringConversionLoaderProblem);
			assertEquals(SimpleParams.LNG_TEN, vps.get(2).getBadValueCoord().getProperty());
		}
	
	}
	
	
}
