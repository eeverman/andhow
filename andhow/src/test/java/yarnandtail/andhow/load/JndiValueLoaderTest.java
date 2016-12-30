package yarnandtail.andhow.load;

import javax.naming.NamingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import yarnandtail.andhow.AndHow;
import yarnandtail.andhow.AndHowTestBase;
import static yarnandtail.andhow.AndHowTestBase.reloader;
import yarnandtail.andhow.PropertyGroup;
import yarnandtail.andhow.SimpleParams;

/**
 *
 * @author ericeverman
 */
public class JndiValueLoaderTest extends AndHowTestBase {
	
	
	@Before
	public void setUp() throws NamingException, IllegalArgumentException, IllegalAccessException {
		
	}
	
	@After
	public void tearDown() throws NamingException {
		//AndHowTestBase.stopJndi();
	}

	/**
	 * Test of load method, of class JndiValueLoader.
	 */
	@Test
	public void testHappyPathFromStringsCompEnv() throws Exception {
		
		SimpleNamingContextBuilder jndi = AndHowTestBase.getJndi();

		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.STR_BOB), "test");
		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.STR_NULL), "not_null");
		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_TRUE), "false");
		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_FALSE), "true");
		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_NULL), "TRUE");
		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.INT_TEN), "-999");
		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.INT_NULL), "999");
		jndi.activate();
		
		AndHow.builder()
				.loader(new JndiValueLoader())
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
	public void testHappyPathFromObjectsCompEnv() throws Exception {
		
		SimpleNamingContextBuilder jndi = AndHowTestBase.getJndi();
		
		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.STR_BOB), "test");
		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.STR_NULL), "not_null");
		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_TRUE), Boolean.FALSE);
		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_FALSE), Boolean.TRUE);
		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_NULL), Boolean.TRUE);
		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.INT_TEN), new Integer(-999));
		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.INT_NULL), new Integer(999));
		
		jndi.activate();
		
		AndHow.builder()
				.loader(new JndiValueLoader())
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
	public void testHappyPathFromObjectsRoot() throws Exception {
		
		SimpleNamingContextBuilder jndi = AndHowTestBase.getJndi();
		
		//switching values slightly to make sure we are reading the correct ones
		jndi.bind("java:" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.STR_BOB), "test2");
		jndi.bind("java:" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.STR_NULL), "not_null2");
		jndi.bind("java:" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_TRUE), Boolean.FALSE);
		jndi.bind("java:" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_FALSE), Boolean.TRUE);
		jndi.bind("java:" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_NULL), Boolean.TRUE);
		jndi.bind("java:" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.INT_TEN), new Integer(-9999));
		jndi.bind("java:" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.INT_NULL), new Integer(9999));
		
		jndi.activate();
		
		AndHow.builder()
				.loader(new JndiValueLoader())
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


	
}
