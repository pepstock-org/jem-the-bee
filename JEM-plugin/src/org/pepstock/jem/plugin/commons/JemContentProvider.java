/*******************************************************************************
 * Copyright (C) 2012-2014 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Enrico - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.commons;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Represents a common content provider, used for table viewers
 * 
 * @author Andrea "Stock" Stocchero
 * @param <T>
 * 
 */
public class JemContentProvider<T> implements IStructuredContentProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object o1, Object o2) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	@Override
	public Object[] getElements(Object parent) {
		// checks parent
		if (parent != null) {
			@SuppressWarnings("unchecked")
			Collection<T> items = (Collection<T>) parent;
			return items.toArray();
		}
		return new Object[0];
	}
}
