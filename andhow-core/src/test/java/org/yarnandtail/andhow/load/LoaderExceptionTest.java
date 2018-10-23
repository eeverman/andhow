package org.yarnandtail.andhow.load;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class LoaderExceptionTest {

    @Test
    public void testGetMessageShowsSourceDescription() {
        String sourceDescription = "someSourceDescription";
        LoaderException loaderException = new LoaderException(null, null, sourceDescription);

        String message = loaderException.getMessage();

        assertThat(message, containsString("Error reading source: " + sourceDescription));
    }

    @Test
    public void testGetMessageShowsBaseExceptionMessage() {
        Exception exception = new Exception("SomeExceptionMessage");
        LoaderException loaderException = new LoaderException(exception, null, "");

        String message = loaderException.getMessage();

        assertThat(message, is("Error reading source:   Base error message: " + exception.toString()));
    }

    @Test
    public void testGetMessageShowsSourceDescriptionAndBaseExceptionMessage() {
        Exception exception = new Exception("SomeExceptionMessage");
        String sourceDescription = "anotherSourceDescription";
        LoaderException loaderException = new LoaderException(exception, null, sourceDescription);

        String message = loaderException.getMessage();

        assertThat(message, is("Error reading source: " + sourceDescription + "  Base error message: " + exception.toString()));
    }

    @Test
    public void testGetMessageShowsBaseExceptionWhenSourceDescriptionIsNull() {
        Exception exception = new Exception("SomeExceptionMessage");
        LoaderException loaderException = new LoaderException(exception, null, null);

        String message = loaderException.getMessage();

        assertThat(message, is(exception.toString()));
    }

    @Test
    public void testGetCauseReturnsBaseException() {
        Exception exception = new Exception("SomeExceptionMessage");
        LoaderException loaderException = new LoaderException(exception, null, null);

        Exception cause = loaderException.getCause();

        assertThat(cause, is(exception));
    }

}
