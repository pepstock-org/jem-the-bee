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
package org.pepstock.jem.node.resources.impl;

import java.util.List;
import java.util.Properties;

import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * Common JNDI common factory of JEM factories. It loads all JDNI refaddr on properties
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public abstract class AbstractObjectFactory implements ObjectFactory {

	/**
	 * Loads JNDI RefAddr instance into java properties
	 * @param object JNDI object
	 * @param allProperties all properties of the resource type
	 * @return properties object
	 */
	public final Properties loadProperties(Object object, List<String> allProperties){
		Reference ref = (Reference) object;
		Properties properties = new Properties();
		for (int i = 0; i < allProperties.size(); i++) {
			String propertyName = allProperties.get(i);
			RefAddr ra = ref.get(propertyName);
			if (ra != null) {
				String propertyValue = ra.getContent().toString();
				properties.setProperty(propertyName, propertyValue);
			}
		}
		return properties;
	}
}
