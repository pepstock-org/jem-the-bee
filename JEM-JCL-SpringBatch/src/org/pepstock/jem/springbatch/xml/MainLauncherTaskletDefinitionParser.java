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

import org.pepstock.jem.springbatch.tasks.utilities.MainLauncherTasklet;
import org.pepstock.jem.springbatch.xml.MainLauncherTaskletFactoryBean;
import org.pepstock.jem.springbatch.xml.TaskletDefinitionParser;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Specific parser for <main-launcher> tag, to create tasklet bean.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class MainLauncherTaskletDefinitionParser extends TaskletDefinitionParser {
	
	static final String MAIN_LAUNCHER_TASKLET_ELEMENT = "main-launcher";
	
	static final String CLASS_NAME_ATTRIBUTE = "className";

	static final String ARGUMENTS_ELEMENT = "arguments";
	
	static final String ARGUMENT_ELEMENT = "argument";
	
	static final String CLASSPATH_ELEMENT = "classPath";
	
	static final String PATH_ELEMENT_ELEMENT = "pathElement";
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.springbatch4.xml.TaskletDefinitionParser#parseInternal(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
		// creates a factory which contains all root and children objects
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(MainLauncherTaskletFactoryBean.class);
		factory.addPropertyValue(TASKLET_ELEMENT, parseTasklet(element));
		// parses all arguments elements
		parseArguments(element, factory);
		// parses classPath elements
		parseClassPath(element, factory);
		// loads all data description, locks, data source
		loadChildren(element, factory, context);
		return factory.getBeanDefinition();
	}
	
	/**
	 * Creates a MAIN launcher tasklet object using the class of bean
	 * and reading the class name to be executed
	 * 
	 * @param element XML element to parse
	 * @return tasklet object using the class of bean
	 */
	private BeanDefinition parseTasklet(Element element) {
		// gets main laucnher tasklet
		BeanDefinitionBuilder component = BeanDefinitionBuilder.rootBeanDefinition(MainLauncherTasklet.class);
		// reads the mandatory attribute if class name
		component.addPropertyValue(CLASS_NAME_ATTRIBUTE, element.getAttribute(CLASS_NAME_ATTRIBUTE));
		return component.getBeanDefinition();
	}
	
	/**
	 * Loads all java arguments children of tasklet element
	 * @param childElements all XML elements 
	 * @param factory parent bean builder to load
	 * @param context parser context
	 */
	private void parseArguments(Element element, BeanDefinitionBuilder factory) {
		// gets the ARGUMENTS element
		Element arguments = DomUtils.getChildElementByTagName(element, ARGUMENTS_ELEMENT);
		// if is missing, return
		if (arguments == null){
			return;
		}
		// reads all ARGUMENT elements, children of ARGUMENTS
		List<Element> argumentList = DomUtils.getChildElementsByTagName(arguments, ARGUMENT_ELEMENT);
		// if are missing, return
		if (argumentList == null){
			return;
		}
		// loads all contents of all argument elements
		loadElementsValue(argumentList, MainLauncherTaskletFactoryBean.ARGUMENTS, factory);
	}
	/**
	 * Loads all java classpath element children of tasklet element
	 * @param childElements all XML elements 
	 * @param factory parent bean builder to load
	 * @param context parser context
	 */
	private void parseClassPath(Element element, BeanDefinitionBuilder factory) {
		// gets the CLASSPATH element
		Element classpath = DomUtils.getChildElementByTagName(element,CLASSPATH_ELEMENT);
		// if is missing, return
		if (classpath == null){
			return;
		}
		// reads all PATHELEMENT elements, children of CLASSPATH
		List<Element> pathElementList = DomUtils.getChildElementsByTagName(classpath, PATH_ELEMENT_ELEMENT);
		// if are missing, return
		if (pathElementList == null){
			return;
		}
		// loads all contents of all pathelement elements
		loadElementsValue(pathElementList, MainLauncherTaskletFactoryBean.CLASSPATH, factory);
	}
	
	/**
	 * Loads all content of all element, putting everything in a manage list of SpringBatch
	 * @param elements list of elements to scan to get the content
	 * @param key key to use to put the manage list into the bean
	 * @param factory factory bean to be loaded with the manage list
	 */
	@SuppressWarnings("unchecked")
	private void loadElementsValue(List<Element> elements, String key, BeanDefinitionBuilder factory){
		// creates the manage list
		@SuppressWarnings("rawtypes")
		ManagedList elementsList = new ManagedList(elements.size());
		// scans all elements
		for (Element path :elements) {
			// sets content only if not null
			if (StringUtils.hasText(path.getTextContent())) {
				// adds to the manage list the content of element 
				elementsList.add(path.getTextContent().trim());
			}
		}
		// adds the list to factory bean
		factory.addPropertyValue(key, elementsList);
	}
}