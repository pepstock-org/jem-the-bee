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
package org.pepstock.jem.node.executors.nodes;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.executors.DefaultExecutor;
import org.pepstock.jem.node.executors.ExecutorException;
import org.pepstock.jem.util.TimeUtils;

/**
 * Is the "Shutdown" command implementation.<br>
 * Runs inside the node and calls "System.exit" method to activate
 * the shutdown hook thread.
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class Shutdown extends DefaultExecutor<Boolean> {

	private static final long WAIT_BEFORE_EXIT = 5 * TimeUtils.SECOND;
	
	private static final long serialVersionUID = 1L;

	/** 
	 * N O T    U S E D
	 * @return execution result, always successful
	 * @throws Exception occurs if errors
	 */
	@Override
	public Boolean execute() throws ExecutorException {
		// creates a thead to call system.exit
		SystemExitCaller caller = new SystemExitCaller();
		caller.start();
		return Boolean.TRUE;
	}

	
	static class SystemExitCaller extends Thread{
		
		@Override
		public void run(){
			try {
				// wait 5 seconds and close
				Thread.sleep(WAIT_BEFORE_EXIT);
			} catch (InterruptedException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
			System.exit(1);	
		}
		
	}
}