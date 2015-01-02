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
package org.pepstock.jem.jbpm.tasks;

import org.pepstock.catalog.AbstractDataDescription;

/**
 * Is a container of dataset (one or more), addressable by name both a java code
 * (JNDI) and executable command (by environment variables). It could be defined
 * without datasets when is considered a sysout.<br>
 * There are 3 kinds of data description.<br>
 * <br>
 * <b>Sysout</b>: a file used in output way and in the same container of job-log
 * and message-log.<br>
 * Example:
 * <code>SYSOUT,DISP=NEW</code>
 * <br>
 * <b>Single dataset</b>: a single dataset is defined which must accessible with
 * dispositions.<br>
 * Example: <br>
 * <code>DSN=nas.rx.jemtest(0),DISP=SHR<br>
 * <b>Multi dataset</b>: more than one dataset are defined which must accessible
 * ONLY in SHR mode.<br>
 * Example: DSN=(@@temp;nas.rx.jemtest(0)),DISP=SHR<br>
 * <br>
 * Defines also how to access to file.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 * 
 */
public class DataDescription extends AbstractDataDescription<DataSet> {

	private static final long serialVersionUID = 1L;

	/**
	 * Empty constructor
	 */
	public DataDescription() {
	}

	/**
	 * helpful to add a single dataset
	 * @param ds dataset definition
	 */
	public void addDataSet(DataSet ds) {
		getDatasets().add(ds);
	}
}