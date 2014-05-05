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
package org.pepstock.jem.junit.test.jppf.java;

import java.io.InputStream;
import java.io.Serializable;

import javax.naming.InitialContext;

import org.pepstock.jem.jppf.TaskData;
import org.pepstock.jem.jppf.TaskInfo;
import org.pepstock.jem.jppf.UniqueInitialContext;

/**
 * Simple reader used to JPPF test 
 * @author Andrea "Stock" Stocchero
 *
 */
public class RunnableReader implements Runnable, Serializable {

    private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			InitialContext ic = UniqueInitialContext.getContext();
			TaskData task = TaskInfo.getTaskData();
			Object o = (Object) ic.lookup("INPUT");
			InputStream is = (InputStream)o;
			StringBuffer sb = new StringBuffer();
			while(true){
				int read = is.read();
				if (read == -1)
					break;
				sb.append((char)read);
			}
			System.out.println(task.getIndex()+" length: "+sb.length());
			is.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
