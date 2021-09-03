package org.yarnandtail.andhow.property;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.AppFatalException;
import org.yarnandtail.andhow.api.Problem;
import org.yarnandtail.andhow.api.ProblemList;
import org.yarnandtail.andhow.api.PropertyType;
import org.yarnandtail.andhow.internal.ValueProblem;
import org.yarnandtail.andhow.valuetype.StrType;

import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests basic Property methods in PropertyBase and also a few default methods in Property.
 *
 * Many of the Property methods are not directly unit testable and require AndHow
 * to be initialized for a meaningful test.  This class does that.
 */
public class PropertyBaseTest extends PropertyTestBase {

	@Test
	public void validationGroupGoodValuesTest() {
		this.buildConfig(this, "_good", Config.class);

		//
		// STR1 - "asdf" value config'ed in prop file

		assertEquals("asdf", Config.STR1.getValue());
		assertEquals("asdf", Config.STR1.getExplicitValue());
		assertEquals("asdf", Config.STR1.getValueAsString());
		assertNull(Config.STR1.getDefaultValue());
		assertEquals("Str1 Desc", Config.STR1.getDescription());
		assertEquals("Str1 Help", Config.STR1.getHelpText());
		assertEquals("org.yarnandtail.andhow.property.PropertyBaseTest.Config.STR1",
				Config.STR1.getCanonicalName());
		assertEquals(PropertyType.SINGLE_NAME_VALUE, Config.STR1.getPropertyType());
		assertTrue(Config.STR1.getTrimmer() instanceof QuotedSpacePreservingTrimmer);
		assertTrue(Config.STR1.getValueType() instanceof StrType);
		assertFalse(Config.STR1.isNonNullRequired());
		assertTrue(Config.STR1.isExplicitlySet());
		assertEquals(1, Config.STR1.getValidators().size());

		// Aliases
		assertThat(Config.STR1.getInAliases(),
				containsInAnyOrder("STR1IN1", "STR1IN2", "STR1INOUT1", "STR1INOUT2"));
		assertThat("TODO:  Out aliases should preserve order",
				Config.STR1.getOutAliases(),
				containsInAnyOrder("Str1Out1", "Str1Out2", "Str1InOut1", "Str1InOut2"));
		assertThat(Config.STR1.getRequestedAliases().stream()
						.map(a -> a.getActualName()).collect(Collectors.toList()),
				containsInAnyOrder("Str1In1", "Str1In2", "Str1Out1", "Str1Out2", "Str1InOut1", "Str1InOut2"));
		assertThat(Config.STR1.getRequestedAliases().stream().filter(a -> a.isIn())
						.map(a -> a.getActualName()).collect(Collectors.toList()),
				containsInAnyOrder("Str1In1", "Str1In2", "Str1InOut1", "Str1InOut2"));
		assertThat(Config.STR1.getRequestedAliases().stream().filter(a -> a.isOut())
						.map(a -> a.getActualName()).collect(Collectors.toList()),
				containsInAnyOrder("Str1Out1", "Str1Out2", "Str1InOut1", "Str1InOut2"));


		//
		// STR2 - No value config'ed

		assertNull(Config.STR2.getValue());
		assertNull(Config.STR2.getExplicitValue());
		assertNull(Config.STR2.getValueAsString());
		assertNull(Config.STR2.getDefaultValue());
		assertTrue(Config.STR2.getDescription().isEmpty());
		assertTrue(Config.STR2.getHelpText().isEmpty());
		assertEquals("org.yarnandtail.andhow.property.PropertyBaseTest.Config.STR2",
				Config.STR2.getCanonicalName());
		assertFalse(Config.STR2.isExplicitlySet());
		assertEquals(2, Config.STR2.getValidators().size());

		// Aliases
		assertThat(Config.STR2.getInAliases(),
				containsInAnyOrder("STR2IN1"));
		assertTrue(Config.STR2.getOutAliases().isEmpty());
		assertThat(Config.STR2.getRequestedAliases().stream()
						.map(a -> a.getActualName()).collect(Collectors.toList()),
				containsInAnyOrder("Str2In1"));
		assertThat(Config.STR2.getRequestedAliases().stream().filter(a -> a.isIn())
						.map(a -> a.getActualName()).collect(Collectors.toList()),
				containsInAnyOrder("Str2In1"));
		assertTrue(Config.STR2.getRequestedAliases().stream().filter(a -> a.isOut())
						.map(a -> a.getActualName()).collect(Collectors.toList()).isEmpty());


		//
		// STR3 - No value config'ed, but default is "xxx"

		assertEquals("xxx", Config.STR3.getValue());
		assertNull(Config.STR3.getExplicitValue());
		assertEquals("xxx", Config.STR3.getValueAsString());
		assertEquals("xxx", Config.STR3.getDefaultValue());
		assertTrue(Config.STR3.isNonNullRequired());
		assertFalse(Config.STR3.isExplicitlySet());
		assertEquals(0, Config.STR3.getValidators().size());

		// Aliases
		assertThat(Config.STR3.getInAliases(),
				containsInAnyOrder("STR3"));
		assertThat(Config.STR3.getOutAliases(),
				containsInAnyOrder("Str3"));
		assertThat(Config.STR3.getRequestedAliases().stream()
						.map(a -> a.getActualName()).collect(Collectors.toList()),
				containsInAnyOrder("Str3"));
		assertThat(Config.STR3.getRequestedAliases().stream().filter(a -> a.isIn())
						.map(a -> a.getActualName()).collect(Collectors.toList()),
				containsInAnyOrder("Str3"));
		assertThat(Config.STR3.getRequestedAliases().stream().filter(a -> a.isOut())
						.map(a -> a.getActualName()).collect(Collectors.toList()),
				containsInAnyOrder("Str3"));


		//
		// STR4 - No value config'ed

		// Aliases
		assertThat(Config.STR4.getInAliases(),
				containsInAnyOrder("STR4"));
		assertThat(Config.STR4.getOutAliases(),
				containsInAnyOrder("Str4"));
		assertThat(Config.STR4.getRequestedAliases().stream()
						.map(a -> a.getActualName()).collect(Collectors.toList()),
				containsInAnyOrder("Str4"));
		assertThat(Config.STR4.getRequestedAliases().stream().filter(a -> a.isIn())
						.map(a -> a.getActualName()).collect(Collectors.toList()),
				containsInAnyOrder("Str4"));
		assertThat(Config.STR4.getRequestedAliases().stream().filter(a -> a.isOut())
						.map(a -> a.getActualName()).collect(Collectors.toList()),
				containsInAnyOrder("Str4"));


	}

	//duplicate and overlapping alias in builder...

	public interface Config {
		StrProp STR1 = StrProp.builder().mustMatchRegex("[a-z]+")
				.aliasIn("Str1In1").aliasIn("Str1In2")
				.aliasOut("Str1Out1").aliasOut("Str1Out2")
				.aliasInAndOut("Str1InOut1").aliasInAndOut("Str1InOut2")
				.desc("Str1 Desc").helpText("Str1 Help").build();

		StrProp STR2 = StrProp.builder().mustMatchRegex("[a-z]+").mustEqual("aa", "bb")
				.aliasIn("Str2In1").aliasIn("Str2In1")	//duplicated
				.build();

		StrProp STR3 = StrProp.builder().defaultValue("xxx").mustBeNonNull()
				.aliasIn("Str3").aliasIn("Str3")	//duplicated
				.aliasInAndOut("Str3") //duplicated
				.build();

		StrProp STR4 = StrProp.builder()
				.aliasIn("Str4").aliasIn("Str4")	//duplicated
				.aliasInAndOut("Str4") //duplicated
				.aliasOut("Str4") //duplicated
				.build();

	}
}
