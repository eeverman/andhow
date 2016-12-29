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
		
		SimpleNamingContextBuilder jndi = AndHowTestBase.getJndi();
		
		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.KVP_BOB), "test");
		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.KVP_NULL), "not_null");
		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_TRUE), "false");
		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_FALSE), "true");
		jndi.bind("java:comp/env/" + PropertyGroup.getCanonicalName(SimpleParams.class, SimpleParams.FLAG_NULL), "TRUE");
		jndi.activate();
		
		AndHow.builder()
				.loader(new JndiValueLoader())
				.group(SimpleParams.class)
				.reloadForNonPropduction(reloader);
	}
	
	@After
	public void tearDown() throws NamingException {
		AndHowTestBase.stopJndi();
	}

	/**
	 * Test of load method, of class JndiValueLoader.
	 */
	@Test
	public void testLoad() {
		assertEquals("test", SimpleParams.KVP_BOB.getValue());
		assertEquals("not_null", SimpleParams.KVP_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
	}

	
}
