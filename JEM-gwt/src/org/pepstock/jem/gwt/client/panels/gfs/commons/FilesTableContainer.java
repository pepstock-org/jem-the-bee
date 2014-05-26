/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.client.panels.gfs.commons;



import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.panels.components.TableContainer;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Nodes table container for nodes
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class FilesTableContainer extends VerticalPanel {

	private TableContainer<GfsFile> nodes = null;
	
	/**
	 * Creates the UI by the argument (the table)
	 *  
	 * @param nodes table of nodes 
	 */
	public FilesTableContainer(FilesTable nodes) {
		this.nodes = new TableContainer<GfsFile>(nodes);
		setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		add(this.nodes);
		setCellHeight(this.nodes, Sizes.HUNDRED_PERCENT);
	}

	/**
	 * @return the jobs
	 */
	public FilesTable getFilesTable() {
		return (FilesTable) nodes.getUnderlyingTable();
	}

}