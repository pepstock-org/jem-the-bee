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
package org.pepstock.jem.gwt.client.panels.administration.queues;

import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.AnchorTextColumn;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;

/**
 * Creates all columns to show into table, defening teh sorter too.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class QueuesTable extends AbstractTable<DetailedQueueData> {

	/**
	 *  Empty constructor
	 */
	public QueuesTable() {
	}

	/**
	 * Adds all columns to table, defining the sort columns too.
	 */
	@Override
	public IndexedColumnComparator<DetailedQueueData> initCellTable(CellTable<DetailedQueueData> table) {
		
		/*-------------------------+
		 | QUEUE NAME              |
		 +-------------------------*/
	    // construct a column that uses anchorRenderer
	    AnchorTextColumn<DetailedQueueData> name = new AnchorTextColumn<DetailedQueueData>() {
			@Override
			public String getValue(DetailedQueueData object) {
				return object.getShortName();
			}

			@Override
			public void onClick(int index, DetailedQueueData object, String value) {
				getInspectListener().inspect(object);
			}
		};
		name.setSortable(true);
		table.addColumn(name, "Queue");
		
		/*-------------------------+
		 | Time                    |
		 +-------------------------*/
		TextColumn<DetailedQueueData> time = new TextColumn<DetailedQueueData>() {
			@Override
			public String getValue(DetailedQueueData data) {
				return data.getTime();
			}
		};
		table.addColumn(time, "Time");

		/*-------------------------+
		 | NUMBER OF ENTRIES       |
		 +-------------------------*/
		TextColumn<DetailedQueueData> numberOfEntries = new TextColumn<DetailedQueueData>() {
			@Override
			public String getValue(DetailedQueueData data) {
				 
				return NumberFormat.getFormat("###,###,##0").format(data.getEntries());
			}
		};
		numberOfEntries.setSortable(true);
		table.addColumn(numberOfEntries, "Entries");

		/*-------------------------+
		 | TOT NUMBER OF hits      |
		 +-------------------------*/
		TextColumn<DetailedQueueData> hits = new TextColumn<DetailedQueueData>() {
			@Override
			public String getValue(DetailedQueueData data) {
				return NumberFormat.getFormat("###,###,##0").format(data.getHits());
			}
		};
		hits.setSortable(true);
		table.addColumn(hits, "Hits");	
		
		/*-------------------------+
		 | TOT NUMBER OF Locks     |
		 +-------------------------*/
		TextColumn<DetailedQueueData> locks = new TextColumn<DetailedQueueData>() {
			@Override
			public String getValue(DetailedQueueData data) {
				return NumberFormat.getFormat("###,###,##0").format(data.getLocked());
			}
		};
		locks.setSortable(true);
		table.addColumn(locks, "Locked Entries");	
		
		/*-------------------------+
		 | TOT NUMBER OF Locks Wait |
		 +-------------------------*/
		TextColumn<DetailedQueueData> lockWaits = new TextColumn<DetailedQueueData>() {
			@Override
			public String getValue(DetailedQueueData data) {
				return NumberFormat.getFormat("###,###,##0").format(data.getLockWaits());
			}
		};
		lockWaits.setSortable(true);
		table.addColumn(lockWaits, "Lock Waits");	
		/*-------------------------+
		 | TOT NUMBER OF Gets      |
		 +-------------------------*/
		TextColumn<DetailedQueueData> numberOfGets = new TextColumn<DetailedQueueData>() {
			@Override
			public String getValue(DetailedQueueData data) {
				return NumberFormat.getFormat("###,###,##0").format(data.getGets());
			}
		};
		numberOfGets.setSortable(true);
		table.addColumn(numberOfGets, "Number of Gets");		

		/*-------------------------+
		 | TOT NUMBER OF Puts      |
		 +-------------------------*/
		TextColumn<DetailedQueueData> numberOfPuts = new TextColumn<DetailedQueueData>() {
			@Override
			public String getValue(DetailedQueueData data) {
				return NumberFormat.getFormat("###,###,##0").format(data.getPuts());
			}
		};
		numberOfPuts.setSortable(true);
		table.addColumn(numberOfPuts, "Number of Puts");	

		/*-------------------------+
		 | TOT NUMBER OF Removes   |
		 +-------------------------*/
		TextColumn<DetailedQueueData> numberOfRemoves = new TextColumn<DetailedQueueData>() {
			@Override
			public String getValue(DetailedQueueData data) {
				return NumberFormat.getFormat("###,###,##0").format(data.getGets());
			}
		};
		numberOfRemoves.setSortable(true);
		table.addColumn(numberOfRemoves, "Number of Removes");			


		return new QueuesComparator(0);

	}

}