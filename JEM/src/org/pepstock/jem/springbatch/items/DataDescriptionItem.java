/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
package org.pepstock.jem.springbatch.items;

import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.jem.springbatch.tasks.DataDescription;
import org.springframework.core.io.Resource;

/**
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public interface DataDescriptionItem {
	
	/**
	 * @return datadescription instance of item
	 */
	DataDescription getDataDescription();
	
	/**
	 * @param dataDescription datadescription instance of item
	 */
	void setDataDescription(DataDescription dataDescription);
	
	/**
	 * 
	 * @return step name which is using the data description item
	 */
	String getStepName();
	
	/**
	 * @return the dataDescriptionImpl
	 */
	DataDescriptionImpl getDataDescriptionImpl();
	/**
	 * @param dataDescriptionImpl the dataDescriptionImpl to set
	 */
	void setDataDescriptionImpl(DataDescriptionImpl dataDescriptionImpl);
	
	/**
	 * 
	 * @param resources sets teh resources for SB
	 */
	void setResources(Resource[] resources);
}