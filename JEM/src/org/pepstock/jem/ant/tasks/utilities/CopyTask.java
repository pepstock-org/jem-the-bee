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
package org.pepstock.jem.ant.tasks.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.pepstock.jem.ant.AntException;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.node.tasks.jndi.ContextUtils;

/**
 * Is a utility (both a task ANT and a main program) that copy data from and to a data description.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class CopyTask extends AbstractIOTask {

	/**
	 * Empty constructor
	 */
	public CopyTask() {
	}

	/**
	 * Main program, called by StepJava class. It reads from dd defined as INPUT and writes in OUTPUT one
	 * 
	 * @param args not used
	 * @throws AntException 
	 * @throws NamingException 
	 * @throws IOException 
	 * @throws Exception if data description data description doesn't exists, if an
	 *             error occurs during copying
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) throws AntException, NamingException, IOException  {
		// new initial context to access by JNDI to COMMAND DataDescription
		InitialContext ic = ContextUtils.getContext();
		// gets inputstream
		Object input = (Object) ic.lookup(INPUT_DATA_DESCRIPTION_NAME);
		// gets outputstream
		Object output = (Object) ic.lookup(OUTPUT_DATA_DESCRIPTION_NAME);

		InputStream istream = null;
		OutputStream ostream = null;

		// checks if object is a inputstream otherwise error
		if (input instanceof InputStream){
			istream = (InputStream) input;
		} else {
			throw new AntException(AntMessage.JEMA017E, INPUT_DATA_DESCRIPTION_NAME, input.getClass().getName());
		}
		// checks if object is a outputstream otherwise error
		if (output instanceof OutputStream){
			ostream = (OutputStream) output;
		} else {
			throw new AntException(AntMessage.JEMA016E, OUTPUT_DATA_DESCRIPTION_NAME, output.getClass().getName());
		}

		// copy
		int bytes = IOUtils.copy(istream, ostream);
		IOUtils.closeQuietly(istream);
		IOUtils.closeQuietly(ostream);
		System.err.println(AntMessage.JEMA062I.toMessage().getFormattedMessage(bytes));
	}
}