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
package org.pepstock.jem.jbpm.tasks.utilities;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.naming.InitialContext;

import org.apache.commons.io.IOUtils;
import org.pepstock.jem.Result;
import org.pepstock.jem.jbpm.JBpmMessage;
import org.pepstock.jem.jbpm.tasks.JemWorkItem;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.log.MessageException;
import org.pepstock.jem.node.tasks.jndi.ContextUtils;

/**
 * JEM work item that copies a file or list of files (data description named INPUT) on other one (data description named OUTPUT).
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class Copy implements JemWorkItem {
	
	/**
	 * Data description name for files in INPUT
	 */
	private static final String INPUT_DATA_DESCRIPTION_NAME = "INPUT";
	
	/**
	 * Data description name for files in OUTPUT
	 */
	private static final String OUTPUT_DATA_DESCRIPTION_NAME = "OUTPUT";

	/* (non-Javadoc)
	 * @see org.pepstock.jem.jbpm.JemWorkItem#execute(java.util.Map)
	 */
	@Override
	public int execute(Map<String, Object> parameters) throws Exception {
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
			throw new MessageException(JBpmMessage.JEMM017E, INPUT_DATA_DESCRIPTION_NAME, input.getClass().getName());
		}
		// checks if object is a outputstream otherwise error
		if (output instanceof OutputStream){
			ostream = (OutputStream) output;
		} else {
			IOUtils.closeQuietly(istream);
			throw new MessageException(JBpmMessage.JEMM016E, OUTPUT_DATA_DESCRIPTION_NAME, output.getClass().getName());
		}

		// copy
		int bytes = IOUtils.copy(istream, ostream);
		IOUtils.closeQuietly(istream);
		IOUtils.closeQuietly(ostream);
		LogAppl.getInstance().emit(JBpmMessage.JEMM062I, bytes);

		return Result.SUCCESS;
	}

}
