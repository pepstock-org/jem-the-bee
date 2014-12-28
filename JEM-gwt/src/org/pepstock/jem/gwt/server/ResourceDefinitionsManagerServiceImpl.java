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
package org.pepstock.jem.gwt.server;

import java.util.Collection;

import org.pepstock.jem.gwt.client.services.ResourceDefinitionsManagerService;
import org.pepstock.jem.gwt.server.services.ResourceDefinitionsManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.resources.definition.ResourceDescriptor;

/**
 * GWT service that provide methods to manage common resource definitions.
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class ResourceDefinitionsManagerServiceImpl extends DefaultManager implements ResourceDefinitionsManagerService {

	private static final long serialVersionUID = 1L;

	private transient ResourceDefinitionsManager resourceDefinitionsManager = null;
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.CustomResourceDefinitionsManagerService#getDescriptorOf(java.lang.String)
	 */
    @Override
    public ResourceDescriptor getDescriptorOf(String resourceName) throws JemException {
		checkIsEnable();
		initManager();
		try {
			return resourceDefinitionsManager.getDescriptorOf(resourceName);
		} catch (Exception e) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG055E, e);
			throw new JemException(e.getMessage());
		}
	}

    
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.CustomResourceDefinitionsManagerService#getAllResourceDescriptors()
	 */
    @Override
    public Collection<ResourceDescriptor> getAllResourceDescriptors() throws JemException {
		checkIsEnable();
		initManager();
		try {
			return resourceDefinitionsManager.getAllDescriptors();
		} catch (Exception e) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG055E, e);
			throw new JemException(e.getMessage());
		}
	}
    
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.CustomResourceDefinitionsManagerService#getAllResourceNames()
	 */
    @Override
    public Collection<String> getAllResourceNames() throws JemException {
		checkIsEnable();
		initManager();
		try {
			return resourceDefinitionsManager.getAllResourceNames();
		} catch (Exception e) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG055E, e);
			throw new JemException(e.getMessage());
		}
	}

	/**
     * Initializes a manager
     * @throws JemException if any exception occurs 
     */
	private synchronized void initManager() throws JemException {
		if (resourceDefinitionsManager == null) {
			try {
				resourceDefinitionsManager = new ResourceDefinitionsManager();
			} catch (Exception ex) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG055E, ex);
				throw new JemException(ex.getMessage());
			}
		}
	}

}
