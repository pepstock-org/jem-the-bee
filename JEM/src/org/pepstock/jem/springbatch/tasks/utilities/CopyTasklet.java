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

import org.apache.commons.io.IOUtils;
import org.pepstock.jem.annotations.AssignDataDescription;
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

	@AssignDataDescription(INPUT_DATA_DESCRIPTION_NAME)
	private InputStream istream = null;
	
	@AssignDataDescription(OUTPUT_DATA_DESCRIPTION_NAME)
	private OutputStream ostream = null;
	
	/**
	 * Empty constructor
	 */
	public CopyTasklet() {
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.springbatch.tasks.JemTasklet#run(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus run(StepContribution stepContribution, ChunkContext chuckContext) throws TaskletException {
		try {
			int bytes = IOUtils.copy(istream, ostream);
			IOUtils.closeQuietly(istream);
			IOUtils.closeQuietly(ostream);
			System.err.println(SpringBatchMessage.JEMS053I.toMessage().getFormattedMessage(bytes));
		} catch (IOException e) {
			throw new TaskletException(e.getMessage(), e);
		}
		return  RepeatStatus.FINISHED;
	}

}