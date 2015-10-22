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
package org.pepstock.jem.node.https;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.pepstock.jem.Job;
import org.pepstock.jem.commands.util.HttpUtil;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;
import org.pepstock.jem.node.events.JobLifecycleListener;
import org.pepstock.jem.util.CharSet;
import org.pepstock.jem.util.Parser;

/**
 * Is JOB lifecycle listener for submitters not java written which are listening in a HTTP port 
 * to get result and output of the job.
 *  
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class HttpJobListener implements JobLifecycleListener {
	
	private static final int ELEMENT_1 = 0;

	private static final int ELEMENT_2 = 1;

	private static final int ELEMENT_3 = 2;
	

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.events.JobLifecycleListener#init(java.util.Properties)
	 */
	@Override
	public void init(Properties properties) {
		// nop
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.events.JobLifecycleListener#queued(org.pepstock.jem.Job)
	 */
	@Override
	public void queued(Job job) {
		// nop
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.events.JobLifecycleListener#running(org.pepstock.jem.Job)
	 */
	@Override
	public void running(Job job) {
		// nop
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.events.JobLifecycleListener#ended(org.pepstock.jem.Job)
	 */
	@Override
	public void ended(Job job) {
		// if job is not waiting, return
		if (job.isNowait()){
			return;
		}
		// if there is the IP address
		// to reply
		if (job.getInputArguments() != null && job.getInputArguments().contains(SubmitHandler.JOB_SUBMIT_IP_ADDRESS_KEY)){
			// creates the URL
			// the path is always the JOBID
			String url = "http://"+job.getInputArguments().get(ELEMENT_1)+":"+job.getInputArguments().get(ELEMENT_2)+"/"+job.getId();
			// creates a HTTP client
			CloseableHttpClient httpclient = null;
			try {
				httpclient = HttpUtil.createHttpClient(url);
				// gets if printoutput is required
				boolean printOutput = Parser.parseBoolean(job.getInputArguments().get(ELEMENT_3), false);
				// creates the response
				// ALWAYS the return code
				StringBuilder sb = new StringBuilder();
				sb.append(job.getResult().getReturnCode()).append("\n");
				// if client needs output
				// it adds the output of job
				if (printOutput){
					File file = Main.getOutputSystem().getMessagesLogFile(job);
					sb.append(FileUtils.readFileToString(file));
				}
				// creates the HTTP entity
				StringEntity entity = new StringEntity(sb.toString(), ContentType.create("text/plain", CharSet.DEFAULT_CHARSET_NAME));

				// prepares POST request and basic response handler
				HttpPost httppost = new HttpPost(url);
				httppost.setEntity(entity);
				// executes and no parsing
				// result must be only a string
				CloseableHttpResponse response = httpclient.execute(httppost);
				response.close();
				return;
			} catch (Exception e) {
				LogAppl.getInstance().emit(NodeMessage.JEMC289E, e, job.getInputArguments().get(ELEMENT_1)+":"+job.getInputArguments().get(ELEMENT_2), job.toString());
			} finally {
				// close http client
				if (httpclient != null){
					try {
						httpclient.close();
					} catch (IOException e) {
						LogAppl.getInstance().ignore(e.getMessage(), e);
					}
				}
			}
		}
	}
}