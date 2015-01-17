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
package org.pepstock.jem.junit.init.submitters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.pepstock.jem.commands.SubmitParameters;
import org.pepstock.jem.commands.SubmitResult;
import org.pepstock.jem.junit.init.Submitter;
import org.pepstock.jem.node.NodeMessageException;
import org.pepstock.jem.node.tasks.platform.CurrentPlatform;
import org.pepstock.jem.node.tasks.shell.Shell;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public abstract class AbstractSubmitter implements Callable<SubmitResult> {
	
	private String jcl;

	private String type;

	private boolean wait;

	private boolean printout;

	private Submitter selectedSubmitter;

	/**
	 * @param selectedSubmitter 
	 * @param jcl 
	 * @param type 
	 * @param wait 
	 * @param printout 
	 * 
	 */
	public AbstractSubmitter(Submitter selectedSubmitter, String jcl, String type,
			boolean wait, boolean printout) {
		this.selectedSubmitter = selectedSubmitter;
		this.jcl = jcl;
		this.type = type;
		this.wait = wait;
		this.printout = printout;
	}

	/**
	 * @return the jcl
	 */
	public String getJcl() {
		return jcl;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the wait
	 */
	public boolean isWait() {
		return wait;
	}

	/**
	 * @return the printout
	 */
	public boolean isPrintout() {
		return printout;
	}

	/**
	 * @return the selectedSubmitter
	 */
	public Submitter getSelectedSubmitter() {
		return selectedSubmitter;
	}
	
	/**
	 * 
	 * @param args
	 */
	void addSpecificArguments(List<String> args){
		// nothing
	}

	/**
	 * 
	 * @param clazz
	 * @param jcl
	 * @param type
	 * @param wait
	 * @param printout
	 * @return
	 */
	String[] getArguments() {
		List<String> list = new ArrayList<String>();
		String paramJcl = SubmitParameters.JCL.getName();
		String paramType = SubmitParameters.TYPE.getName();
		String paramWait = SubmitParameters.WAIT.getName();
		
		for (int i = 0; i < selectedSubmitter.getParams().size(); i++) {
			list.add("-" + selectedSubmitter.getParams().get(i).getName());
			list.add(selectedSubmitter.getParams().get(i).getValue());
		}
		addSpecificArguments(list);
		
		list.add("-" + paramJcl);
		list.add(jcl);
		list.add("-" + paramType);
		list.add(type);
		list.add("-" + paramWait);
		list.add(String.valueOf(wait));
		return list.toArray(new String[list.size()]);
	}
	
	/**
	 * 
	 * @param command
	 * @param args
	 * @return
	 * @throws NodeMessageException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	int launch(String command, String[] args) throws NodeMessageException, IOException, InterruptedException{
		String osCommand = null;
		if (SystemUtils.IS_OS_WINDOWS){
			osCommand = command+".cmd";
		} else {
			osCommand = command+".sh";
		}
		
		Process process = null;
		try {
			File logFile = File.createTempFile("junit", "log");
			
			String redirect = "> "+ FilenameUtils.normalize(logFile.getAbsolutePath(), true)+" 2>&1";
			StringBuilder sb = new StringBuilder(osCommand);
			for (String arg: args){
				sb.append(" ").append(arg);
			}
			
			System.err.println(sb.toString());
			
			sb.append(" ").append(redirect);
			
			// create a process builder
			ProcessBuilder builder = new ProcessBuilder();
			Shell shell = CurrentPlatform.getInstance().getShell();
			
			builder.command(shell.getName(), shell.getParameters(), sb.toString());

			// set directory where execute process
			builder.directory(new File(System.getenv("JEM_HOME")+"/bin"));

			// load variable environment from a temporary maps that you can use
			// inside of configure method.
			Map<String, String> env = System.getenv();
			Map<String, String> map = builder.environment();
			for (Map.Entry<String, String> e : env.entrySet()) {
				map.put(e.getKey(), e.getValue());
			}
		
			// start process and save instance
			process = builder.start();
			// wait for end of job execution
			int rc = process.waitFor();
			
			FileInputStream fis = new FileInputStream(logFile);
			IOUtils.copy(fis, System.out);
			IOUtils.closeQuietly(fis);
			logFile.delete();
			return rc;
			
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
	}
}
