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
package org.pepstock.jem.junit.test.jppf;

import junit.framework.TestCase;

import org.jppf.management.JMXDriverConnectionWrapper;
import org.jppf.management.JMXNodeConnectionWrapper;

/**
 * 
 * @author Simone "Busy" Businaro
 * @version 1.4
 */
public class StopJppf extends TestCase{
	
	/**
	 * 
	 * @throws Exception
	 */
	public void testStopJppf() throws Exception{
		try {
			
			System.out.println("====================================\nStop JPPF node\n====================================");
			JMXNodeConnectionWrapper wrapper = new JMXNodeConnectionWrapper("localhost", 12001, false);
			wrapper.connectAndWait(5000L);
			// stop the node
			wrapper.shutdown();

			System.out.println("====================================\nStop JPPF driver\n====================================");
			// connect to the driver's JMX server
			JMXDriverConnectionWrapper jmxDriver =
					new JMXDriverConnectionWrapper("localhost", 11198, false);
			jmxDriver.connectAndWait(5000L);
			jmxDriver.restartShutdown(0L, -1L);
		} catch (Exception e) {
		}
	}

}
