package org.yarnandtail.andhow.load;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.yarnandtail.andhow.api.ParsingException;
import org.yarnandtail.andhow.api.ValidatedValue;
import org.yarnandtail.andhow.property.*;
import org.yarnandtail.andhow.valuetype.IntType;
import org.yarnandtail.andhow.valuetype.StrType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BaseLoaderTest extends BaseForLoaderTests {

	BaseLoader loader;

	TrimToNullTrimmer nullTrimmer;

	StrType strType;
	StrProp strProp;

	IntType intType;
	IntProp intProp;

	@BeforeEach
	public void setup() {
		loader = Mockito.spy(BaseLoader.class);
		nullTrimmer = Mockito.spy(TrimToNullTrimmer.instance());

		strType = spy(StrType.instance());
		strProp = StrProp.builder().trimmer(nullTrimmer).valueType(strType).build();

		intType = spy(IntType.instance());
		intProp = IntProp.builder().trimmer(nullTrimmer).valueType(intType).build();
	}

	@Test
	public void createValueShouldTrimStrPropWithPropTrimmerIfTrimReqForStrings() throws ParsingException {

		when(loader.isTrimmingRequiredForStringValues()).thenReturn(true);

		ValidatedValue vv = loader.createValue(strProp, "  AB  ");
		assertFalse(vv.hasProblems());
		assertEquals("AB", vv.getValue());
		assertEquals(0, vv.getProblems().size());
		assertSame(strProp, vv.getProperty());

		InOrder inOrder = inOrder(nullTrimmer, strType);
		inOrder.verify(nullTrimmer).trim("  AB  ");
		inOrder.verify(strType).parse("AB");
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void createValueShouldNotTrimStrPropWithPropTrimmerIfTrimNotReqForStrings() throws ParsingException {

		when(loader.isTrimmingRequiredForStringValues()).thenReturn(false);

		ValidatedValue vv = loader.createValue(strProp, "  AB  ");
		assertFalse(vv.hasProblems());
		assertEquals("  AB  ", vv.getValue());
		assertEquals(0, vv.getProblems().size());
		assertSame(strProp, vv.getProperty());

		verifyNoInteractions(nullTrimmer);
		verify(strType).parse("  AB  ");
	}


	//
	// The isTrimmingRequiredForStringValues() switch should only apply to String type properties,
	// so testing both settings for a non-String value to ensure non-string values are always trimmed.
	@Test
	public void createValueShouldTrimNonStrPropWithPropTrimmerIfTrimReqForStrings() throws ParsingException {

		when(loader.isTrimmingRequiredForStringValues()).thenReturn(true);

		ValidatedValue vv = loader.createValue(intProp, "  12  ");
		assertFalse(vv.hasProblems());
		assertEquals(Integer.valueOf(12), vv.getValue());
		assertEquals(0, vv.getProblems().size());
		assertSame(intProp, vv.getProperty());

		InOrder inOrder = inOrder(nullTrimmer, intType);
		inOrder.verify(nullTrimmer).trim("  12  ");
		inOrder.verify(intType).parse("12");
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void createValueShouldStillTrimNonStrPropWithPropTrimmerIfTrimNotReqForStrings() throws ParsingException {

		when(loader.isTrimmingRequiredForStringValues()).thenReturn(false);

		ValidatedValue vv = loader.createValue(intProp, "  12  ");
		assertFalse(vv.hasProblems());
		assertEquals(Integer.valueOf(12), vv.getValue());
		assertEquals(0, vv.getProblems().size());
		assertSame(intProp, vv.getProperty());

		InOrder inOrder = inOrder(nullTrimmer, intType);
		inOrder.verify(nullTrimmer).trim("  12  ");
		inOrder.verify(intType).parse("12");
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	public void mapNametoPropertyTest() {
		String basePath = SimpleParams.class.getCanonicalName() + ".";

		assertSame(SimpleParams.STR_BOB, loader.mapNametoProperty(appDef, "String_Bob"));
		assertSame(SimpleParams.STR_BOB, loader.mapNametoProperty(appDef, "Stringy.Bob"));
		assertSame(SimpleParams.STR_BOB, loader.mapNametoProperty(appDef, "Stringy.Bob"));
		assertSame(SimpleParams.STR_BOB, loader.mapNametoProperty(appDef, basePath + "STR_BOB"));
		assertNull(loader.mapNametoProperty(appDef, basePath + "IDontExist"));
	}

}
