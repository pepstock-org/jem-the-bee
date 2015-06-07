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

import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.pepstock.jem.springbatch.SpringBatchRuntimeException;
import org.springframework.batch.support.DatabaseType;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.MetaDataAccessException;

/**
 * Extends the OOTB data source TM of Spring Batc, using and setting the data source defined
 * in the JCL factory properties.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class JemTransactionManager extends DataSourceTransactionManager {

	private static final long serialVersionUID = 1L;

	private String databaseType = null;

	/**
	 * Returns the data base type
	 * @return the data base type
	 */
	String getDatabaseType(){
		return databaseType;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.jdbc.datasource.DataSourceTransactionManager#setDataSource(javax.sql.DataSource)
	 */
	@Override
	public void setDataSource(DataSource dataSource) {
		// It's not possible to set a datasource.
		// it can use ONLY the data source which can be defined
		// by JCL factory properties
		throw new UnsupportedOperationException(JemJobRepository.UNABLE_OVERRIDE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.datasource.DataSourceTransactionManager#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() {
		try {
			// creates the data source  by RMI
			DataSourceContainer.createInstances();
			// if null, EXCEPTION
			if (DataSourceContainer.getDataSource() == null){
				throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS055E);
			}
			// uses the SUPER method to set the data source 
			// it can not use the set method of this class
			super.setDataSource(DataSourceContainer.getDataSource());
			// gets database type from data source
			databaseType = DatabaseType.fromMetaData(getDataSource()).getProductName();
		} catch (RemoteException e) {
			throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS056E, e);
		} catch (UnknownHostException e) {
			throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS056E, e);
		} catch (MetaDataAccessException e) {
			throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS066E, e);
		}
		// calls the method of the OOTB TM of Spring Batch
		super.afterPropertiesSet();
	}
}