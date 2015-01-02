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
package org.pepstock.jem.junit.init;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.pepstock.jem.commands.HttpSubmit;
import org.pepstock.jem.commands.LocalHostSubmit;
import org.pepstock.jem.commands.Submit;
import org.pepstock.jem.commands.SubmitParameters;
import org.pepstock.jem.commands.SubmitResult;

/**
 * 
 * @author Simone "Busy" Businaro
 *
 */
public class Task implements Callable<SubmitResult> {

	private String jcl;

	private String type;

	private boolean wait;

	private boolean printout;

	private Submitter selectedSubmitter;

	/**
	 * @param selectedSubmitter
	 *            the submitter to use to submit the jcl
	 * @param jcl
	 *            the jcl to run
	 * @param type
	 *            the jcl type
	 * @param wait
	 *            true to wait for job response after it's execution false
	 *            otherwise
	 * @param printout
	 *            true to print to outout of the job to standard output stream
	 *            false otherwise
	 */
	public Task(Submitter selectedSubmitter, String jcl, String type,
			boolean wait, boolean printout) {
		this.selectedSubmitter = selectedSubmitter;
		this.jcl = jcl;
		this.type = type;
		this.wait = wait;
		this.printout = printout;
	}

	@Override
	public SubmitResult call() throws Exception {
		if (selectedSubmitter.getReferenceClass().equals(HttpSubmit.class)) {
			// get args
			String[] args = getArguments(HttpSubmit.class, jcl, type, wait,
					printout);
			HttpSubmit submit = new HttpSubmit();
			return submit.execute(args);
		} else if (selectedSubmitter.getReferenceClass().equals(Submit.class)) {
			// get args
			String[] args = getArguments(Submit.class, jcl, type, wait,
					printout);
			Submit submit = new Submit();
			return submit.execute(args);
		} else {
			// get args
			String[] args = getArguments(LocalHostSubmit.class, jcl, type,
					wait, printout);
			LocalHostSubmit submit = new LocalHostSubmit();
			return submit.execute(args);
		}
	}

	@SuppressWarnings("rawtypes")
	private String[] getArguments(Class clazz, String jcl, String type,
			boolean wait, boolean printout) {
		List<String> list = new ArrayList<String>();
		String paramJcl = null;
		String paramType = null;
		String paramWait = null;
		String paramPrintOut = null;
		if (clazz.equals(HttpSubmit.class)) {
			paramJcl = SubmitParameters.JCL.getName();
			paramType = SubmitParameters.TYPE.getName();
			paramWait = SubmitParameters.WAIT.getName();
		}
		if (clazz.equals(Submit.class)) {
			paramJcl = SubmitParameters.JCL.getName();
			paramType = SubmitParameters.TYPE.getName();
			paramWait = SubmitParameters.WAIT.getName();
			paramPrintOut = SubmitParameters.PRINT_OUTPUT.getName();
		}
		if (clazz.equals(LocalHostSubmit.class)) {
			paramJcl = SubmitParameters.JCL.getName();
			paramType = SubmitParameters.TYPE.getName();
			paramWait = SubmitParameters.WAIT.getName();
			paramPrintOut = SubmitParameters.PRINT_OUTPUT.getName();
		}
		for (int i = 0; i < selectedSubmitter.getParams().size(); i++) {
			list.add("-" + selectedSubmitter.getParams().get(i).getName());
			list.add(selectedSubmitter.getParams().get(i).getValue());
		}
		list.add("-" + paramJcl);
		list.add(jcl);
		list.add("-" + paramType);
		list.add(type);
		list.add("-" + paramWait);
		list.add(String.valueOf(wait));
		if (paramPrintOut != null){
			list.add("-" + paramPrintOut);
			list.add(String.valueOf(printout));
		}
		
		System.err.println(list);
		return list.toArray(new String[list.size()]);
	}
}
