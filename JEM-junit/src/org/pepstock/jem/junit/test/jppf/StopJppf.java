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
package org.pepstock.jem.junit.test.jppf;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jppf.management.JMXDriverConnectionWrapper;
import org.jppf.management.JMXNodeConnectionWrapper;
import org.pepstock.jem.node.tasks.platform.CurrentPlatform;
import org.pepstock.jem.node.tasks.platform.Platform;

/**
 * 
 * @author Simone "Busy" Businaro
 * @version 1.4
 */
public class StopJppf extends TestCase{
	
	private Platform platform = null;

	/**
	 * 
	 * @throws Exception
	 */
	public void testStopJppf() throws Exception{
		try {
			platform = CurrentPlatform.getInstance();
			JMXDriverConnectionWrapper jmxDriver =	new JMXDriverConnectionWrapper("localhost", 11198, false);
			jmxDriver.connectAndWait(5000L);
			close("driver");
			
			JMXNodeConnectionWrapper wrapper = new JMXNodeConnectionWrapper("localhost", 12001, false);
			wrapper.connectAndWait(5000L);			
			close("node");
//			System.out.println("====================================\nStop JPPF driver\n====================================");
//			// connect to the driver's JMX server
//			JMXDriverConnectionWrapper jmxDriver =
//					new JMXDriverConnectionWrapper("localhost", 11198, false);
////			jmxDriver.connectAndWait(5000L);
//			jmxDriver.connect();
//			jmxDriver.restartShutdown(5000L, -1L);
//			
//			jmxDriver.getAllJobIds();
//			
//			System.err.println(jmxDriver.getDisplayName());
//			
//			System.out.println("====================================\nStop JPPF node\n====================================");
//			JMXNodeConnectionWrapper wrapper = new JMXNodeConnectionWrapper("localhost", 12001, false);
//			wrapper.connectAndWait(5000L);
//			// stop the node
//			wrapper.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void close(String type) throws IOException{
		System.out.println("====================================\nStop JPPF "+type+"\n====================================");
		File dir = new File("./jppf/"+type);
		File log = new File(dir, "jppf-"+type+".log");
		
		List<String> rows = FileUtils.readLines(log);
		
		String toSearch = "starting "+type+" with PID=";
		
		for (String row : rows){
			if (StringUtils.contains(row, toSearch)){
				String pids = StringUtils.substringBetween(row, toSearch, ",");
				long pid = Long.parseLong(pids);
				platform.kill(pid, null, true, false);
				System.out.println("kill process "+type+":"+pid);
				return;
			}
		}


	}

}
