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
package org.pepstock.jem.node.executors.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;
import org.pepstock.jem.node.resources.definition.ResourceDefinition;
import org.pepstock.jem.node.resources.definition.ResourceDescriptor;

/**
 * Returns all resources definitions in JEM.
 * 
 * @author Marco Cuccato
 * @version 1.4
 */
public class GetResourceDescriptors extends DefaultExecutor<Collection<ResourceDescriptor>> {

	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#execute()
	 */
	@Override
	public Collection<ResourceDescriptor> execute() throws ExecutorException {
		List<ResourceDescriptor> toReturn = new ArrayList<ResourceDescriptor>(); 
		for (ResourceDefinition rd : Main.RESOURCE_DEFINITION_MANAGER.getAllResourceDefinitions()) {
			toReturn.add(rd.getDescriptor());
		}
		return toReturn;
	}

}
