/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Simone "Busy" Businaro
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

import org.pepstock.jem.commands.LocalHostSubmit;
import org.pepstock.jem.commands.SubmitParameters;
import org.pepstock.jem.commands.SubmitResult;
import org.pepstock.jem.node.NodeMessage;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Simone "Busy" Businaro
 * @version 1.0
 * 
 *          This class represent a Quartz Job to execute the jem command
 *          SubmitParameters.
 * @see org.pepstock.jem.commands.LocalHostSubmit
 */
public class JobLocalHostSubmit extends PrivateKeyJclJob {

	private String jemEnv = null;

	private String jemPort = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LocalHostSubmit submit = new LocalHostSubmit();
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
		if (jemEnv != null) {
			list.add("-" + SubmitParameters.ENV.getName());
			list.add(jemEnv);
		}
		if (jemPort != null) {
			list.add("-" + SubmitParameters.PORT.getName());
			list.add(jemPort);
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * @param jemEnv the jemEnv to set.
	 *            <p>
	 *            It is called by quartz framework before the execute method but
	 *            JobDetail must include this property: e.g.
	 *            <p>
	 *            JobDetail job = JobBuilder.newJob(JobSubmitParameters.class)<br>
	 *            .withIdentity("job1", "group1")<br>
	 *            <b>.usingJobData("jemEnv", "C0-CRM")</b><br>
	 *            .usingJobData("jemPort", "5710")<br>
	 *            .usingJobData("jclUrl",
	 *            "file:/C:/Users/Simone/Desktop/JEM/test/ECHO.xml")<br>
	 *            .usingJobData("jclType", "ant")<br>
	 *            .build();<br>
	 */
	public void setJemEnv(String jemEnv) {
		this.jemEnv = jemEnv;
	}

	/**
	 * @param jemPort the jemPort to set.
	 *            <p>
	 *            It is called by quartz framework before the execute method but
	 *            JobDetail must include this property: e.g.
	 *            <p>
	 *            JobDetail job = JobBuilder.newJob(JobSubmitParameters.class)<br>
	 *            .withIdentity("job1", "group1")<br>
	 *            .usingJobData("jemEnv", "C0-CRM")<br>
	 *            <b>.usingJobData("jemPort", "5710")</b><br>
	 *            .usingJobData("jclUrl",
	 *            "file:/C:/Users/Simone/Desktop/JEM/test/ECHO.xml")<br>
	 *            .usingJobData("jclType", "ant")<br>
	 *            .build();<br>
	 */
	public void setJemPort(String jemPort) {
		this.jemPort = jemPort;
	}

	/**
	 * @return the jemEnv
	 */
	public String getJemEnv() {
		return jemEnv;
	}

	/**
	 * @return the jemPort
	 */
	public String getJemPort() {
		return jemPort;
	}
}