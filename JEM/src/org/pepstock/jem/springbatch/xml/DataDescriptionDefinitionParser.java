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

import java.util.List;

import org.pepstock.jem.springbatch.tasks.DataDescription;
import org.pepstock.jem.springbatch.tasks.DataSet;
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
 * Specific parser for <dataDescription> tag, to create data descriptions bean.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
public class DataDescriptionDefinitionParser extends AbstractBeanDefinitionParser {
	
	static final String DATA_DESCRIPTION_ELEMENT = "dataDescription";
	
	static final String DATA_SET_ELEMENT = "dataSet";
	
	static final String NAME_ATTRIBUTE = "name";
	
	static final String SYSOUT_ATTRIBUTE = "sysout";

	static final String DISPOSITION_ATTRIBUTE = "disposition";
	
	static final String TEXT_ATTRIBUTE = "text";
	
	static final String DATASOURCE_ATTRIBUTE = "datasource";
	
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
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(DataDescriptionFactoryBean.class);
		factory.addPropertyValue(DataDescriptionFactoryBean.DATA_DESCRIPTION, parseDataDescription(element));

		// scans all data sets definition
		List<Element> childElements = DomUtils.getChildElementsByTagName(element, DATA_SET_ELEMENT);
		if (childElements != null && !childElements.isEmpty()) {
			parseDataSet(childElements, factory);
		}
		return factory.getBeanDefinition();
	}

	/**
	 * Creates a data description bean setting the right attribute from XML element
	 * @param element XML element to parse
	 * @return data description object using the class of bean
	 */
	private BeanDefinition parseDataDescription(Element element) {
		BeanDefinitionBuilder component = BeanDefinitionBuilder.rootBeanDefinition(DataDescription.class);
		Util.setProperty(element, component, NAME_ATTRIBUTE);
		Util.setProperty(element, component, DISPOSITION_ATTRIBUTE);
		Util.setProperty(element, component, SYSOUT_ATTRIBUTE, Boolean.class);
		return component.getBeanDefinition();
	}

	/**
	 * Creates and loads all data set children of data description element
	 * @param childElements all XML elements 
	 * @param factory parent bean builder to load
	 */
	@SuppressWarnings("unchecked")
	private void parseDataSet(List<Element> childElements, BeanDefinitionBuilder factory) {
		// creates a list. Be carefully to use ManageList of Spring which auto wiring the objects
		@SuppressWarnings("rawtypes")
		ManagedList children = new ManagedList(childElements.size());

		for (Element element : childElements) {
			// creates a builder for all datasets
			BeanDefinitionBuilder component = BeanDefinitionBuilder.genericBeanDefinition(DataSet.class);
			Util.setProperty(element, component, NAME_ATTRIBUTE);
			Util.setProperty(element, component, DATASOURCE_ATTRIBUTE);
			// sets content only if not null
			if (StringUtils.hasText(element.getTextContent())) {
				component.addPropertyValue(TEXT_ATTRIBUTE, element.getTextContent().trim());
			}
			children.add(component.getBeanDefinition());
		}
		factory.addPropertyValue(DataDescriptionFactoryBean.DATA_SETS, children);
	}
}
