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

import java.util.UUID;

import org.pepstock.jem.springbatch.tasks.Lock;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Specific parser for <lock> tag, to create lock bean.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
public class LockDefinitionParser extends AbstractSingleBeanDefinitionParser {
	
	static final String LOCK_ELEMENT = "lock";
	
	static final String NAME_ATTRIBUTE = "name";	
	
	private boolean isNested = false;
	
	/**
	 * Creates the object knowing that is not nested in any other element.<br>
	 * Usually called by Spring engine
	 */
	public LockDefinitionParser() {
		this(false);
	}

	/**
	 * Creates the object being aware that is nested in <tasklet> tag.<br>
	 * Called with <code>true</code> ONLY from Tasklet definition parser. 
	 * 
	 * @param nested <code>true</code> if nested
	 */
	public LockDefinitionParser(boolean nested) {
		this.isNested = nested;
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
		// if attribute ID is not set, is correct ONLY if is nested
		// in this case creates a random ID
		if (isNested){
			return UUID.randomUUID().toString();
		}
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser#doParse(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext, org.springframework.beans.factory.support.BeanDefinitionBuilder)
	 */
	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		Util.setProperty(element, builder, NAME_ATTRIBUTE);
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser#getBeanClass(org.w3c.dom.Element)
	 */
	@Override
	protected Class<Lock> getBeanClass(Element element) {
		return Lock.class; 
	}
}
