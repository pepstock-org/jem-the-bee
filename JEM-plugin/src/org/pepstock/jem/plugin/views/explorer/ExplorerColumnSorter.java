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
package org.pepstock.jem.plugin.views.explorer;

import org.pepstock.jem.GfsFile;
import org.pepstock.jem.plugin.commons.JemColumnSorter;

/**
 * It provides column sorter for a table viewer for explorer.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class ExplorerColumnSorter extends JemColumnSorter<GfsFile> {

    private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(GfsFile o1, GfsFile o2) {
		int diff = 0;
		switch (getIndex()) {
		case 0:
			// sorts by file 
			diff = getComparedName(o1, o2);
			break;
		case 1:
			// size of file
			diff = getComparedSize(o1, o2);
			break;
		case 2:
			// last modified of file
			diff = getComparedLastModified(o1, o2);
			break;
		default:
			// default sorts by file 
			diff = getComparedName(o1, o2);
			break;
		}
		// checks if Ascending otherwise negative
		return isAscending() ? diff : -diff;
	}
	
	/**
	 * Compares the name or directory name of GFS file
	 * @param o1 first job to check
	 * @param o2 second job to check
	 * @return the value <code>0</code> if the arguments are equals; a value less than <code>0</code> if first argument
     *          is lexicographically less than the second argument; and a
     *          value greater than <code>0</code> if first argument is
     *          lexicographically greater than the second argument.
	 */
	private int getComparedName(GfsFile o1, GfsFile o2){
		// sorts by file 
		int diff = 0;
		// it bases on type of file. Directory always before of files
		if (o1.isDirectory()) {
			if (o2.isDirectory()) {
				diff = o1.getName().compareTo(o2.getName());
			} else {
				diff = -1;
			}
		} else {
			if (o2.isDirectory()) {
				diff = 1;
			} else {
				diff = o1.getName().compareTo(o2.getName());
			}
		}
		return diff;
	}
	
	/**
	 * Compares the name or directory name of GFS file
	 * @param o1 first job to check
	 * @param o2 second job to check
	 * @return the value <code>0</code> if the arguments are equals; a value less than <code>0</code> if first argument
     *          is lexicographically less than the second argument; and a
     *          value greater than <code>0</code> if first argument is
     *          lexicographically greater than the second argument.
	 */	
	private int getComparedSize(GfsFile o1, GfsFile o2){
		// size of file
		int diff = 0;
		if (o1.getLength() > o2.getLength()) {
			diff = 1;
		} else if (o1.getLength() < o2.getLength()) {
			diff = -1;
		}
		return diff;
	}
	
	/**
	 * Compares the name or directory name of GFS file
	 * @param o1 first job to check
	 * @param o2 second job to check
	 * @return the value <code>0</code> if the arguments are equals; a value less than <code>0</code> if first argument
     *          is lexicographically less than the second argument; and a
     *          value greater than <code>0</code> if first argument is
     *          lexicographically greater than the second argument.
	 */
	private int getComparedLastModified(GfsFile o1, GfsFile o2){
		// last modified of file
		int diff = 0;
		if (o1.getLastModified() > o2.getLastModified()) {
			diff = 1;
		} else if (o1.getLastModified() < o2.getLastModified()) {
			diff = -1;
		}
		return diff;
	}
}
