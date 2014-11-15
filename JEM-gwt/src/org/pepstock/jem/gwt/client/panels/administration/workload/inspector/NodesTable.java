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
package org.pepstock.jem.gwt.client.panels.administration.workload.inspector;

import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.gwt.client.panels.administration.commons.LightMemberSampleColumns;
import org.pepstock.jem.node.stats.LightMemberSample;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;

/**
 * Creates all columns to show into table, defening the sorter too.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class NodesTable extends AbstractTable<LightMemberSample> {

	
	
	/**
	 *  Empty constructor
	 */
	public NodesTable() {
	}

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
		 | TIME                    |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.TIME_SORTABLE, "Time");

		/*-------------------------+
		 | NUMBER OF JCL           |
		 +-------------------------*/
		TextColumn<LightMemberSample> numberOfJclCheck = new TextColumn<LightMemberSample>() {
			@Override
			public String getValue(LightMemberSample memberSample) {
				return NumberFormat.getFormat("###,###,##0").format(memberSample.getNumberOfJCLCheck());
			}
		};
		numberOfJclCheck.setSortable(true);
		table.addColumn(numberOfJclCheck, "JCL checked");
		
		/*-------------------------+
		 | TOT NUMBER OF JCL       |
		 +-------------------------*/
		TextColumn<LightMemberSample> totNumberOfJclCheck = new TextColumn<LightMemberSample>() {
			@Override
			public String getValue(LightMemberSample memberSample) {
				return NumberFormat.getFormat("###,###,##0").format(memberSample.getTotalNumberOfJCLCheck());
			}
		};
		totNumberOfJclCheck.setSortable(true);
		table.addColumn(totNumberOfJclCheck, "Total JCL checked");		

		/*-------------------------+
		 | NUMBER OF JOB           |
		 +-------------------------*/
		TextColumn<LightMemberSample> numberOfJobSubmitted = new TextColumn<LightMemberSample>() {
			@Override
			public String getValue(LightMemberSample memberSample) {
				return NumberFormat.getFormat("###,###,##0").format(memberSample.getNumberOfJOBSubmitted());
			}
		};
		numberOfJobSubmitted.setSortable(true);
		table.addColumn(numberOfJobSubmitted, "JOB submitted");

		/*-------------------------+
		 | TOT NUMBER OF JOB       |
		 +-------------------------*/
		TextColumn<LightMemberSample> totNumberOfJobSubmitted = new TextColumn<LightMemberSample>() {
			@Override
			public String getValue(LightMemberSample memberSample) {
				return NumberFormat.getFormat("###,###,##0").format(memberSample.getTotalNumberOfJOBSubmitted());
			}
		};
		totNumberOfJobSubmitted.setSortable(true);
		table.addColumn(totNumberOfJobSubmitted, "Total JOB submitted");		

		return new NodesComparator(0);

	}

}