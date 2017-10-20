package org.yarnandtail.andhow.compile;

import com.sun.source.tree.*;
import com.sun.source.util.TreePathScanner;

/**
 * A compilation tree scanner used during compile time to scan for AndHow Property
 * construction being assigned to a variable.
 * 
 * The intended use is to create a new instance of this class when a variable
 * of type <code>org.yarnandtail.andhow.api.Property</code> is found.  The current
 * path is passed into this classes scan() method along with a PropertyMarker.
 * When the scan is complete, the PropertyMarker is returned, which will indicate
 * if the Property assigned to the variable is newly constructed or just a
 * reference to an already constructed Property.
 * 
 * @author ericeverman
 */
public class PropertyVariableTreeScanner extends TreePathScanner<PropertyMarker, PropertyMarker> {

	@Override
	public PropertyMarker visitNewClass(NewClassTree nct, PropertyMarker p) {
		p.markDirectConstruct();
		return p;
	}
	

	/**
	 * Called for the actual invocation of a specific method.
	 * 
	 * This is called by visitMethodInvocation(), which is actually called at
	 * a higher level with metadata and arguments, before we get down to the
	 * level of a specific method.  At that higher level, there doesn't seem
	 * to be a way to reliably pick out the name of the method being invoked.

	 * @param mst
	 * @param p
	 * @return 
	 */
	@Override
	public PropertyMarker visitMemberSelect(MemberSelectTree mst, PropertyMarker p) {

		if (mst.getIdentifier().contentEquals("builder")) {
			p.markBuilder();
		} else if (mst.getIdentifier().contentEquals("build")) {
			p.markBuild();
		}
		
		if (p.isNewProperty()) {
			return p;	//No need to scan any deeper
		} else {
			return super.visitMemberSelect(mst, p);
		}
		
	}
	

	
}
