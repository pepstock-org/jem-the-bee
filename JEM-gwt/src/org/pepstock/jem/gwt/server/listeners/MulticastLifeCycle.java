/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Simone "Busy" Businaro
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
package org.pepstock.jem.gwt.server.listeners;

import org.pepstock.jem.gwt.server.commons.SharedObjects;
import org.pepstock.jem.gwt.server.security.JemCacheManager;

/**
 * Is the client life cycle listener for the multicast service. Here the
 * business logic for each state changes
 * 
 * @author Simone "Busy" Businaro
 * @version 1.3
 * 
 */
public class MulticastLifeCycle extends LifeCycle {

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.server.listeners.LifeCycle#handleShuttingDown()
	 */
    @Override
    public void shuttingDown() {
    	SharedObjects.getInstance().setDataClusterAvailable(false);	    
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.server.listeners.LifeCycle#handleShutDown()
	 */
    @Override
    public void shuttedDown() {
		JemCacheManager.updateJemCache();
    }

}
