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
package org.pepstock.jem.springbatch.xml;

import org.pepstock.jem.springbatch.tasks.utilities.LauncherTasklet;
import org.pepstock.jem.springbatch.xml.TaskletFactoryBean;

/**
 * Factory bean for complex XML element for LAUNCHER bean tasklet.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class LauncherTaskletFactoryBean extends TaskletFactoryBean {

	/* (non-Javadoc)
	 * @see org.pepstock.jem.springbatch4.xml.TaskletFactoryBean#getObject()
	 */
	@Override
	public Object getObject() throws Exception {
		// cast the tasklet and return it.
		return (LauncherTasklet)super.getObject();
	}
}