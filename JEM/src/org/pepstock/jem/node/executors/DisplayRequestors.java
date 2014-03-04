/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.node.executors;

import org.pepstock.jem.node.Main;


/**
 * Returns contention list of jobs if JEM node implements it.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 1.2	
 *
 */
public class DisplayRequestors extends DefaultExecutor<String> {

	private static final long serialVersionUID = 1L;
	
	private String resourceKey = null;

	/**
	 * Constructs without any resources key.
	 */
	public DisplayRequestors() {
		this(null);
	}

	/**
	 * Constructs with resource key which is used to get contention list
	 * @param resourceKey reosurce key is used to get contention list
	 */
	public DisplayRequestors(String resourceKey) {
		this.resourceKey = resourceKey;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#execute()
	 */
	@Override
	public String execute() throws ExecutorException {
		if (resourceKey != null){
			return Main.getNode().displayRequestors(resourceKey);
		} else {
			return Main.getNode().displayRequestors();
		}
	}
}