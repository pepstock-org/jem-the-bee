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
package org.pepstock.jem.gwt.client.services;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.node.About;
import org.pepstock.jem.util.Numbers;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Andrea "Stock" Stocchero
 * 
 */
@RemoteServiceRelativePath(Services.INFO)
public interface InfoService extends RemoteService {

	/**
	 * Holds the info indexes
	 * @author Marco "Fuzzo" Cuccato
	 */
	public enum Indexes {
		/**
		 * Length of array of data to return
		 */
		INFO_SIZE(Numbers.N_5),

		/**
		 * Index of array for JEM group
		 */
		 NAME(Numbers.N_0),

		/**
		 * Index of array for count of nodes
		 */
		NODES_COUNT(Numbers.N_1),

		/**
		 * Index of array for job in execution
		 */
		EXECUTION_JOB_COUNT(Numbers.N_2),

		/**
		 * Index of array for JEM group started time
		 */
		STARTED_TIME(Numbers.N_3),

		/**
		 * Index of array for JEM group time
		 */
		CURRENT_TIME(Numbers.N_4),

		/**
		 * Length of array of data to return
		 */
		INFO_LOGO_SIZE(Numbers.N_2),

		/**
		 * Index of array for JEM group
		 */
		URL(Numbers.N_0),

		/**
		 * Index of array for count of nodes
		 */
		LINK(Numbers.N_1);
		
		private int index;
		
		private Indexes(int index) {
			this.index = index;
		}
		
		/**
		 * @return the index
		 */
		public int getIndex() {
			return index;
		}
	}

	/**
	 * @return
	 * @throws JemException
	 */
	String[] getEnvironmentInformation() throws JemException;

	/**
	 * 
	 * @return
	 * @throws JemException
	 */
	String[] getLogoURL() throws JemException;

	/**
	 * Returns the information about build time, version and licenses
	 * 
	 * @return about instance with all info inside
	 * @throws JemException
	 *             if any exception occurs
	 */
	About getAbout() throws JemException;

}