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

import org.pepstock.jem.springbatch.JemBean;
import org.pepstock.jem.springbatch.SpringBatchKeys;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Specific parser for <configuration> tag, to create JemBean bean, with all properties to execute the job.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
public class JemBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
	
	static final String CONFIGURATION_ELEMENT = "configuration";
	
	static final String ENVIRONMENT_ATTRIBUTE =  "environment";
	
	static final String DOMAIN_ATTRIBUTE =  "domain";
	
	static final String JOBNAME_ATTRIBUTE =  "jobName";
	
	static final String AFFINITY_ATTRIBUTE =  "affinity";
	
	static final String MEMORY_ATTRIBUTE =  "memory";
	
	static final String PRIORITY_ATTRIBUTE =  "priority";
	
	static final String HOLD_ATTRIBUTE =  "hold";
	
	static final String EMAIL_ATTRIBUTE =  "emailNotificationAddresses";
	
	static final String USER_ATTRIBUTE =  "user";
	
	static final String CLASSPATH_ATTRIBUTE =  "classPath";
	
	static final String LOCKINGSCOPE_ATTRIBUTE =  "lockingScope";
	
	/**
	 * Constant to define options of Springbatch
	 */
	public static final String OPTIONS_ATTRIBUTE =  "options";

	/**
	 * Constant to define parameters of Springbatch
	 */
	public static final String PARAMETERS_ATTRIBUTE =  "parameters";

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser#doParse(org.w3c.dom.Element, org.springframework.beans.factory.support.BeanDefinitionBuilder)
	 */
	@Override
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		// this will never be null since the schema explicitly requires that a value be supplied
		Util.setProperty(element, builder, ENVIRONMENT_ATTRIBUTE);
		Util.setProperty(element, builder, DOMAIN_ATTRIBUTE);
		Util.setProperty(element, builder, JOBNAME_ATTRIBUTE);
		Util.setProperty(element, builder, AFFINITY_ATTRIBUTE);
		Util.setProperty(element, builder, MEMORY_ATTRIBUTE, Integer.class);
		Util.setProperty(element, builder, PRIORITY_ATTRIBUTE, Integer.class);
		Util.setProperty(element, builder, HOLD_ATTRIBUTE, Boolean.class);
		Util.setProperty(element, builder, EMAIL_ATTRIBUTE);
		Util.setProperty(element, builder, USER_ATTRIBUTE);
		Util.setProperty(element, builder, CLASSPATH_ATTRIBUTE);
		Util.setProperty(element, builder, LOCKINGSCOPE_ATTRIBUTE);
		Util.setProperty(element, builder, OPTIONS_ATTRIBUTE);
		Util.setProperty(element, builder, PARAMETERS_ATTRIBUTE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.AbstractBeanDefinitionParser#resolveId(org.w3c.dom.Element, org.springframework.beans.factory.support.AbstractBeanDefinition, org.springframework.beans.factory.xml.ParserContext)
	 */
	@Override
	protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext)	throws BeanDefinitionStoreException {
		// ALWAYS jem.bean
		return SpringBatchKeys.BEAN_ID;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser#getBeanClass(org.w3c.dom.Element)
	 */
	@Override
	protected Class<JemBean> getBeanClass(Element element) {
		return JemBean.class; 
	}
}
