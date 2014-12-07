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
package org.pepstock.jem.junit.test.springbatch.java;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;


/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class TrySecurityTasklet implements Tasklet {


	/* (non-Javadoc)
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {

		String what = null;
		try {
			what = "CHANGE SECURITY MANAGER!";
			// gry to change security manager
	        System.setSecurityManager(null);
        } catch (Exception e) {
        	e.printStackTrace();
    		try {
    			what = "CHANGE FIELD OF SECURITY MANAGER!";
	            SecurityManager sm = System.getSecurityManager();
	            Field f = sm.getClass().getField("isAdministrator");
	            System.err.println(FieldUtils.readField(f, sm, true));
            } catch (Exception e1) {
            	e1.printStackTrace();
	            return RepeatStatus.FINISHED;
            }
        }
		throw new SecurityException("Securitymanager is not secure: "+what);
	}
}
