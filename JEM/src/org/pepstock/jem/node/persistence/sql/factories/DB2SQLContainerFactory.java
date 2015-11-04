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
package org.pepstock.jem.node.persistence.sql.factories;


/**
 * Contains all DB2 SQL for Hazelcast persistence
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class DB2SQLContainerFactory extends DefaultSQLContainerFactory {
	/**
	 * is the type of the database in this case mysql
	 */
	public static final String DATABASE_TYPE = "db2";
	
	private static final String TEST_CONNECTION_SQL_QUERY = "SELECT CURRENT DATE FROM SYSIBM.SYSDUMMY1";


	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.persistence.SQLContainerFactory#getTestConnectionSQL()
	 */
	@Override
	public String getKeepAliveConnectionSQL() {
		return TEST_CONNECTION_SQL_QUERY;
	}
	
}
