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
package org.pepstock.jem.gwt.server;

import java.util.Collection;

import org.pepstock.jem.gwt.client.services.StatisticsManagerService;
import org.pepstock.jem.gwt.server.services.StatisticsManager;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.persistence.RedoStatement;
import org.pepstock.jem.node.stats.LightSample;

/**
 *  Is GWT server service which can provide all methods to get statistics information.
 *  
 * @author Andrea "Stock" Stocchero
 * 
 */
public class StatisticsManagerServiceImpl extends DefaultManager implements StatisticsManagerService {

	private static final long serialVersionUID = 1L;

	private transient StatisticsManager statsManager = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.StatisticsManagerService#getSamples
	 * ()
	 */
	@Override
	public Collection<LightSample> getSamples() throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (statsManager == null){
			initManager();
		}
		try {
			return statsManager.getSamples();
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG042E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.StatisticsManagerService#getLastSample
	 * ()
	 */
	@Override
	public LightSample getCurrentSample() throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (statsManager == null){
			initManager();
		}
		try {
			return statsManager.getCurrentSample();
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG042E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.gwt.client.services.StatisticsManagerService#getLastSample
	 * ()
	 */
	@Override
	public String displayRequestors(String resorceKey) throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (statsManager == null){
			initManager();
		}
		try {
			return statsManager.displayRequestors(resorceKey);
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG042E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.services.StatisticsManagerService#getAllRedoStatements()
	 */
    @Override
    public Collection<RedoStatement> getAllRedoStatements() throws JemException {
		// check if JEM is available
		// if not, throws an exception
		checkIsEnable();
		// the manager is null
		// creates a new one
		if (statsManager == null){
			initManager();
		}
		try {
			return statsManager.getAllRedoStatements();
		} catch (Exception ex) {
			LogAppl.getInstance().emit(UserInterfaceMessage.JEMG042E, ex);
			// creates a new Exception to avoid to try
			// to serialize Exception (like hazelcast ones) which are not
			// serializable
			throw new JemException(ex.getMessage());
		}
    }
   
	/**
     * Initializes a manager
     * @throws JemException if any exception occurs 
     */
	private synchronized void initManager() throws JemException {
		if (statsManager == null) {
			try {
				statsManager = new StatisticsManager();
			} catch (Exception ex) {
				LogAppl.getInstance().emit(UserInterfaceMessage.JEMG042E, ex);
				// creates a new Exception to avoid to try
				// to serialize Exception (like hazelcast ones) which are not
				// serializable
				throw new JemException(ex.getMessage());
			}
		}

	}



}