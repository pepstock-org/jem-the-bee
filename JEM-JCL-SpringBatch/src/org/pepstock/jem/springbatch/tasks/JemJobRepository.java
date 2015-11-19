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
 * Is a custom implementation of SB job repository which will use a JEM transaction manager to access to the data source,
 * defined in the SB JCL, to save the status of job for restart it, in cause of error
 *  * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class JemJobRepository extends JobRepositoryFactoryBean {
	
	// common label for error when you try to override some methods
	static final String UNABLE_OVERRIDE = "Unable to override JEM configuration";

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		// gets TM
		// be aware that the TM to use MUST be a JEMTRANSATIONMANAGER
		JemTransactionManager jemTm = null;
		
		// gets the TM defined in jCL
		PlatformTransactionManager tm = super.getTransactionManager();
		
		// if doesn't exist
		if (tm == null){
			// creates a JEM transaction manager at runtime
			jemTm = new JemTransactionManager();
			// and calls the afterproperties of bean
			jemTm.afterPropertiesSet();
		} else if (tm instanceof JemTransactionManager){
			// if the defined is JEM TM, just set 
			jemTm = (JemTransactionManager) tm;
		} else {
			// if the TM defined in JCL is NOT a JEM TM
			// it doesn't create any error
			// but ignore the TM defined, and creates a new JEM TM
			jemTm = new JemTransactionManager();
			// and calls the afterproperties of bean
			jemTm.afterPropertiesSet();
		}
		// sets the new TRANSACTION manager
		super.setTransactionManager(jemTm);
		
		// gets the data source and database type from transation manager
		DataSource dataSource = jemTm.getDataSource();
		super.setDataSource(dataSource);
		super.setDatabaseType(jemTm.getDatabaseType());
		// calls the super after properties
		super.afterPropertiesSet();
	}


	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setClobType(int)
	 * 
	 * PAY ATTENTION: if you use SB 2.1.9, you must remove it
	 */
	@Override
	public void setClobType(int type) {
		// you must use the default
		throw new UnsupportedOperationException(UNABLE_OVERRIDE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setDataSource(javax.sql.DataSource)
	 */
	@Override
	public void setDataSource(DataSource dataSource) {
		// It's not possible to set a datasource.
		// it can use ONLY the data source which can be defined
		// by JCL factory properties
		throw new UnsupportedOperationException(UNABLE_OVERRIDE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setDatabaseType(java.lang.String)
	 */
	@Override
	public void setDatabaseType(String dbType) {
		// It's not possible to set a data base type.
		// it can use ONLY the data base type extracted from data source
		throw new UnsupportedOperationException(UNABLE_OVERRIDE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setIncrementerFactory(org.springframework.batch.item.database.support.DataFieldMaxValueIncrementerFactory)
	 */
	@Override
	public void setIncrementerFactory(DataFieldMaxValueIncrementerFactory incrementerFactory) {
		// you must use the default
		throw new UnsupportedOperationException(UNABLE_OVERRIDE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setMaxVarCharLength(int)
	 */
	@Override
	public void setMaxVarCharLength(int maxVarCharLength) {
		// you must use the default
		throw new UnsupportedOperationException(UNABLE_OVERRIDE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setSerializer(org.springframework.batch.core.repository.ExecutionContextSerializer)
	 * 
	 * PAY ATTENTION: if you use SB 2.1.9, you must remove it
	 */
	@Override
	public void setSerializer(ExecutionContextSerializer serializer) {
		// you must use the default
		throw new UnsupportedOperationException(UNABLE_OVERRIDE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.repository.support.JobRepositoryFactoryBean#setTablePrefix(java.lang.String)
	 */
	@Override
	public void setTablePrefix(String tablePrefix) {
		// you must use the default
		throw new UnsupportedOperationException(UNABLE_OVERRIDE);
	}
}
