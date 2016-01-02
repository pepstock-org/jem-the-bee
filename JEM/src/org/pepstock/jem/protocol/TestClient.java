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
package org.pepstock.jem.protocol;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.pepstock.jem.Job;
import org.pepstock.jem.PreJob;
import org.pepstock.jem.log.JemException;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class TestClient {

	/**
	 * @param args
	 * @throws JemException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws JemException, UnknownHostException {
		ClientConfig config = new ClientConfig();
		config.setGroup("TEST-Env");
		config.setPassword("jem_password");
		config.setUser("Stock");
		
		config.addAddresses("192.168.1.16:6010");
		
//		config.addAddresses("169.254.38.199:18567", InetAddress.getLocalHost().getHostAddress()+":8888", InetAddress.getLocalHost().getHostAddress()+":8888"
//				, InetAddress.getLocalHost().getHostAddress()+":8888", InetAddress.getLocalHost().getHostAddress()+":8888", InetAddress.getLocalHost().getHostAddress()+":8888");
//		
//		Client client = new Client(config);
//		
//		client.start();
		
		Future<Client> future = Client.open(config);
		
		try {
			Client client = future.get();
			
			System.err.println("Client started");
			
			Future<String> futureJobId = client.getJobId();
			
			String id = futureJobId.get();
			
			System.err.println("Da future: "+id);
			
			PreJob job = new PreJob();
			Job j = new Job();
			job.setJob(j);
			
			job.setId(id);
			j.setId(id);
			Future<Job> futureSubmitJob = client.submit(job);
			
			Job newJob = futureSubmitJob.get();
			
			System.err.println("Da future: "+newJob.getResult().getExceptionMessage());

			Future<String> futureContent = client.getOutput(newJob);
			
			String content = futureContent.get();
			
			System.err.println(content);
		
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.err.println("SONO QUI!");
	}

}
