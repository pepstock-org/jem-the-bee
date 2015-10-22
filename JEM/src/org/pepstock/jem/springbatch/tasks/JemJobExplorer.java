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

import org.pepstock.jem.springbatch.SpringBatchException;
import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.repository.ExecutionContextSerializer;

/**
 * Custom job explorer of SpringBatch which uses a JCL configuration of data source
 * to use for restartability.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public class JemJobExplorer extends JobExplorerFactoryBean {

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.explore.support.JobExplorerFactoryBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			// creates the data source  by RMI
			DataSourceContainer.createInstances();
			// if null, EXCEPTION
			if (DataSourceContainer.getDataSource() == null){
				throw new SpringBatchException(SpringBatchMessage.JEMS055E);
			}
			// uses the SUPER method to set the data source 
			// it can not use the set method of this class
			super.setDataSource(DataSourceContainer.getDataSource());
		} catch (RemoteException e) {
			throw new SpringBatchException(SpringBatchMessage.JEMS056E, e);
		} catch (UnknownHostException e) {
			throw new SpringBatchException(SpringBatchMessage.JEMS056E, e);
		}
		// calls the method of the OOTB TM of Spring Batch
		super.afterPropertiesSet();
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setDataSource(javax.sql.DataSource)
	 */
	@Override
	public void setDataSource(DataSource dataSource) {
		// It's not possible to set a datasource.
		// it can use ONLY the data source which can be defined
		// by JCL factory properties
		throw new UnsupportedOperationException(JemJobRepository.UNABLE_OVERRIDE);
	}


	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setSerializer(org.springframework.batch.core.repository.ExecutionContextSerializer)
	 */
	@Override
	public void setSerializer(ExecutionContextSerializer serializer) {
		// It's not possible to set a serializer.
		// it can use ONLY the serializer OOTB
		throw new UnsupportedOperationException(JemJobRepository.UNABLE_OVERRIDE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setTablePrefix(java.lang.String)
	 */
	@Override
	public void setTablePrefix(String tablePrefix) {
		// It's not possible to set a table prefix.
		// it can use ONLY the table prefix OOTB
		throw new UnsupportedOperationException(JemJobRepository.UNABLE_OVERRIDE);
	}
	
}
