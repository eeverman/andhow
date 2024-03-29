package org.yarnandtail.andhow.sample;

import org.yarnandtail.andhow.internal.PropertyConfigurationInternal;
import org.yarnandtail.andhow.AndHow;
import org.yarnandtail.andhow.api.*;
import org.yarnandtail.andhow.util.TextUtil;

/**
 *
 * @author ericeverman
 */
public class JndiLoaderSamplePrinter extends BaseSamplePrinter implements SamplePrinter {
	protected PrintFormat format;

	public JndiLoaderSamplePrinter() {
		format = new JndiFileFormat();
	}

	@Override
	public PrintFormat getFormat() {
		return format;
	}

	@Override
	public TextBlock getSampleFileStart() {
		TextBlock tb = new TextBlock(false, false);
		tb.addLine("<Context>");
		return tb;
	}

	@Override
	public TextBlock getSampleStartComment(PropertyConfigurationInternal definition) {
		TextBlock tb = new TextBlock(true, true);
		tb.addHR();
		tb.addLine("Sample JNDI config file generated by " +  AndHow.ANDHOW_NAME);
		tb.addLine(AndHow.ANDHOW_TAG_LINE + "  -  " + AndHow.ANDHOW_URL);
		tb.addLine("This sample uses Tomcat syntax. JNDI configuration for other servers will be similar.");
		tb.addHR();
		return tb;
	}

	@Override
	public String getInAliaseString(PropertyConfigurationInternal definition, EffectiveName name) {
		return definition.getNamingStrategy().getUriName(name.getActualName());
	}


	@Override
	public TextBlock getActualProperty(PropertyConfigurationInternal definition,
                                       GroupProxy group, Property prop) throws Exception {

		TextBlock tb = new TextBlock(false, true);

		String propCanonName = definition.getNamingStrategy().getUriName(group.getCanonicalName(prop));
		String type = prop.getValueType().getDestinationType().getCanonicalName();

		tb.addLine(
				TextUtil.format("<Environment name=\"{}\" value=\"{}\" type=\"{}\" override=\"false\"/>",
					propCanonName,
					"",
					type)
		);

		return tb;
	}

	@Override
	public TextBlock getSampleFileEnd() {
		TextBlock tb = new TextBlock(false, false);
		tb.addLine("</Context>");
		return tb;
	}

	@Override
	public String getSampleFileExtension() {
		return "xml";
	}
}
