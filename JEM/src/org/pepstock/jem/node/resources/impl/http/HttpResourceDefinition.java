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
package org.pepstock.jem.node.resources.impl.http;

import javax.naming.Reference;

import org.pepstock.jem.annotations.Mode;
import org.pepstock.jem.annotations.ResourceMetaData;
import org.pepstock.jem.annotations.ResourceTemplate;
import org.pepstock.jem.node.resources.definition.XmlConfigurationResourceDefinition;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
@ResourceMetaData(type = "http", description = "HTTP resource")
@ResourceTemplate(value="org/pepstock/jem/node/resources/impl/http/HttpResourcesConfiguration.xml",
		mode = Mode.FROM_CLASSPATH)
public class HttpResourceDefinition extends XmlConfigurationResourceDefinition {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Empty constructor
	 */
	public HttpResourceDefinition() {
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.resources.custom.ResourceDefinition#getResourceReference()
	 */
	@Override
	public Reference getReference() {
		return new HttpReference();
	}

}
