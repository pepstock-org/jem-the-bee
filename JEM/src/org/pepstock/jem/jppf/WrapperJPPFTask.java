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
package org.pepstock.jem.jppf;

import org.jppf.server.protocol.JPPFTask;
import org.jppf.task.storage.DataProvider;

/**
 * Is a JPPF task that wraps a JPPFTask object to execute on JPPF grid.<br>
 * To perform a very good grid business logic, the usage of a JPPFtask is suggested
 * because you can have all information about position, id, job, cancel and timeout events.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 * 
 */
public class WrapperJPPFTask extends RunnableTask {

	private static final long serialVersionUID = 1L;

	private JPPFTask task = null;

	/**
	 * Constructs task using the JPPFTask object to execute
	 * @param task the JPPFTask object to execute
	 */
	public WrapperJPPFTask(JPPFTask task) {
		super(task);
		this.task = task;
	}
	
	/* (non-Javadoc)
	 * @see org.jppf.server.protocol.JPPFTask#setDataProvider(DataProvider)
	 */
	@Override
	public void setDataProvider(DataProvider dataProvider){
		super.setDataProvider(dataProvider);
		// sets the dataprovider to the delegate
		task.setDataProvider(dataProvider);
	}

	/* (non-Javadoc)
	 * @see org.jppf.server.protocol.JPPFTask#onCancel()
	 */
	@Override
	public void onCancel() {
		// calls the delegate
		task.onCancel();
	}

	/* (non-Javadoc)
	 * @see org.jppf.server.protocol.JPPFTask#onTimeout()
	 */
	@Override
	public void onTimeout() {
		// calls the delegate
		task.onTimeout();
	}

	/* (non-Javadoc)
	 * @see org.jppf.server.protocol.JPPFTask#setInNode(boolean)
	 */
	@Override
	public void setInNode(final boolean inNode) {
		super.setInNode(inNode);
		// sets the inNode to the delegate
		task.setInNode(inNode);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// sets position to the delegate
		task.setPosition(getPosition());
		// sets id to the delegate
		task.setId("SubTask["+getPosition()+"]");
		// calls RunnableTask execution
		super.run();
	}
}