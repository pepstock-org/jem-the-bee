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

import java.util.UUID;

import org.pepstock.jem.jppf.JPPFBean;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Specific parser for <jppf-configuration> tag, to create JPPFBean bean, with all properties to execute the JPPF task.<br>
 * It uses the extensions XML authoring of SprigBatch.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
public class JPPFBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
	
	// these are all the elements and attributes
	// which can be use to add the JEM configuration bean 
	// in a Springbatch using the extension XML authoring
	static final String CONFIGURATION_ELEMENT = "jppf-configuration";
	
	static final String RUNNABLE_ATTRIBUTE =  "runnable";
	
	static final String ADDRESS_ATTRIBUTE =  "address";
	
	static final String PARALLELTASK_ATTRIBUTE =  "parallelTaskNumber";
	
	static final String DATASOURCE_ATTRIBUTE =  "datasource";
	
	static final String DELIMITER_ATTRIBUTE =  "delimiter";
	
	static final String DELIMITER_STRING_ATTRIBUTE =  "delimiterString";
	
	static final String CHUNKABLE_DD_ATTRIBUTE =  "chunkableDataDescription";
	
	static final String MERGE_DD_ATTRIBUTE =  "mergedDataDescription";
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser#doParse(org.w3c.dom.Element, org.springframework.beans.factory.support.BeanDefinitionBuilder)
	 */
	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		// this will never be null since the schema explicitly requires that a value be supplied
		// it calls the utility to set the property
		Util.setProperty(element, builder, RUNNABLE_ATTRIBUTE);
		Util.setProperty(element, builder, ADDRESS_ATTRIBUTE);
		Util.setProperty(element, builder, PARALLELTASK_ATTRIBUTE, Integer.class);
		Util.setProperty(element, builder, DATASOURCE_ATTRIBUTE);
		Util.setProperty(element, builder, DELIMITER_ATTRIBUTE);
		Util.setProperty(element, builder, DELIMITER_STRING_ATTRIBUTE);
		Util.setProperty(element, builder, CHUNKABLE_DD_ATTRIBUTE);
		Util.setProperty(element, builder, MERGE_DD_ATTRIBUTE);
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#resolveId(org.w3c.dom.Element, org.springframework.beans.factory.support.AbstractBeanDefinition, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext)	throws BeanDefinitionStoreException {
		// checks if the user set own ID 
		String id = super.resolveId(element, definition, parserContext);
		if (StringUtils.hasText(id)){
			return id;
		}
		// if attribute ID is not set, creates a random ID
		return UUID.randomUUID().toString();
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser#getBeanClass(org.w3c.dom.Element)
	 */
	@Override
	protected Class<JPPFBean> getBeanClass(Element element) {
		return JPPFBean.class; 
	}
}