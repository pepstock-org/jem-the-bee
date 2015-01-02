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
package org.pepstock.jem.node.resources.impl.jppf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Reference;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.annotations.Mode;
import org.pepstock.jem.annotations.ResourceMetaData;
import org.pepstock.jem.annotations.ResourceTemplate;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.definition.ResourceDefinitionException;
import org.pepstock.jem.node.resources.definition.XmlConfigurationResourceDefinition;

/**
 * Resource definition for a JPPF resource.<br> 
 * Sets metadata and xml template url to read in classpath
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
@ResourceMetaData(type = "jppf", description = "JPPF enables applications with large processing power requirements to be run on any number of computers, in order to dramatically reduce their processing time. This is done by splitting an application into smaller parts that can be executed simultaneously on different machines.")
@ResourceTemplate(value="org/pepstock/jem/node/resources/impl/jppf/JppfResourcesConfiguration.xml",
		mode = Mode.FROM_CLASSPATH)
public class JppfResourceDefinition extends XmlConfigurationResourceDefinition {

	private static final long serialVersionUID = 1L;
	
	// pattern use to check if the hosts are correctly defined: 
	// format = host:port
	private static final Pattern PATTERN = Pattern.compile("^\\s*(.*?):(\\d+)\\s*$");
	
	/**
	 * Empty constructor
	 */
	public JppfResourceDefinition() {
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.resources.custom.ResourceDefinition#getResourceReference()
	 */
	@Override
	public Reference getReference() {
		return new JppfReference();
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.resources.definition.ResourceDefinition#validateResource(org.pepstock.jem.node.resources.Resource)
	 */
	@Override
	public void validateResource(Resource resource) throws ResourceDefinitionException {
		// validates the properties
		validateResource(resource, JppfResourceKeys.PROPERTIES_ALL, JppfResourceKeys.PROPERTIES_ALL);
		
		// if here, everything is ofk
		// but we need to test the format of ADDRESSES. MUST be host:port,host:port,host:port
		String value = resource.getProperties().get(JppfResourceKeys.ADDRESSES).getValue();
		String[] hosts = StringUtils.split(value, ",");
		// scans all hosts checking if they are correct
		for (String host : hosts){
			Matcher m = PATTERN.matcher(host);
			if (!m.matches()) {
				throw new ResourceDefinitionException(NodeMessage.JEMC272E, JppfResourceKeys.ADDRESSES);
			}
		}
	}
}
