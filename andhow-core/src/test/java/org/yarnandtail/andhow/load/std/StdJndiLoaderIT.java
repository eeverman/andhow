package org.yarnandtail.andhow.load.std;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.*;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.api.Problem;
import org.yarnandtail.andhow.internal.ValueProblem;
import org.yarnandtail.andhow.junit5.*;
import org.yarnandtail.andhow.name.CaseInsensitiveNaming;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.property.StrProp;
import org.yarnandtail.andhow.util.NameUtil;

import javax.naming.InitialContext;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@KillAndHowBeforeEachTest
public class StdJndiLoaderIT {

	CaseInsensitiveNaming bns;

	public interface ValidParams {
		//Strings
		StrProp STR_XXX = StrProp.builder().endsWith("XXX").build();
		IntProp INT_TEN = IntProp.builder().greaterThan(10).build();
	}

	private StdJndiLoader loader;

	@BeforeEach
	public void initLoader() {
		loader = new StdJndiLoader();
		bns = new CaseInsensitiveNaming();
	}

	@Test	// Duplicates test in StdJndiLoaderTest, ensuring unit tests works as expected vs entire system.
	@EnableJndiForThisTestMethod
	public void testHappyPathFromStringsCompEnvAsURIs() throws Exception {

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:comp/env/org/yarnandtail/andhow/SimpleParams/");
		EnableJndiUtil.createSubcontexts(jndi, "java:org/yarnandtail/andhow/SimpleParams/");
		EnableJndiUtil.createSubcontexts(jndi, "org/yarnandtail/andhow/SimpleParams/");
		//


		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.STR_BOB)), "test");
		jndi.bind("java:" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.STR_NULL)), "not_null");
		jndi.bind("" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_TRUE)), "false");
		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_FALSE)), "true");
		jndi.bind("java:" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.FLAG_NULL)), "TRUE");
		jndi.bind("" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.INT_TEN)), "-999");
		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.INT_NULL)), "999");
		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.LNG_TEN)), "-999");
		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.LNG_NULL)), "999");
		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.LDT_2007_10_01)), "2007-11-02T00:00");
		jndi.bind("java:comp/env/" +
				bns.getUriName(NameUtil.getAndHowName(SimpleParams.class, SimpleParams.LDT_NULL)), "2007-11-02T00:00");


		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(SimpleParams.class);

		AndHow.setConfig(config);

		assertEquals("test", SimpleParams.STR_BOB.getValue());
		assertEquals("not_null", SimpleParams.STR_NULL.getValue());
		assertEquals(false, SimpleParams.FLAG_TRUE.getValue());
		assertEquals(true, SimpleParams.FLAG_FALSE.getValue());
		assertEquals(true, SimpleParams.FLAG_NULL.getValue());
		assertEquals(-999, SimpleParams.INT_TEN.getValue());
		assertEquals(999, SimpleParams.INT_NULL.getValue());
		assertEquals(-999, SimpleParams.LNG_TEN.getValue());
		assertEquals(999, SimpleParams.LNG_NULL.getValue());
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), SimpleParams.LDT_2007_10_01.getValue());
		assertEquals(LocalDateTime.parse("2007-11-02T00:00"), SimpleParams.LDT_NULL.getValue());
	}

	//
	//
	// Non-HappyPath
	// These tests use validation, which happens outside the loader.  They really should be moved
	// to an AndHowCore test or whereever the validation takes place.


	@Test
	@EnableJndiForThisTestMethod
	public void testValidationIsEnforcedWhenExactTypeUsed() throws Exception {

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:org/yarnandtail/andhow/load/std/StdJndiLoaderIT/ValidParams/");
		//

		jndi.bind("java:" + NameUtil.getAndHowName(ValidParams.class, ValidParams.INT_TEN), Integer.parseInt("9"));
		jndi.bind("java:" +
				bns.getUriName(NameUtil.getAndHowName(ValidParams.class, ValidParams.STR_XXX)), "YYY");



		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(ValidParams.class);

		AndHow.setConfig(config);

		AppFatalException e = assertThrows(AppFatalException.class, () -> AndHow.instance());

		List<Problem> vps = e.getProblems();

		assertEquals(2, vps.size());
	}

	@Test
	@EnableJndiForThisTestMethod
	public void testValidationIsEnforcedWhenConvertionUsed() throws Exception {

		//
		// Create a context and create subcontexts
		InitialContext jndi = new InitialContext();
		EnableJndiUtil.createSubcontexts(jndi, "java:org/yarnandtail/andhow/SimpleParams/");
		//

		jndi.bind("java:" + NameUtil.getAndHowName(ValidParams.class, ValidParams.INT_TEN), "9");



		AndHowConfiguration config = AndHowTestConfig.instance()
				.addOverrideGroup(ValidParams.class);

		AndHow.setConfig(config);

		AppFatalException e = assertThrows(AppFatalException.class, () -> AndHow.instance());

		List<Problem> vps = e.getProblems();

		assertEquals(1, vps.size());
		assertTrue(vps.get(0) instanceof ValueProblem);
		assertEquals(ValidParams.INT_TEN, ((ValueProblem) (vps.get(0))).getLoaderPropertyCoord().getProperty());
	}


}
