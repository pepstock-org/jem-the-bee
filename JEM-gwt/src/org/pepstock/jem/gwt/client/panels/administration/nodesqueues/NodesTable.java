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
package org.pepstock.jem.gwt.client.panels.administration.nodesqueues;

import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.AnchorTextColumn;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.gwt.client.panels.administration.commons.LightMemberSampleColumns;
import org.pepstock.jem.node.stats.LightMemberSample;

import com.google.gwt.user.cellview.client.CellTable;

/**
 * Creates all columns to show into table, defening the sorter too.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class NodesTable extends AbstractTable<LightMemberSample> {

	/**
	 * Adds all columns to table, defining the sort columns too.
	 */
	@Override
	public IndexedColumnComparator<LightMemberSample> initCellTable(CellTable<LightMemberSample> table) {
		
		/*-------------------------+
		 | IPADDRESS AND PORT      |
		 +-------------------------*/
	    // construct a column that uses anchorRenderer
	    AnchorTextColumn<LightMemberSample> name = new AnchorTextColumn<LightMemberSample>() {
			@Override
			public String getValue(LightMemberSample object) {
				return object.getMemberLabel()+" - "+object.getMemberHostname();
			}

			@Override
			public void onClick(int index, LightMemberSample object, String value) {
				getInspectListener().inspect(object);
			}
		};
		name.setSortable(true);
		table.addColumn(name, "Name");
		
		/*-------------------------+
		 | PID                     |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.PID, "PID");

		/*-------------------------+
		 | TIME                    |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.TIME, "Time");

		/*-------------------------+
		 | INPUT                   |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.INPUT_ENTRIES_SORTABLE, "Input");

		/*-------------------------+
		 | Input Memory Cost       |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.INPUT_MEMORY_COST_SORTABLE, "Input Cost");		

		/*-------------------------+
		 | RUNNING                 |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.RUNNING_ENTRIES_SORTABLE, "Running");

		/*-------------------------+
		 | RUNNING MEMORY COST     |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.RUNNING_MEMORY_COST_SORTABLE, "Running Cost");		

		/*-------------------------+
		 | OUTPUT                  |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.OUTPUT_ENTRIES_SORTABLE, "Output");

		/*-------------------------+
		 | OUTPUT MEMORY COST      |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.OUTPUT_MEMORY_COST_SORTABLE, "Output Cost");		

		/*-------------------------+
		 | ROUTING                 |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.ROUTING_ENTRIES_SORTABLE, "Routing");

		/*-------------------------+
		 | ROUTING MEMORY COST     |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.ROUTING_MEMORY_COST_SORTABLE, "Routing Cost");
		
		return new NodesComparator(0);

	}

}