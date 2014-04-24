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

import org.pepstock.jem.jppf.JPPFTasklet;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
public class JPPFTaskletDefinitionParser extends TaskletDefinitionParser {
	
	static final String JPPF_TASKLET_ELEMENT = "jppf-tasklet";

	/* (non-Javadoc)
	 * @see org.pepstock.jem.springbatch.xml.TaskletDefinitionParser#parseInternal(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
		// creates a factory which containes all root and children objects
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(JPPFTaskletFactoryBean.class);
		factory.addPropertyValue(TASKLET_ELEMENT, parseTasklet(element));

		Element config = DomUtils.getChildElementByTagName(element, JPPFBeanDefinitionParser.CONFIGURATION_ELEMENT);
		if (config != null){
			parseJPPFConfiguration(config, factory, context);
		}
		loadChildren(element, factory, context);
		return factory.getBeanDefinition();
	}
	
	/**
	 * Creates a tasklet object using the class of bean
	 * 
	 * @param element XML element to parse
	 * @return tasklet object using the class of bean
	 */
	private BeanDefinition parseTasklet(Element element) {
		BeanDefinitionBuilder component = BeanDefinitionBuilder.rootBeanDefinition(JPPFTasklet.class);
		return component.getBeanDefinition();
	}
	
	/**
	 * Creates and loads all jppf configuration children of tasklet element
	 * @param childElements all XML elements 
	 * @param factory parent bean builder to load
	 * @param context parser context
	 */
	private void parseJPPFConfiguration(Element element, BeanDefinitionBuilder factory, ParserContext context) {
		// creates configuration parser
		JPPFBeanDefinitionParser parser = new JPPFBeanDefinitionParser();
		factory.addPropertyValue(JPPFTaskletFactoryBean.JPPF_CONFIGURATION, parser.parse(element, context));
	}
}
