/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015  Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.services;

import java.util.Collection;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.persistence.RedoStatement;
import org.pepstock.jem.node.stats.LightSample;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service. Provides all statistics actions
 */
@RemoteServiceRelativePath(Services.STATS)
public interface StatisticsManagerService extends RemoteService {

	/**
	 * Returns all statistics loaded in JEM environment
	 * 
	 * @return collection of statistics samples
	 * @throws JemException
	 *             if error occurs
	 * 
	 */
	Collection<LightSample> getSamples() throws JemException;

	/**
	 * Returns last sample about JEM env statistics
	 * 
	 * @return last sample loaded
	 * @throws JemException
	 *             if error occurs
	 * 
	 */
	LightSample getCurrentSample() throws JemException;

	/**
	 * @param resorceKey
	 * @return
	 * @throws JemException
	 */
	String displayRequestors(String resorceKey) throws JemException;

	/**
	 * Returns all redo statements loaded in JEM environment waiting for
	 * database
	 * 
	 * @return collection of redo statements
	 * @throws JemException
	 *             if error occurs
	 * 
	 */
	Collection<RedoStatement> getAllRedoStatements() throws JemException;
}