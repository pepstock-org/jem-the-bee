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
package org.pepstock.jem.springbatch.tasks.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.pepstock.jem.node.tasks.jndi.ContextUtils;
import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.pepstock.jem.springbatch.tasks.JemTasklet;
import org.pepstock.jem.springbatch.tasks.TaskletException;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Spring batch utility which copy a file into another one.
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class CopyTasklet extends JemTasklet {

	private static final String INPUT_DATA_DESCRIPTION_NAME = "INPUT";
	
	private static final String OUTPUT_DATA_DESCRIPTION_NAME = "OUTPUT";

	/**
	 * Empty constructor
	 */
	public CopyTasklet() {
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.springbatch.tasks.JemTasklet#run(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@SuppressWarnings("resource")
	@Override
	public RepeatStatus run(StepContribution stepContribution, ChunkContext chuckContext) throws TaskletException {

		InputStream istream;
		OutputStream ostream;
		try {
			// new initial context to access by JNDI to COMMAND DataDescription
			InitialContext ic = ContextUtils.getContext();

			// gets inputstream
			Object input = (Object) ic.lookup(INPUT_DATA_DESCRIPTION_NAME);
			// gets outputstream
			Object output = (Object) ic.lookup(OUTPUT_DATA_DESCRIPTION_NAME);

			istream = null;
			ostream = null;

			// checks if object is a inputstream otherwise error
			if (input instanceof InputStream){
				istream = (InputStream) input;
			} else {
				throw new TaskletException(SpringBatchMessage.JEMS011E.toMessage().getFormattedMessage(INPUT_DATA_DESCRIPTION_NAME, input.getClass().getName()));
			}
			// checks if object is a outputstream otherwise error
			if (output instanceof OutputStream){
				ostream = (OutputStream) output;
			} else {
				throw new TaskletException(SpringBatchMessage.JEMS010E.toMessage().getFormattedMessage(OUTPUT_DATA_DESCRIPTION_NAME, output.getClass().getName()));
			}
		} catch (NamingException e) {
			throw new TaskletException(e.getMessage(), e);
		}

		try {
			IOUtils.copy(istream, ostream);
		} catch (IOException e) {
			throw new TaskletException(e.getMessage(), e);
		}

		return  RepeatStatus.FINISHED;
	}

}