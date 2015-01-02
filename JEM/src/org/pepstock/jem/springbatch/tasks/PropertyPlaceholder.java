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

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Extends the org.springframework.beans.factory.config.PropertyPlaceholderConfigurer of Spring to load properties 
 * inside of Job properties. It must be used if we want to change variables on resources configuration.
 * 
 * @see JobsProperties
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class PropertyPlaceholder extends PropertyPlaceholderConfigurer {

	/* (non-Javadoc)
	 * @see org.springframework.core.io.support.PropertiesLoaderSupport#loadProperties(java.util.Properties)
	 */
	@Override
	protected void loadProperties(Properties props) throws IOException {
		super.loadProperties(props);
		JobsProperties.getInstance().loadProperties(props);
	}
}
