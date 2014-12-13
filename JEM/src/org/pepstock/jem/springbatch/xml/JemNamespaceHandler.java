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

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Registers all parser for different XML element and then beans.<br>
 * This is the class which is defined in extended XML authoring of Springbatch
 * to define all XML parser for the custom elements.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
public class JemNamespaceHandler extends NamespaceHandlerSupport {

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.xml.NamespaceHandler#init()
	 */
	@Override
	public void init() {
		// register all parser for all main elements
		registerBeanDefinitionParser(JemBeanDefinitionParser.CONFIGURATION_ELEMENT, new JemBeanDefinitionParser());
		registerBeanDefinitionParser(LockDefinitionParser.LOCK_ELEMENT, new LockDefinitionParser());
		registerBeanDefinitionParser(DataDescriptionDefinitionParser.DATA_DESCRIPTION_ELEMENT, new DataDescriptionDefinitionParser());
		registerBeanDefinitionParser(DataSourceDefinitionParser.DATA_SOURCE_ELEMENT, new DataSourceDefinitionParser());
		registerBeanDefinitionParser(TaskletDefinitionParser.TASKLET_ELEMENT, new TaskletDefinitionParser());
		registerBeanDefinitionParser(ItemReaderDefinitionParser.ITEM_READER_ELEMENT, new ItemReaderDefinitionParser());
		registerBeanDefinitionParser(ItemWriterDefinitionParser.ITEM_WRITER_ELEMENT, new ItemWriterDefinitionParser());
		registerBeanDefinitionParser(JPPFBeanDefinitionParser.CONFIGURATION_ELEMENT, new JPPFBeanDefinitionParser());
		registerBeanDefinitionParser(JPPFTaskletDefinitionParser.JPPF_TASKLET_ELEMENT, new JPPFTaskletDefinitionParser());
	}
}