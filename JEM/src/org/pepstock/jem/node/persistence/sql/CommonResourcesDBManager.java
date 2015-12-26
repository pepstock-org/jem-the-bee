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
package org.pepstock.jem.node.persistence.sql;

import org.pepstock.jem.node.hazelcast.Queues;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.XmlUtil;

/**
 * Manages all SQL statements towards the database to persist the commons
 * resources.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class CommonResourcesDBManager extends AbstractDBManager<Resource>{

	/**
	 * Creates the DB manager
	 * @param factory SQL factory
	 */
	public CommonResourcesDBManager(SQLContainerFactory factory) {
		super(Queues.COMMON_RESOURCES_MAP, factory.getSQLContainerForCommonResourcesMap(), XmlUtil.getXStream(), false);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.AbstractDBManager#getKey(java.lang.Object)
	 */
	@Override
	public String getKey(Resource item) {
		return item.getName();
	}
}