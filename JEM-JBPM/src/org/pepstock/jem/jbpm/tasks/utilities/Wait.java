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
package org.pepstock.jem.jbpm.tasks.utilities;

import java.util.Map;

import org.pepstock.jem.jbpm.tasks.JemWorkItem;
import org.pepstock.jem.util.Parser;
import org.pepstock.jem.util.TimeUtils;

/**
 * JEM work item that waits for a certain amount of seconds. Is able to get how many seconds to wait
 * by a JBPM parameter.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class Wait implements JemWorkItem {
	
	private static final String SECONDS_PARM_KEY = "jem.workItem.wait.seconds";

	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.jbpm.tasks.JemWorkItem#execute(java.util.Map)
	 */
	@Override
	public int execute(Map<String, Object> parameters) throws Exception {
		int seconds = 0;
		// args are null, second sets to 0
		Object args = parameters.get(SECONDS_PARM_KEY);
		if (args != null){
			seconds = Parser.parseInt(args.toString(), 0);
		}
		// if seconds == 0, waits forever
		Object lock = new Object();
		synchronized (lock) {
			if (seconds > 0){
				lock.wait(seconds * TimeUtils.SECOND);
			} else{
				lock.wait();
			}
		}
		return 0;
	}

}
