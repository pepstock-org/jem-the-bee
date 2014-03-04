/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Simone "Busy" Businaro
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
package org.pepstock.jem.quartz;

import java.util.List;

import org.pepstock.jem.commands.Submit;
import org.pepstock.jem.commands.SubmitParameters;
import org.pepstock.jem.commands.SubmitResult;
import org.pepstock.jem.node.NodeMessage;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 *          This class represent a Quartz Job to execute the jem command Submit.
 * @see org.pepstock.jem.commands.Submit
 */
public class JobSubmit extends PrivateKeyJclJob {

	private String hostForJemWeb = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Submit submit = new Submit();
		SubmitResult sr = submit.execute(createArguments());
		if (sr.getRc() != 0) {
			throw new JobExecutionException(NodeMessage.JEMC021I.toMessage().getFormattedMessage("", sr.getRc()));
		}
	}

	/**
	 * 
	 * @return the arguments needed to start LocalHostSubmit
	 */
	private String[] createArguments() {
		List<String> list = super.createArgs();
		if (hostForJemWeb != null) {
			list.add("-" + SubmitParameters.HOST.getName());
			list.add(hostForJemWeb);
		}
		return list.toArray(new String[list.size()]);
	}


	/**
	 * @param hostForJemWeb the hostForJemWeb to set
	 *            <p>
	 *            It is called by quartz framework before the execute method but
	 *            JobDetail must include this property: e.g.
	 *            <p>
	 *            JobDetail job = JobBuilder.newJob(JobLocalHostSubmit.class)<br>
	 *            .withIdentity("job1", "group1")<br>
	 *            <b>.usingJobData("hostForJemWeb",
	 *            "http://localhost:8080/jem_gwt")</b><br>
	 *            .build();<br>
	 */
	public void setHostForJemWeb(String hostForJemWeb) {
		this.hostForJemWeb = hostForJemWeb;
	}

	/**
	 * @return the hostForJemWeb
	 */
	public String getHostForJemWeb() {
		return hostForJemWeb;
	}
}