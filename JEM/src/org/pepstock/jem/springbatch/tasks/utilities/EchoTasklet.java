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

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.springbatch.JemBean;
import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.pepstock.jem.springbatch.tasks.JemTasklet;
import org.pepstock.jem.springbatch.tasks.TaskletException;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Spring batch utility which display on standard output teh message passed.
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class EchoTasklet extends JemTasklet {
	
	private JemBean bean = null;

	/**
	 * Empty constructor
	 */
	public EchoTasklet() {
	}

	/**
	 * @return the bean
	 */
	public JemBean getBean() {
		return bean;
	}

	/**
	 * @param bean the bean to set
	 */
	public void setBean(JemBean bean) {
		this.bean = bean;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.springbatch.tasks.JemTasklet#run(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus run(StepContribution stepContribution, ChunkContext chuckContext) throws TaskletException {
		LogAppl.getInstance().emit(SpringBatchMessage.JEMS046E, bean.toString());
		return RepeatStatus.FINISHED;
	}

}