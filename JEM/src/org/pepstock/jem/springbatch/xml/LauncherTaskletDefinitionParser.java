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
package org.pepstock.jem.springbatch.xml;

import org.pepstock.jem.springbatch.tasks.utilities.LauncherTasklet;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Specific parser for <launcher> tag, to create tasklet bean.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class LauncherTaskletDefinitionParser extends TaskletDefinitionParser {
	
	static final String LAUNCHER_TASKLET_ELEMENT = "launcher";
	
	static final String OBJECT_ATTRIBUTE = "object";
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.springbatch.xml.TaskletDefinitionParser#parseInternal(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
		// creates a factory which contains all root and children objects
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(LauncherTaskletFactoryBean.class);
		factory.addPropertyValue(TASKLET_ELEMENT, parseTasklet(element));
		// loads all data description, locks, data source
		loadChildren(element, factory, context);
		return factory.getBeanDefinition();
	}
	
	/**
	 * Creates a launcher tasklet object using the class of bean
	 * and reading the object reference to be executed
	 * 
	 * @param element XML element to parse
	 * @return tasklet object using the class of bean
	 */
	private BeanDefinition parseTasklet(Element element) {
		// gets main launcher tasklet
		BeanDefinitionBuilder component = BeanDefinitionBuilder.rootBeanDefinition(LauncherTasklet.class);
		// reads the mandatory attribute if object reference
		component.addPropertyReference(OBJECT_ATTRIBUTE, element.getAttribute(OBJECT_ATTRIBUTE));
		return component.getBeanDefinition();
	}
}