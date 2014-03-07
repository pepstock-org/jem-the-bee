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
package org.pepstock.jem.gwt.client.panels.administration.redo;

import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.node.persistence.RedoStatement;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;

/**
 * Creates all columns to show into table, defening teh sorter too.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class RedoTable extends AbstractTable<RedoStatement> {

	
	
	/**
	 *  Empty constructor
	 */
	public RedoTable() {
	}

	/**
	 * Adds all columns to table, defining the sort columns too.
	 */
	@Override
	public IndexedColumnComparator<RedoStatement> initCellTable(CellTable<RedoStatement> table) {
		
	
		/*-------------------------+
		 | PID                     |
		 +-------------------------*/
		TextColumn<RedoStatement> id = new TextColumn<RedoStatement>() {
			@Override
			public String getValue(RedoStatement redo) {
				return String.valueOf(redo.getId());
			}
		};
		id.setSortable(true);
		table.addColumn(id, "ID");

		/*-------------------------+
		 | QUEUENAME               |
		 +-------------------------*/
		TextColumn<RedoStatement> queue = new TextColumn<RedoStatement>() {
			@Override
			public String getValue(RedoStatement redo) {
				return redo.getQueueName();
			}
		};
		queue.setSortable(true);
		table.addColumn(queue, "Queue");

		/*-------------------------+
		 | ACTION                  |
		 +-------------------------*/
		TextColumn<RedoStatement> action = new TextColumn<RedoStatement>() {
			@Override
			public String getValue(RedoStatement redo) {
				return redo.getAction();
			}
		};
		action.setSortable(true);
		table.addColumn(action, "Action");
		
		/*-------------------------+
		 | JOBID                   |
		 +-------------------------*/
		TextColumn<RedoStatement> jobId = new TextColumn<RedoStatement>() {
			@Override
			public String getValue(RedoStatement redo) {
				return redo.getJobId() == null ? redo.getJob() == null ? "" : redo.getJob().getId() : redo.getJobId() ;
			}
		};
		jobId.setSortable(true);
		table.addColumn(jobId, "Job ID");		

		/*-------------------------+
		 | JOB             |
		 +-------------------------*/
		TextColumn<RedoStatement> jobName = new TextColumn<RedoStatement>() {
			@Override
			public String getValue(RedoStatement redo) {
				return (redo.getJob() == null) ? "" : redo.getJob().getName();
			}
		};
		jobName.setSortable(true);
		table.addColumn(jobName, "Job Name");		
		

		return new RedoComparator(0);

	}

}