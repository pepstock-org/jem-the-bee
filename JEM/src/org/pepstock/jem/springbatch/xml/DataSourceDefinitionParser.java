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

import org.pepstock.jem.springbatch.tasks.DataSource;
import org.pepstock.jem.springbatch.tasks.Property;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Specific parser for <dataSource> tag, to create data source bean.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
public class DataSourceDefinitionParser extends AbstractBeanDefinitionParser {
	
	static final String DATA_SOURCE_ELEMENT = "dataSource";
	
	static final String PROPERTY_ELEMENT = "property";
	
	static final String NAME_ATTRIBUTE = "name";

	static final String VALUE_ATTRIBUTE = "value";
	
	static final String RESOURCE_ATTRIBUTE = "resource";
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
	 * parseInternal(org.w3c.dom.Element,
	 * org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
		// creates the root of all beans
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(DataSourceFactoryBean.class);
		factory.addPropertyValue(DataSourceFactoryBean.DATA_SOURCE, parseDataSource(element));

		// scans all property definition
		List<Element> childElements = DomUtils.getChildElementsByTagName(element, PROPERTY_ELEMENT);
		if (childElements != null && !childElements.isEmpty()) {
			parseProperty(childElements, factory);
		}
		return factory.getBeanDefinition();
	}

	/**
	 * Creates a data source bean setting the right attribute from XML element
	 * @param element XML element to parse
	 * @return data source object using the class of bean
	 */
	private BeanDefinition parseDataSource(Element element) {
		BeanDefinitionBuilder component = BeanDefinitionBuilder.rootBeanDefinition(DataSource.class);
		Util.setProperty(element, component, NAME_ATTRIBUTE);
		Util.setProperty(element, component, RESOURCE_ATTRIBUTE);
		return component.getBeanDefinition();
	}

	/**
	 * Creates and loads all property children of data source element
	 * @param childElements all XML elements 
	 * @param factory parent bean builder to load
	 */
	@SuppressWarnings("unchecked")
	private void parseProperty(List<Element> childElements, BeanDefinitionBuilder factory) {
		// creates a list. Be carefully to use ManageList of Spring which auto wiring the objects
		@SuppressWarnings("rawtypes")
		ManagedList properties = new ManagedList(childElements.size());

		for (Element element : childElements) {
			// creates bean only if content not null
			if (StringUtils.hasText(element.getTextContent())) {
				// creates a builder for all properties
				BeanDefinitionBuilder component = BeanDefinitionBuilder.genericBeanDefinition(Property.class);
				Util.setProperty(element, component, NAME_ATTRIBUTE);
				component.addPropertyValue(VALUE_ATTRIBUTE, element.getTextContent());
				properties.add(component.getBeanDefinition());
			}
		}
		factory.addPropertyValue(DataSourceFactoryBean.PROPERTIES, properties);
	}
}
