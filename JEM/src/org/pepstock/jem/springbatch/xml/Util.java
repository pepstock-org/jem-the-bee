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

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Utility to set property inside of bean builder 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
final class Util {

	/**
	 * private to avoid any interaction 
	 */
	private Util() {

	}

	/**
	 * Sets a property using XML element and name of property. String is added
	 * 
	 * @param element XML element to read to load the attributes
	 * @param bean bean builder to set property
	 * @param name name of property
	 */
	public static final void setProperty(Element element, BeanDefinitionBuilder bean, String name){
		setProperty(element, bean, name, String.class);
	}
	
	/**
	 * Sets a property using XML element, name of property and type of value
	 * 
	 * @param element XML element to read to load the attributes
	 * @param bean bean builder to set property
	 * @param name name of property
	 * @param what class of type to add
	 */
	public static final void setProperty(Element element, BeanDefinitionBuilder bean, String name, Class<?> what){
		String value = element.getAttribute(name);
		// tests if the value is null
		// if yes, ignore
		if (StringUtils.hasText(value)) {
			// adds boolean, integer or string values
			if (what.equals(Boolean.class)){
				bean.addPropertyValue(name, Boolean.valueOf(value));
			} else if (what.equals(Integer.class)){
				bean.addPropertyValue(name, Integer.valueOf(value));
			} else {
				bean.addPropertyValue(name, value);
			}
		} 
	}
}
