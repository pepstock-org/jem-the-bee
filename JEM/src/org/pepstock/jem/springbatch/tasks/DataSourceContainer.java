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
package org.pepstock.jem.springbatch.tasks;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.sql.DataSource;

import org.pepstock.jem.node.tasks.InitiatorManager;
import org.pepstock.jem.springbatch.DataSourceFactory;
import org.pepstock.jem.springbatch.SpringBatchFactory;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
final class DataSourceContainer {
	
	private static DataSource DATASOURCE_INSTANCE = null;
	
	private static String DATASOURCE_TYPE_INSTANCE = null;

	/**
	 * To avoid any instatiation
	 */
	private DataSourceContainer() {
		
	}
	
	static void createInstances() throws RemoteException, UnknownHostException{
		if (DATASOURCE_INSTANCE == null){
			Properties jdbcProps = InitiatorManager.getCommonResourcer().getJemFactoryProperties(SpringBatchFactory.SPRINGBATCH_TYPE);
			DATASOURCE_INSTANCE = DataSourceFactory.createDataSource(jdbcProps);
			DATASOURCE_TYPE_INSTANCE = DataSourceFactory.getDataSourceType(jdbcProps);
		}
	}

	static synchronized DataSource getDataSource(){
		return DATASOURCE_INSTANCE;
	}

	static synchronized String getDataSourceType(){
		return DATASOURCE_TYPE_INSTANCE;
	}

}
