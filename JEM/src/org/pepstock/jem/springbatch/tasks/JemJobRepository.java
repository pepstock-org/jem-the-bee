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

import javax.sql.DataSource;

import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.database.support.DataFieldMaxValueIncrementerFactory;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class JemJobRepository extends JobRepositoryFactoryBean {
	
	static final String UNABLE_OVERRIDE = "Unable to override JEM configuration";

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		
		JemTransactionManager jemTm = null;
		
		PlatformTransactionManager tm = super.getTransactionManager();
		
		if (tm == null){
			jemTm = new JemTransactionManager();
			jemTm.afterPropertiesSet();
		} else if (tm instanceof JemTransactionManager){
			jemTm = (JemTransactionManager) tm;
		} else {
			jemTm = new JemTransactionManager();
			jemTm.afterPropertiesSet();
		}
		
		super.setTransactionManager(jemTm);
		
		DataSource dataSource = jemTm.getDataSource();
		super.setDataSource(dataSource);
		super.setDatabaseType(jemTm.getDatabaseType());
		
		super.afterPropertiesSet();
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setClobType(int)
	 */
	@Override
	public void setClobType(int type) {
		throw new UnsupportedOperationException(UNABLE_OVERRIDE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setDataSource(javax.sql.DataSource)
	 */
	@Override
	public void setDataSource(DataSource dataSource) {
		throw new UnsupportedOperationException(UNABLE_OVERRIDE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setDatabaseType(java.lang.String)
	 */
	@Override
	public void setDatabaseType(String dbType) {
		throw new UnsupportedOperationException(UNABLE_OVERRIDE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setIncrementerFactory(org.springframework.batch.item.database.support.DataFieldMaxValueIncrementerFactory)
	 */
	@Override
	public void setIncrementerFactory(DataFieldMaxValueIncrementerFactory incrementerFactory) {
		throw new UnsupportedOperationException(UNABLE_OVERRIDE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setMaxVarCharLength(int)
	 */
	@Override
	public void setMaxVarCharLength(int maxVarCharLength) {
		throw new UnsupportedOperationException(UNABLE_OVERRIDE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setSerializer(org.springframework.batch.core.repository.ExecutionContextSerializer)
	 */
	@Override
	public void setSerializer(ExecutionContextSerializer serializer) {
		throw new UnsupportedOperationException(UNABLE_OVERRIDE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setTablePrefix(java.lang.String)
	 */
	@Override
	public void setTablePrefix(String tablePrefix) {
		throw new UnsupportedOperationException(UNABLE_OVERRIDE);
	}
}
