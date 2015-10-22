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
 * It contains the data source instance for job repository.It asks by RMI to JEM node to get
 * all properties used to deine SpringBatch JCL factory, necessary to create a data source.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
final class DataSourceContainer {
	
	private static DataSource DATASOURCE_INSTANCE = null;
	
	/**
	 * To avoid any instantiation
	 */
	private DataSourceContainer() {
	}
	
	/**
	 * Creates the data source instance and get the data source type.
	 * 
	 * @throws RemoteException if any RMI exception occurs getting the JCL factory properties
	 * @throws UnknownHostException if any RMI exception occurs getting the JCL factory properties
	 */
	static synchronized void createInstances() throws RemoteException, UnknownHostException{
		// creates the data source instance ONLY the first time 
		if (DATASOURCE_INSTANCE == null){
			// gets by RMI of JCL factory properties
			Properties jdbcProps = InitiatorManager.getCommonResourcer().getJemFactoryProperties(SpringBatchFactory.SPRINGBATCH_TYPE);
			// creates a data source 
			DATASOURCE_INSTANCE = DataSourceFactory.createDataSource(jdbcProps);
		}
	}

	/**
	 * Returns the data source instance
	 * @return the data source instance
	 */
	static DataSource getDataSource(){
		return DATASOURCE_INSTANCE;
	}
}
