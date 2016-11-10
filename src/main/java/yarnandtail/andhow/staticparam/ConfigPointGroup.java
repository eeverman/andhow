package yarnandtail.andhow.staticparam;

import java.util.ArrayList;

/**
 *
 * @author eeverman
 */
public interface ConfigPointGroup {

	public static <T extends ConfigPoint> T add(T point) {
		ConfigPointBase.add(point);
		return point;
	}
}
