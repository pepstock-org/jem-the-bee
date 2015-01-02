/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Andrea "Stock" Stocchero
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pepstock.jem.node.executors.jobs;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator used to sort the files tree of global file system, order by last
 * update time.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class OutputTreeComparator implements Comparator<File>, Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(File fileFrom, File fileTo) {
		int diff = 0;
		// gets the last update time of both files
		long compareFrom = getLastModified(fileFrom);
		long compareTo = getLastModified(fileTo);

		// is discending order
		if (compareFrom > compareTo) {
			diff = 1;
		} else if (compareFrom < compareTo) {
			diff = -1;
		}
		return diff;
	}

	/**
	 * Gets the last update time of the file. If it's a directory
	 * uses the first file of the directory as last update time
	 * @param file file to get last modified time
	 * @return last update time
	 */
	private long getLastModified(File file) {
		// gets the last modified
		long compare = file.lastModified();
		if (file.isDirectory()) {
			// if is directory
			// gets all files of folder
			File[] subFileFrom = file.listFiles();
			// and gets the last modified time 
			// of first file
			if (subFileFrom != null && subFileFrom.length > 0) {
				compare = subFileFrom[0].lastModified();
			}
		}
		return compare;
	}
}
