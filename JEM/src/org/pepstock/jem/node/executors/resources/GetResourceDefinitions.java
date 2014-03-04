/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
import org.pepstock.jem.node.resources.custom.ResourceDefinition;

/**
 * Returns all resources definitions in JEM.
 * 
 * @author Marco Cuccato
 * @version 1.4
 */
public class GetResourceDefinitions extends DefaultExecutor<Collection<ResourceDefinition>> {

	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.node.executors.DefaultExecutor#execute()
	 */
	@Override
	public Collection<ResourceDefinition> execute() throws ExecutorException {
		List<ResourceDefinition> toReturn = new ArrayList<ResourceDefinition>(); 
		for (ResourceDefinition rd : Main.CUSTOM_RESOURCE_DEFINITION_MANAGER.getAllCustomResourceDefinitions()) {
			toReturn.add(rd);
		}
		return toReturn;
	}

}
