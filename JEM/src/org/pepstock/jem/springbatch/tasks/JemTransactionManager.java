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

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class JemTransactionManager extends DataSourceTransactionManager {

	private static final long serialVersionUID = 1L;

	private String databaseType = null;

	/**
	 * 
	 * @return
	 */
	String getDatabaseType(){
		return databaseType;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.jdbc.datasource.DataSourceTransactionManager#setDataSource(javax.sql.DataSource)
	 */
	@Override
	public void setDataSource(DataSource dataSource) {
		throw new UnsupportedOperationException(JemJobRepository.UNABLE_OVERRIDE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.datasource.DataSourceTransactionManager#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() {
		try {
			DataSourceContainer.createInstances();
			if (DataSourceContainer.getDataSource() == null){
				// FIXME
				throw new RuntimeException("data source is null");
			}
			super.setDataSource(DataSourceContainer.getDataSource());
			databaseType = DataSourceContainer.getDataSourceType();
		} catch (RemoteException e) {
			// FIXME
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.afterPropertiesSet();
	}
}
