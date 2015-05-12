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

import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.repository.ExecutionContextSerializer;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class JemJobExplorer extends JobExplorerFactoryBean {

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.explore.support.JobExplorerFactoryBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			DataSourceContainer.createInstances();
			if (DataSourceContainer.getDataSource() == null){
				// FIXME
				throw new RuntimeException("data source is null");
			}
			super.setDataSource(DataSourceContainer.getDataSource());
		} catch (RemoteException e) {
			// FIXME
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.afterPropertiesSet();
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setDataSource(javax.sql.DataSource)
	 */
	@Override
	public void setDataSource(DataSource dataSource) {
		throw new UnsupportedOperationException(JemJobRepository.UNABLE_OVERRIDE);
	}


	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setSerializer(org.springframework.batch.core.repository.ExecutionContextSerializer)
	 */
	@Override
	public void setSerializer(ExecutionContextSerializer serializer) {
		throw new UnsupportedOperationException(JemJobRepository.UNABLE_OVERRIDE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setTablePrefix(java.lang.String)
	 */
	@Override
	public void setTablePrefix(String tablePrefix) {
		throw new UnsupportedOperationException(JemJobRepository.UNABLE_OVERRIDE);
	}
	
}
