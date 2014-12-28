/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.panels.resources.inspector.widgets;

import org.pepstock.jem.gwt.client.panels.resources.inspector.ResourcesPropertiesPanel;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.definition.ResourceDescriptor;
import org.pepstock.jem.node.resources.definition.ResourcePartDescriptor;

/**
 * Base abstract class for custom resource property panel. Has support for {@link ResourceDescriptor}.
 * <code>D</code> is the type of configuration descriptor
 * @author Marco "Fuzzo" Cuccato
 * @param <D> 
 *
 */
public abstract class CommonResourcePropertiesPanel<D extends ResourcePartDescriptor> extends ResourcesPropertiesPanel {
	
	protected D descriptor = null;
	
	/**
	 * Builds the panel
	 * @param resource the underlying resource
	 * @param descriptor the descriptor, contains rendering information
	 * @param hasComplexWidget 
	 */
	public CommonResourcePropertiesPanel(Resource resource, D descriptor, boolean hasComplexWidget) {
		super(resource, hasComplexWidget);
		this.descriptor = descriptor;
	}

	@Override
	public abstract void initializeResource();

	/**
	 * @return the resource descriptor
	 */
	public D getDescriptor() {
		return descriptor;
	}
	
}
