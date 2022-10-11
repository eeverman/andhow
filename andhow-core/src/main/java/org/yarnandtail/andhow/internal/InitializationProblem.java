package org.yarnandtail.andhow.internal;

import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.AndHowInit;
import org.yarnandtail.andhow.api.Problem;
import org.yarnandtail.andhow.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * AndHow initialization problems resulting from illegal method calls due to
 * odd/unsupported user code to configure or initialize AndHow, or classpath setup.
 */
public abstract class InitializationProblem implements Problem {
	private static final String SEE_USER_GUIDE =
			"  See user guide for configuration docs & examples: https://www.andhowconfig.org/user-guide";

	@Override
	public String getFullMessage() {
		return getProblemContext() + ": " + getProblemDescription();
	}

	@Override
	public String getProblemContext() {
		return "AndHow was unable to initialize";
	}

	public static class InitiationLoop extends InitializationProblem {
		AndHow.Initialization originalInit;
		AndHow.Initialization secondInit;

		public InitiationLoop(AndHow.Initialization originalInit, AndHow.Initialization secondInit) {
			this.originalInit = originalInit;
			this.secondInit = secondInit;
		}

		public AndHow.Initialization getOriginalInit() {
			return originalInit;
		}

		public AndHow.Initialization getSecondInit() {
			return secondInit;
		}


		@Override
		public String getProblemDescription() {

			return "An initialization loop was detected (AndHow.initialize was called during execution of AndHow.initialize).  "
					+ "Likely causes are calls [Property].value() or AndHow.instance() in an unexpected place, such as: " + System.lineSeparator()
					+ "- Static initiation blocks or static variable initiation values, e.g., 'static int MyVar = [Some AndHow Prop].getValue()'" + System.lineSeparator()
					+ "- An AndHow Property that refers to the value of another AndHow property in its construction" + System.lineSeparator()
					+ "- An AndHowInit implementation that calls one of these methods in its getConfiguration method" + System.lineSeparator()
					+ "- A custom AndHowConfiguration instance using one of these methods (likely an exotic test setup)"
					+ "::The first line in the stack trace following this error referring to your application code is likely causing the initiation loop::";
		}
	}

	public static class SetConfigCalledDuringInitialization extends InitializationProblem {

		@Override
		public String getProblemDescription() {

			return "AndHow.setConfig() was called during initialization, which is not allowed. "
					+ "This is most likely due to a custom AndHowConfiguration instance that "
					+ "calls AndHow.setConfig() in one of its methods. " + SEE_USER_GUIDE;
		}
	}

	public static class SetConfigCalledDuringFindConfig extends InitializationProblem {

		@Override
		public String getProblemDescription() {

			return "AndHow.setConfig() was called during the invocation of AndHow.findConfig(), " +
					"which is not allowed.  Likely caused by setConfig() called inside of AndHowInit.getConfiguration(). " +
					SEE_USER_GUIDE;
		}
	}

	public static class IllegalMethodCalledAfterInitialization extends InitializationProblem {
		private final String _methodName;

		public IllegalMethodCalledAfterInitialization(String methodName) {
			_methodName = methodName;
		}

		public String getMethodName() {
			return _methodName;
		}

		@Override
		public String getProblemDescription() {
			return "AndHow." + _methodName + "() was called after AndHow initialization was complete, " +
					"which is not allowed." +	SEE_USER_GUIDE;
		}
	}

	/**
	 * This is really a configuration of AndHow problem - it happens while AndHow tries
	 * to find its configuration.  So its not really an initialization problem, but this
	 * seems to be the best place to put this type of problem.
	 */
	public static class TooManyAndHowInitInstances extends InitializationProblem {
		List<String> names = new ArrayList();

		public TooManyAndHowInitInstances(List<? extends AndHowInit> instances) {
			for (AndHowInit init : instances) {
				names.add(init.getClass().getCanonicalName());
			}
		}

		public List<String> getInstanceNames() {
			return names;
		}

		@Override
		public String getProblemDescription() {
			String joined = String.join(", ", names);

			return TextUtil.format(
					"There can be only be one instance each of {} and AndHowTestInit on "
						+ "the classpath, but multiple were found: {}",
					AndHowInit.class.getCanonicalName(), joined);
		}
	}
}
