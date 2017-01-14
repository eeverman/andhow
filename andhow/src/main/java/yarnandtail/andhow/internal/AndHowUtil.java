package yarnandtail.andhow.internal;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.AppFatalException;
import yarnandtail.andhow.ConstructionProblem;
import yarnandtail.andhow.Loader;
import yarnandtail.andhow.LoaderProblem;
import yarnandtail.andhow.LoaderValues;
import yarnandtail.andhow.PropertyValue;
import yarnandtail.andhow.ValueProblem;
import yarnandtail.andhow.RequirementProblem;
import yarnandtail.andhow.ValueMapWithContext;
import yarnandtail.andhow.PropertyGroup;
import yarnandtail.andhow.NamingStrategy;

/**
 * Utilities used by AndHow during initial construction.
 * @author eeverman
 */
public class AndHowUtil {
	

	/**
	 * Build a fully populated RuntimeDefinition from the Properties contained in
	 * the passed Groups, using the NamingStrategy to generate names for each.
	 * 
	 * @param groups The PropertyGroups from which to find Properties.  May be null
	 * @param naming  A naming strategy to use when reading the properties during loading
	 * @return A fully configured instance
	 */
	public static ConstructionDefinitionMutable 
		doRegisterProperties(List<Class<? extends PropertyGroup>> groups, List<Loader> loaders, NamingStrategy naming) {

		ConstructionDefinitionMutable appDef = new ConstructionDefinitionMutable();
		
		if (loaders != null) {
			for (Loader loader : loaders) {
				Class<? extends PropertyGroup> group = loader.getLoaderConfig();
				if (group != null) {
					
					doRegisterGroup(appDef, group, naming);
					
				}
			}
		}
		
		//null groups is possible - used in testing and possibly early uses before params are created
		if (groups != null) {
			for (Class<? extends PropertyGroup> group : groups) {

				doRegisterGroup(appDef, group, naming);
				
			}
		}
		
		return appDef;

	}
		
	protected static void doRegisterGroup(ConstructionDefinitionMutable appDef,
			Class<? extends PropertyGroup> group, NamingStrategy naming) {

		try {
			List<PropertyGroup.NameAndProperty> nameAndProperties = PropertyGroup.getProperties(group);
			
			for (PropertyGroup.NameAndProperty nameAndProp : nameAndProperties) {
				NamingStrategy.Naming names = naming.buildNamesFromCanonical(nameAndProp.property, group, nameAndProp.canonName);
				appDef.addProperty(group, nameAndProp.property, names);
			}
			
		} catch (Exception ex) {
			ConstructionProblem.SecurityException se = new ConstructionProblem.SecurityException(
				ex, group);
			appDef.addConstructionProblem(se);
		}

	}
		
	public static void printExceptions(List<? extends Exception> exceptions, PrintStream out) {
		for (Exception ne : exceptions) {
			out.println(ne.getMessage());
		}
	}
	
	public static AppFatalException buildFatalException(List<LoaderProblem> loaderProblems, 
			List<RequirementProblem> requirementsProblems, ValueMapWithContext loadedValues) {
		
		ArrayList<ValueProblem> pvps = new ArrayList();
		
		//build list of ValueProblems
		
		if (loadedValues != null) {
			for (LoaderValues lvs : loadedValues.getAllLoaderValues()) {
				for (PropertyValue pv : lvs.getValues()) {
					pvps.addAll(pv.getIssues());
				}
			}
		}
		
		return new AppFatalException(
				"Unable to complete application configuration due to problems. " +
				"See the System.err out or the log files for complete details.",
				loaderProblems, pvps, requirementsProblems);
		
	}
}
