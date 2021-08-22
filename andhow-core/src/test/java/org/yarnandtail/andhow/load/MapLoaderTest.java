package org.yarnandtail.andhow.load;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.api.LoaderValues;
import org.yarnandtail.andhow.api.ValidatedValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MapLoaderTest extends BaseForLoaderTests {

    private MapLoader loader;

    @BeforeEach
    public void initLoader() {
        this.loader = new MapLoader();
    }

    @Test
    public void setMapTest() {
        // return set values
        Map<String, String> expectedMap = Collections.singletonMap("key", "value");
        loader.setMap(expectedMap);

        Map<?, ?> actualMap = loader.getMap();

        assertEquals(expectedMap, actualMap);
        assertNotSame(expectedMap, actualMap);

        // replace old values
        expectedMap = Collections.singletonMap("key2", "value2");
        loader.setMap(expectedMap);

        actualMap = loader.getMap();

        assertEquals(expectedMap, actualMap);


        // clear map when called with empty
        loader.setMap(Collections.singletonMap("key", "value"));
        loader.setMap(Collections.emptyMap());

        assertTrue(loader.getMap().isEmpty());

        // clear map when called with null
        loader.setMap(Collections.singletonMap("key", "value"));
        loader.setMap(null);

        assertTrue(loader.getMap().isEmpty());
    }

    @Test
    public void loadMapTest() {
        String basePath = SimpleParams.class.getCanonicalName() + ".";

        Map<String, String> args = new HashMap<>();
        args.put(basePath + "STR_BOB", "test");
        args.put(basePath + "STR_NULL", "not_null");
        args.put(basePath + "STR_ENDS_WITH_XXX", "XXX");
        args.put(basePath + "FLAG_TRUE", "false");
        args.put(basePath + "FLAG_FALSE", "true");
        args.put(basePath + "FLAG_NULL", "true");

        loader.setMap(args);

        LoaderValues result = loader.load(appDef, appValuesBuilder);

        assertEquals(0, result.getProblems().size());
        assertEquals(0L, result.getValues().stream().filter(ValidatedValue::hasProblems).count());
        assertEquals("test", result.getExplicitValue(SimpleParams.STR_BOB));
        assertEquals("not_null", result.getExplicitValue(SimpleParams.STR_NULL));
        assertEquals("XXX", result.getExplicitValue(SimpleParams.STR_ENDS_WITH_XXX));
        assertEquals(Boolean.FALSE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
        assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
        assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
    }

    @Test
    public void loadEmptyValuesMapTest() {
        String basePath = SimpleParams.class.getCanonicalName() + ".";

        Map<String, String> args = new HashMap<>();
        args.put(basePath + "STR_BOB", "");
        args.put(basePath + "STR_NULL", "");
        args.put(basePath + "STR_ENDS_WITH_XXX", "");
        args.put(basePath + "FLAG_TRUE", "");
        args.put(basePath + "FLAG_FALSE", "");
        args.put(basePath + "FLAG_NULL", "");

        loader.setMap(args);

        LoaderValues result = loader.load(appDef, appValuesBuilder);

        assertEquals(0, result.getProblems().size());
        assertEquals(0L, result.getValues().stream().filter(ValidatedValue::hasProblems).count());

        assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
        assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
        assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
        assertNull(result.getValue(SimpleParams.STR_NULL));
        assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_TRUE));
        assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_FALSE));
        assertEquals(Boolean.TRUE, result.getExplicitValue(SimpleParams.FLAG_NULL));
    }

    @Test
    public void loadEmptyMapTest() {
        Map<String, String> args = Collections.emptyMap();

        loader.setMap(args);

        LoaderValues result = loader.load(appDef, appValuesBuilder);

        assertEquals(0, result.getProblems().size());
        assertEquals(0L, result.getValues().stream().filter(ValidatedValue::hasProblems).count());

        assertNull(result.getExplicitValue(SimpleParams.STR_BOB));
        assertEquals("bob", result.getValue(SimpleParams.STR_BOB));
        assertNull(result.getExplicitValue(SimpleParams.STR_NULL));
        assertNull(result.getValue(SimpleParams.STR_NULL));
    }

    @Test
    public void getSpecificLoadDescriptionTest() {
        String expectedDescription = "Map";

        assertEquals(expectedDescription, loader.getSpecificLoadDescription());
    }

    @Test
    public void isTrimmingRequiredForStringValuesTest() {
        assertTrue(loader.isTrimmingRequiredForStringValues());
    }

    @Test
    public void unknownPropertyAProblemTest() {
        assertTrue(loader.isUnknownPropertyAProblem());

        loader.setUnknownPropertyAProblem(false);

        assertFalse(loader.isUnknownPropertyAProblem());
    }

    @Test
    public void getLoaderTypeTest() {
        String expectedType = "Map";

        assertEquals(expectedType, loader.getLoaderType());
    }

    @Test
    public void getLoaderDialectTest() {
        assertNull(loader.getLoaderDialect());
    }

    @Test
    public void releaseResourcesTest() {
        loader.setMap(Collections.singletonMap("key", "value"));
        assertFalse(loader.getMap().isEmpty());

        loader.releaseResources();

        assertTrue(loader.getMap().isEmpty());
    }
}
