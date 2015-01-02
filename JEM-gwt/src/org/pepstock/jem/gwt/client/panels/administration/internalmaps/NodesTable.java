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
package org.pepstock.jem.gwt.client.panels.administration.internalmaps;

import org.pepstock.jem.gwt.client.commons.AbstractTable;
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
		 | IP ADDRESS AND PORT      |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.IP_ADDRESS_AND_PORT_SORTABLE, "Name");
		
		/*-------------------------+
		 | PID                     |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.PID, "PID");

		/*-------------------------+
		 | TIME                    |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.TIME, "Time");

		/*-------------------------+
		 | RESOURCES               |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.RESOURCES_ENTRIES_SORTABLE, "Resources");

		/*-------------------------+
		 | RESOURCES MEMORY COST   |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.RESOURCES_MEMORY_COST_SORTABLE, "Resources Cost");		

		/*-------------------------+
		 | ROLES                   |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.ROLES_ENTRIES_SORTABLE, "Roles");

		/*-------------------------+
		 | ROLES MEMORY COST       |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.ROLES_MEMORY_COST_SORTABLE, "Roles Cost");		

		/*-------------------------+
		 | ROUTED                  |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.ROUTED_ENTRIES_SORTABLE, "Routed");

		/*-------------------------+
		 | ROUTED MEMORY COST      |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.ROUTED_MEMORY_COST_SORTABLE, "Routed Cost");		

		/*-------------------------+
		 | STATISTICS              |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.STATISTICS_ENTRIES_SORTABLE, "Statistics");

		/*-------------------------+
		 | STATISTICS MEMORY COST  |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.STATISTICS_MEMORY_COST_SORTABLE, "Statistics Cost");
		
		/*-------------------------+
		 | USERPREF                |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.USER_PREF_ENTRIES_SORTABLE, "UserPref");

		/*-------------------------+
		 | USERPREF MEMORY COST    |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.USER_PREF_MEMORY_COST_SORTABLE, "UserPref Cost");
		
		return new NodesComparator(0);

	}

}