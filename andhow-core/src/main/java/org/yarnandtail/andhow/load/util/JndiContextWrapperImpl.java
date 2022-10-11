package org.yarnandtail.andhow.load.util;

import org.yarnandtail.andhow.api.JndiContextWrapper;

import javax.naming.Context;

/**
 * Simple JndiContextWrapper implementation.
 */
public class JndiContextWrapperImpl implements JndiContextWrapper {
	public final Context _context;
	public final Exception _exception;

	public JndiContextWrapperImpl(final Context context) {
		_context = context;
		_exception = null;
	}

	public JndiContextWrapperImpl(final Exception exception) {
		_context = null;
		_exception = exception;
	}

	public JndiContextWrapperImpl() {
		_context = null;
		_exception = null;
	}

	@Override
	public Context getContext() {
		return _context;
	}

	@Override
	public Exception getException() {
		return _exception;
	}
}
