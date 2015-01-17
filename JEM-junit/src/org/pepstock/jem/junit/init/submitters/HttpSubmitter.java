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

import org.pepstock.jem.commands.HttpSubmit;
import org.pepstock.jem.commands.SubmitResult;
import org.pepstock.jem.junit.init.Submitter;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class HttpSubmitter extends AbstractSubmitter {

	/**
	 * @param selectedSubmitter
	 * @param jcl
	 * @param type
	 * @param wait
	 * @param printout
	 */
	public HttpSubmitter(Submitter selectedSubmitter, String jcl, String type, boolean wait, boolean printout) {
		super(selectedSubmitter, jcl, type, wait, printout);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public SubmitResult call() throws Exception {
		// get args
		String[] args = getArguments();
		
		if (getSelectedSubmitter().getEmbedded()){
			HttpSubmit submit = new HttpSubmit();
			return submit.execute(args);				
		} else {
			int rc =  launch("jem_http_submit", args);
			SubmitResult res = new SubmitResult(rc, "null");
			return res;
		}
	}
}
