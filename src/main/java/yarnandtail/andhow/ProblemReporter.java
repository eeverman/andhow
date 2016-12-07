package yarnandtail.andhow;

import java.io.PrintStream;
import java.util.List;
import yarnandtail.andhow.AppConfigStructuredValues;
import yarnandtail.andhow.AppFatalException;
import yarnandtail.andhow.ConstructionProblem;
import yarnandtail.andhow.PointValueProblem;
import yarnandtail.andhow.RequirementProblem;
import yarnandtail.andhow.TextUtil;
import yarnandtail.andhow.appconfig.AppConfigDefinition;

/**
 *
 * @author ericeverman
 */
public class ProblemReporter {
	
	protected AppFatalException fatalException;
	AppConfigDefinition appDef;
			
	public ProblemReporter(AppFatalException fatalException, AppConfigDefinition appDef) {
		this.fatalException = fatalException;
		this.appDef = appDef;
	}
	
	public void print(PrintStream out) {
		
		try {
			TextUtil.printlnHr(out);
			TextUtil.printlnHr(out);
			out.println("Problem report from AndHow!  The App Configuration framework.");
			out.println("https://github.com/eeverman/andhow");
			TextUtil.printlnHr(out);
			printConfigurationProblems(out, fatalException.getConstructionProblems(), appDef);
			printPointValueProblems(out, fatalException.getPointValueProblems(), appDef);
			printRequirementProblems(out, fatalException.getRequirementsProblems(), appDef);
			TextUtil.printlnHr(out);
			out.println("  ~~ Thanks for playing ~~");
			TextUtil.printlnHr(out);
			TextUtil.printlnHr(out);
		} catch (Exception e) {
			out.println("Uh oh.  There was an Exception while printing the list of problems with the app config.");
			out.println("Error Type: " + e.getClass().getCanonicalName() + ",  Error message: " + e.getMessage());
			e.printStackTrace(out);
		}
	}
	
	public void printConfigurationProblems(PrintStream out, List<ConstructionProblem> probs, AppConfigDefinition appDef) {
		if (probs.isEmpty()) {
			
			TextUtil.printlnHr(out);
			out.println("CONSTRUCTION PROBLEMS:  Excellent!  There are no construction problems");
			TextUtil.printlnHr(out);
			
		} else {
			
			TextUtil.printlnHr(out);
			out.println("CONSTRUCTION PROBLEMS  ~Foundational problems with the App Configuration.");
			out.println("~The AppConfig may have been built in an inconsistent way, such as default values that violate validation rules.");
			out.println("~When there are construction problems, no attempt is made to load configuration values.");
			out.println();
			out.println("Defailed list of Construction Problems:");
			
			for (ConstructionProblem p : probs) {
				out.println(p.getMessage());
			}
			
			TextUtil.printlnHr(out);
		}
	}
	
	public void printPointValueProblems(PrintStream out, List<PointValueProblem> probs, AppConfigDefinition appDef) {
		if (probs.isEmpty()) {
			
			TextUtil.printlnHr(out);
			out.println("VALUE PROBLEMS:  Nice! There are no value problems.");
			TextUtil.printlnHr(out);
			
		} else {
			
			TextUtil.printlnHr(out);
			
			out.println("VALUE PROBLEMS  ~Values that violate validation rules, "
					+ "or source values that cannot be converted to their destination type.");
			out.println();
			out.println("Defailed list of Value Problems:");
			
			for (PointValueProblem p : probs) {
				TextUtil.println(out, "ConfigPoint {} loaded from {}: {}", 
						appDef.getCanonicalName(p.getPoint()), 
						p.getLoader().getSpecificLoadDescription(),
						p.getMessageWithinFullContext());
			}
			
			TextUtil.printlnHr(out);
		}
	}
	
	public void printRequirementProblems(PrintStream out, List<RequirementProblem> probs, AppConfigDefinition appDef) {
		if (probs.isEmpty()) {
			
			TextUtil.printlnHr(out);
			out.println("REQUIRMENT PROBLEMS:  Well done!  There are no requirment problems");
			TextUtil.printlnHr(out);
			
		} else {
			
			TextUtil.printlnHr(out);
			out.println("REQUIRMENT PROBLEMS  ~When a required configuration point is not provided");
			out.println("~Note:  Prior issues that prevente individual values from loading may also result in requirements problems.");
			out.println();
			out.println("Defailed list of Requirements Problems:");
			
			for (RequirementProblem p : probs) {
				if (p.getPoint() != null) {
					TextUtil.println(out, "ConfigPoint {}: {}", 
						appDef.getCanonicalName(p.getPoint()), 
						p.getMessageWithinFullContext());
				} else {
					TextUtil.println(out, "ConfigPointGroup {}: {}", 
						p.getGroup().getCanonicalName(), 
						p.getMessageWithinFullContext());
				}

			}
			
			TextUtil.printlnHr(out);
		}
	}
}
