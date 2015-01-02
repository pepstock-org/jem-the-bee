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
package org.pepstock.jem.junit.test.jppf.java;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.UUID;

import javax.naming.InitialContext;

import org.pepstock.jem.jppf.TaskData;
import org.pepstock.jem.jppf.TaskInfo;
import org.pepstock.jem.jppf.UniqueInitialContext;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class RunnableWriter implements Runnable, Serializable {

    private static final long serialVersionUID = 1L;
    
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			InitialContext ic = UniqueInitialContext.getContext();
			TaskData task = TaskInfo.getTaskData();

			Object o = (Object) ic.lookup("OUTPUT");
			OutputStream os = (OutputStream)o;
			PrintStream print = new PrintStream(os);
			for (int i=0; i< 100;i++){
				print.println(task.getIndex()+":"+UUID.randomUUID().toString());
			}
			print.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
