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
package org.pepstock.jem.node.persistence.database;

import org.pepstock.jem.Job;

/**
 * Manages all SQL statements towards the database to persist the job in INPUT,
 * OUTPUT and ROUTING queues.<br>
 * Actions are the same for all queues (and then table because there is a table
 * for queue)<br>
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class JobDBManager extends AbstractDBManager<Job>{

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.AbstractDBManager#getKey(java.lang.Object)
	 */
	@Override
	public String getKey(Job item) {
		return item.getId();
	}

}