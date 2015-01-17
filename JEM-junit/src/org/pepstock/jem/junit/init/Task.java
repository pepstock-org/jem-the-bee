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

import java.util.concurrent.Callable;

import org.pepstock.jem.commands.HttpSubmit;
import org.pepstock.jem.commands.Submit;
import org.pepstock.jem.commands.SubmitResult;
import org.pepstock.jem.junit.init.submitters.AbstractSubmitter;
import org.pepstock.jem.junit.init.submitters.HttpSubmitter;
import org.pepstock.jem.junit.init.submitters.LocalHostSubmitter;
import org.pepstock.jem.junit.init.submitters.SubmitSubmitter;

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
		AbstractSubmitter s = null;
		if (selectedSubmitter.getReferenceClass().equals(HttpSubmit.class)) {
			s = new HttpSubmitter(selectedSubmitter, jcl, type, wait, printout);
		} else if (selectedSubmitter.getReferenceClass().equals(Submit.class)) {
			s = new SubmitSubmitter(selectedSubmitter, jcl, type, wait, printout);
		} else {
			s = new LocalHostSubmitter(selectedSubmitter, jcl, type, wait, printout);
		}
		return s.call();
	}
}
