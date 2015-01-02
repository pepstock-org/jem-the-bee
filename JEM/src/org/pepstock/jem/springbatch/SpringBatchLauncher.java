/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015  Andrea "Stock" Stocchero
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
package org.pepstock.jem.springbatch;

import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.LogAppl;
import org.springframework.batch.core.launch.support.CommandLineJobRunner;

/**
 * Is the custom wrapper at the launcher of SpringBatch.
 * <br>
 * It wraps the <code>CommandLineJobRunner</code> of SpringBacth, because it must
 * initialize the LOG4J.
 * 
 *  @author Andrea "Stock" Stocchero
 *  @version 1.3
 */
public class SpringBatchLauncher {

	/**
	 * To avoid any instantiation
	 */
	private SpringBatchLauncher() {

	}

	/**
	 * Main method, called by JEM when a SpringBatch job must be executed.
	 * 
	 * @see org.springframework.batch.core.launch.support.CommandLineJobRunner
	 * @param args arguments of <code>CommandLineJobRunner</code>
	 * @throws JemException if any exception occurs
	 */
	public static void main(String[] args) throws JemException  {
		// creates log instance
		LogAppl.getInstance();
		try {
			// launches SPRING BATCH
			CommandLineJobRunner.main(args);
		} catch (Exception e) {
			throw new JemException(e);
		}
	}
}