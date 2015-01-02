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
package org.pepstock.jem.springbatch.xml;

import java.util.List;

import org.pepstock.jem.springbatch.tasks.DataDescription;
import org.pepstock.jem.springbatch.tasks.DataSet;
import org.springframework.beans.factory.FactoryBean;

/**
 * Factory bean for complex XML element for data description <dataDescription>.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
public class DataDescriptionFactoryBean implements FactoryBean<Object> {
	
	static final String DATA_DESCRIPTION = "dataDescription";
	
	static final String DATA_SETS = "dataSets";
	
	private DataDescription dataDescription = null;
	
	private List<DataSet> dataSets = null;


	/**
	 * @return the dataDescription
	 */
	public DataDescription getDataDescription() {
		return dataDescription;
	}

	/**
	 * @param dataDescription the dataDescription to set
	 */
	public void setDataDescription(DataDescription dataDescription) {
		this.dataDescription = dataDescription;
	}

	/**
	 * @return the dataSets
	 */
	public List<DataSet> getDataSets() {
		return dataSets;
	}

	/**
	 * @param dataSets the dataSets to set
	 */
	public void setDataSets(List<DataSet> dataSets) {
		this.dataSets = dataSets;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public Object getObject() throws Exception {
		// if datasets are set, loads into data description
	    if (this.dataSets != null && !this.dataSets.isEmpty()) {
	    	dataDescription.setDatasets(dataSets);
	    }
		return dataDescription;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<DataDescription> getObjectType() {
		return DataDescription.class;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		return false;
	}

}
