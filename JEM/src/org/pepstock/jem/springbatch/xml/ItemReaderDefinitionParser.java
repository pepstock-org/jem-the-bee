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

import org.pepstock.jem.springbatch.items.DataDescriptionItemReader;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * Specific parser for <itemReader> tag, to create item reader bean.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class ItemReaderDefinitionParser extends AbstractBeanDefinitionParser {
	
	static final String ITEM_READER_ELEMENT = "itemReader";
	
	static final String DELEGATE_ATTRIBUTE = "delegate";
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#
	 * parseInternal(org.w3c.dom.Element,
	 * org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected AbstractBeanDefinition parseInternal(Element element, ParserContext context) {
		// creates a factory which containes all root and children objects
		BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(ItemReaderFactoryBean.class);
		factory.addPropertyValue(ITEM_READER_ELEMENT, parseItemReader());
		// load delegate bean
		BeanDefinitionBuilder component = BeanDefinitionBuilder.genericBeanDefinition(element.getAttribute(DELEGATE_ATTRIBUTE));
		factory.addPropertyValue(ItemReaderFactoryBean.DELEGATE, component.getBeanDefinition());

		loadChildren(element, factory, context);
		return factory.getBeanDefinition();
	}
	
	/**
	 * Loads all children defined for a JEM item reader.
	 * @param element XML element to parse
	 * @param factory parent bean builder to load
	 * @param context parser context
	 */
	protected final void loadChildren(Element element, BeanDefinitionBuilder factory, ParserContext context){
		// Reads all data description children and creates objects for next bindings
		List<Element> childDataDescriptions = DomUtils.getChildElementsByTagName(element, DataDescriptionDefinitionParser.DATA_DESCRIPTION_ELEMENT);
		if (childDataDescriptions != null && !childDataDescriptions.isEmpty()) {
			parseDataDescriptions(childDataDescriptions, factory, context);
		}

		// Reads all data source children and creates objects for next bindings
		List<Element> childDataSources = DomUtils.getChildElementsByTagName(element, DataSourceDefinitionParser.DATA_SOURCE_ELEMENT);
		if (childDataSources != null && !childDataSources.isEmpty()) {
			parseDataSources(childDataSources, factory, context);
		}
		
		// Reads all locks children and creates objects for next bindings
		List<Element> childLocks = DomUtils.getChildElementsByTagName(element, LockDefinitionParser.LOCK_ELEMENT);
		if (childLocks != null && !childLocks.isEmpty()) {
			parseLocks(childLocks, factory, context);
		}
	}

	/**
	 * Creates a item Reader object using the class of bean
	 * 
	 * @param element XML element to parse
	 * @return item reader object using the class of bean
	 */
	private BeanDefinition parseItemReader() {
		BeanDefinitionBuilder component = BeanDefinitionBuilder.rootBeanDefinition(DataDescriptionItemReader.class);
		return component.getBeanDefinition();
	}


	/**
	 * Creates and loads data description child of item reader element
	 * @param childElement XML element 
	 * @param factory parent bean builder to load
	 * @param context parser context
	 */
	@SuppressWarnings("unchecked")
	private void parseDataDescriptions(List<Element> childElements, BeanDefinitionBuilder factory, ParserContext context) {
		// creates a list. Be carefully to use ManageList of Spring which auto wiring the objects
		@SuppressWarnings("rawtypes")
		ManagedList children = new ManagedList(childElements.size());

		// creates data description parser 
		DataDescriptionDefinitionParser parser = new DataDescriptionDefinitionParser();
		for (Element element : childElements) {
			children.add(parser.parseInternal(element, context));
		}
		factory.addPropertyValue(ItemReaderFactoryBean.DATA_DESCRIPTIONS, children);
	}
	/**
	 * Creates and loads all data source children of item reader element
	 * @param childElements all XML elements 
	 * @param factory parent bean builder to load
	 * @param context parser context
	 */
	@SuppressWarnings("unchecked")
	private void parseDataSources(List<Element> childElements, BeanDefinitionBuilder factory, ParserContext context) {
		// creates a list. Be carefully to use ManageList of Spring which auto wiring the objects		
		@SuppressWarnings("rawtypes")
		ManagedList children = new ManagedList(childElements.size());
		
		// creates data source parser 
		DataSourceDefinitionParser parser = new DataSourceDefinitionParser();
		for (Element element : childElements) {
			children.add(parser.parseInternal(element, context));
		}
		factory.addPropertyValue(ItemReaderFactoryBean.DATA_SOURCES, children);
	}
	/**
	 * Creates and loads all lock children of item reader element
	 * @param childElements all XML elements 
	 * @param factory parent bean builder to load
	 * @param context parser context
	 */
	@SuppressWarnings("unchecked")
	private void parseLocks(List<Element> childElements, BeanDefinitionBuilder factory, ParserContext context) {
		// creates a list. Be carefully to use ManageList of Spring which auto wiring the objects		
		@SuppressWarnings("rawtypes")
		ManagedList children = new ManagedList(childElements.size());

		// creates lock parser 
		LockDefinitionParser parser = new LockDefinitionParser(true);
		for (Element element : childElements) {
			children.add(parser.parse(element, context));
		}
		factory.addPropertyValue(ItemReaderFactoryBean.LOCKS, children);
	}

}
