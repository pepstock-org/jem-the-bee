/*******************************************************************************
 * Copyright (C) 2012-2015 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
*     Enrico Frigo - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.commons;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Common column sorter for a table viewer.
 * It provides the index of column and how to sort.
 * 
 * @author Andrea "Stock" Stocchero
 * @param <T>
 *
 */
public abstract class JemColumnSorter<T> extends ViewerSorter implements Comparator<T>, Serializable {

    private static final long serialVersionUID = 1L;

	private boolean ascending = true;
	
	private int index = 0;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
    @SuppressWarnings("unchecked")
    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
		T o1 = (T) e1;
		T o2 = (T) e2;
		return compare(o1, o2);
	}

	/**
	 * Returns <code>true</code> if sort must be ascending
	 * 
	 * @return the ascending <code>true</code> if sort must be ascendingfs
	 */
	public boolean isAscending() {
		return ascending;
	}

	/**
	 * Sets <code>true</code> if sort must be ascending
	 * @param ascending the ascending to set
	 */
	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	/**
	 * Returns column index to be used for sorting
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets column index to be used for sorting
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
}
