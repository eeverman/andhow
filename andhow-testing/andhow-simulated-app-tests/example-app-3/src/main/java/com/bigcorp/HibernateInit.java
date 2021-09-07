package com.bigcorp;

import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.api.Exporter;
import org.yarnandtail.andhow.export.ExportCollector;
import org.yarnandtail.andhow.export.ManualExportAllowed;
import org.yarnandtail.andhow.export.ManualExportNotAllowed;
import org.yarnandtail.andhow.property.IntProp;
import org.yarnandtail.andhow.property.StrProp;

import java.util.Properties;

/**
 * A hypothetical class that configures the ORM framework Hibernate using AndHow Properties.
 * <p>
 * Of course, this isn't a complete application, but it shows how this might be done.
 */
@ManualExportAllowed(useCanonicalName = Exporter.EXPORT_CANONICAL_NAME.NEVER)
public class HibernateInit {

	//All the config Properties for this class
	// - Its a best practice to group AndHow Properties into an interface.
	private interface Config {
		StrProp DRIVER = StrProp.builder().aliasInAndOut("hibernate.connection.driver_class")
				.mustBeNonNull().build();
		StrProp CONN_URL = StrProp.builder().aliasInAndOut("hibernate.connection.url")
				.mustBeNonNull().mustStartWith("jdbc:").build();
		StrProp USER = StrProp.builder().aliasInAndOut("hibernate.connection.username")
				.mustBeNonNull().build();
		StrProp PWD = StrProp.builder().aliasInAndOut("hibernate.connection.password")
				.mustBeNonNull().build();
		StrProp ISOLATION = StrProp.builder().aliasInAndOut("hibernate.connection.isolation")
				.mustEqual("REPEATABLE_READ", "TRANSACTION_REPEATABLE_READ")
				.defaultValue("REPEATABLE_READ").build();
		IntProp INIT_POOL_SIZE = IntProp.builder().aliasInAndOut("hibernate.connection.initial_pool_size")
				.defaultValue(1).mustBeLessThan(20).build();
		IntProp POOL_SIZE = IntProp.builder().aliasInAndOut("hibernate.connection.pool_siz")
				.defaultValue(20).mustBeLessThan(200).build();
	}

	@ManualExportNotAllowed
	private interface OTHER_CONFIG {
		StrProp PWD = StrProp.builder().desc("Some other secret pwd that not to be exported").build();
	}

	/**
	 * In a real app, this wouldn't be a public method since it exposes db connection info,
	 * but it makes testing easy for this example.
	 *
	 * @return
	 * @throws IllegalAccessException
	 */
	public Properties getHibernateProperties() throws IllegalAccessException {
		return AndHow.instance().export(HibernateInit.class)
				.collect(ExportCollector.stringProperties(""));
	}

	public HibernateConfiguration pretendToinitializeHibernate() throws IllegalAccessException {
		Properties props = getHibernateProperties();

		HibernateConfiguration hibConfig = new HibernateConfiguration();
		hibConfig.setProperties(props);

		return hibConfig;
	}


	/**
	 * Fake class that pretends to be a Hibernate Configuration object.
	 * See the real thing here:
	 * https://docs.jboss.org/hibernate/orm/3.3/reference/en-US/html/session-configuration.html
	 */
	public static class HibernateConfiguration {
		Properties props;

		void setProperties(Properties props) {
			this.props = props;
		}

		Properties getProperties() {
			return props;
		}
	}


	/**
	 * A main method to run this app as it might be in a real environment.
	 * <p>
	 * In this main method the {@code AndHow.findConfig()} method is used to append the cmd line
	 * arguments to the AndHow configuration.  This allows Property values to be configured from
	 * these arguments, in addition to all the other way they could be configured.
	 * <p>>
	 * Run it from an IDE , or via command line.
	 * <p>
	 * To run from command line, first use Maven to create a runnable jar.
	 * Here are the commands, executed from the root of the AndHow project, to build and run the jar:<br>
	 * <pre>{@code
	 * > mvn clean package -DskipTests -Dmaven.javadoc.skip=true
	 * > java -jar andhow-testing/andhow-simulated-app-tests/example-app-3/target/app.jar
	 * }</pre>
	 * The output of this command will match the config in {@code andhow.properties}:
	 * <pre>{@code
	 * Service url: https://default.bigcorp.com:80/validate
	 * }</pre>
	 *
	 * <pre>{@code
	 * > java -Dcom.bigcorp.Calculator.CALC_MODE=FLOAT -jar andhow-testing/andhow-simulated-app-tests/example-app-2/target/app.jar 1.23 4.56
	 * }</pre>
	 * will result in {@code Result is 0.26973686 (Float)}
	 *
	 * @param args Two arguments that are parsable to numbers.
	 */
	public static void main(String[] args) throws Exception {

		//Find the AndHow Configuration and add to it the commandline args so they can be used for
		//configuration as well.
		AndHow.findConfig().setCmdLineArgs(args);	//Have to call build here or it doesn't work!!

		HibernateInit hibernateInit = new HibernateInit();


		HibernateConfiguration hc = hibernateInit.pretendToinitializeHibernate();

		hc.getProperties().entrySet().stream().forEach(
				e -> System.out.println(String.format("name: %1s value: %2s", e.getKey(), e.getValue()))
		);
	}

}
