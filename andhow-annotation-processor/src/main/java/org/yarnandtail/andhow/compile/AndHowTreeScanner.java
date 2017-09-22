/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yarnandtail.andhow.compile;

import com.sun.source.tree.*;
import com.sun.source.util.TreePathScanner;

/**
 *
 * @author ericevermanpersonal
 */
public class AndHowTreeScanner extends TreePathScanner<PropertyMarker, PropertyMarker> {

	@Override
	public PropertyMarker visitClass(ClassTree ct, PropertyMarker p) {
		//System.out.println("Tree: visitClass: " + ct.getSimpleName().toString());
		return super.visitClass(ct, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public PropertyMarker visitNewClass(NewClassTree nct, PropertyMarker p) {
		//System.out.println("Tree: visitNewClass");
		
		p.markDirectConstruct();
		return p;
		//No need to scan any further
		//return super.visitNewClass(nct, p);
	}
	
	@Override
	public PropertyMarker visitVariable(VariableTree vt, PropertyMarker p) {
		

		System.out.println("Tree: visitVariable: " + vt.getName() + " Type: " + vt.getType().toString());

		return super.visitVariable(vt, p); //To change body of generated methods, choose Tools | Templates.
	}

	/**
	 * A method invocation that includes the entire expression and arguments.
	 * 
	 * It doesn't seem possible to reliably pick out the name of the method
	 * invoked because there are lots of pieces to an invocation.  Instead,
	 * visit on to visitMemberSelect, which has an identifier of the method.
	 * @param mit
	 * @param p
	 * @return 
	 */
	@Override
	public PropertyMarker visitMethodInvocation(MethodInvocationTree mit, PropertyMarker p) {
		System.out.println("Tree: visitMethodInvocation: " + mit.toString());
		return super.visitMethodInvocation(mit, p); //To change body of generated methods, choose Tools | Templates.
	}

	/**
	 * Invocation of a specific method
	 * @param mst
	 * @param p
	 * @return 
	 */
	@Override
	public PropertyMarker visitMemberSelect(MemberSelectTree mst, PropertyMarker p) {
		System.out.println("Tree: visitMemberSelect: " + mst.getIdentifier().toString());
		
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
