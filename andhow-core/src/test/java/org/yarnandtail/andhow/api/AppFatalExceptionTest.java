package org.yarnandtail.andhow.api;

import static org.junit.Assert.*;
import org.junit.Test;

public class AppFatalExceptionTest {

    @Test
    public void testConstructorFromString() {
        AppFatalException instance = new AppFatalException("test");
        assertEquals(instance.getMessage(), "test");
        assertNotNull(instance.getProblems());
    }

    @Test
    public void testConstructorFromStringAndProblemList() {
        ProblemList<Problem> problems = new ProblemList<Problem>();
        AppFatalException instance = new AppFatalException("test", problems);
        assertEquals(instance.getMessage(), "test");
        assertNotNull(instance.getProblems());

        problems = null;
        instance = new AppFatalException("test", problems);
        assertEquals(instance.getMessage(), "test");
        assertNotNull(instance.getProblems());
    }

    @Test
    public void testConstructorFromStringAndProblem() {
        Problem problem = new TestProblem();
        AppFatalException instance = new AppFatalException("test", problem);
        assertEquals(instance.getMessage(), "test");
        assertNotNull(instance.getProblems());
        assertEquals(instance.getProblems().get(0).getFullMessage(), "TEST MESSAGE");

        problem = null;
        instance = new AppFatalException("test", problem);
        assertEquals(instance.getMessage(), "test");
        assertNotNull(instance.getProblems());
    }
<<<<<<< HEAD
=======
	
    @Test
    public void testConstructorFromProblem() {
        Problem problem = new TestProblem();
        AppFatalException instance = new AppFatalException(problem);
        assertEquals(instance.getMessage(), problem.getFullMessage());
        assertNotNull(instance.getProblems());
        assertEquals(instance.getProblems().get(0), problem);

        problem = null;
        instance = new AppFatalException(problem);
        assertEquals(instance.getMessage(), "Unknown AndHow fatal exception");
		assertNotNull(instance.getProblems());
		assertEquals(0, instance.getProblems().size());
    }
>>>>>>> 50dbdceb7259e6c7bb3d01921c8c5479bb399357

    @Test
    public void testSampleDirectory() {
        AppFatalException instance = new AppFatalException("test");
        instance.setSampleDirectory("test/path");
        assertEquals(instance.getSampleDirectory(), "test/path");
    }

    class TestProblem implements Problem {

        @Override
        public String getFullMessage() {
            return "TEST MESSAGE";
        }

        @Override
        public String getProblemContext() {
            return "TEST PROBLEM CONTEXT";
        }

        @Override
        public String getProblemDescription() {
            return "TEST PROBLEM DESCRIPTION";
        }
    }
}