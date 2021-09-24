package org.yarnandtail.andhow.load.std;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StdMainStringArgsLoaderTest {

    private StdMainStringArgsLoader loader;

    @BeforeEach
    public void initLoader() {
        this.loader = new StdMainStringArgsLoader();
    }

    @Test
    public void getSpecificLoadDescriptionTest() {
        String expectedDescription = "main(String[] args)";

        assertEquals(expectedDescription, loader.getSpecificLoadDescription());
    }
}
