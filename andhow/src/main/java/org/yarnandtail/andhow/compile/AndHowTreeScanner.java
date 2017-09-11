/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.yarnandtail.andhow.compile;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

/**
 *
 * @author ericevermanpersonal
 */
public class AndHowTreeScanner extends TreeScanner {

	@Override
	public Object visitIdentifier(IdentifierTree it, Object p) {
		System.out.println("Tree: visitIdentifier: " + it.getName());
		return super.visitIdentifier(it, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Object visitCompoundAssignment(CompoundAssignmentTree cat, Object p) {
		System.out.println("Tree: visitCompoundAssignment");
		return super.visitCompoundAssignment(cat, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Object visitAssignment(AssignmentTree at, Object p) {
		System.out.println("Tree: visitAssignment");
		return super.visitAssignment(at, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Object visitExpressionStatement(ExpressionStatementTree est, Object p) {
		System.out.println("Tree: visitExpressionStatement");
		return super.visitExpressionStatement(est, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Object visitBlock(BlockTree bt, Object p) {
		System.out.println("Tree: visitBlock");
		return super.visitBlock(bt, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Object visitVariable(VariableTree vt, Object p) {
		System.out.println("Tree: visitVariable: " + vt.getName());
		return super.visitVariable(vt, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Object visitMethod(MethodTree mt, Object p) {
		System.out.println("Tree: visitMethod: " + mt.getName());
		return super.visitMethod(mt, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Object visitClass(ClassTree ct, Object p) {
		System.out.println("Tree: visitClass: " + ct.getSimpleName().toString());
		return super.visitClass(ct, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Object visitMemberReference(MemberReferenceTree mrt, Object p) {
		System.out.println("Tree: visitMemberReference: " + mrt.getName().toString());
		return super.visitMemberReference(mrt, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Object visitNewClass(NewClassTree nct, Object p) {
		System.out.println("Tree: visitNewClass");
		return super.visitNewClass(nct, p); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Object visitMethodInvocation(MethodInvocationTree mit, Object p) {
		System.out.println("Tree: visitMethodInvocation: " + mit.toString());
		return super.visitMethodInvocation(mit, p); //To change body of generated methods, choose Tools | Templates.
	}
	
	
	
}
