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
package org.pepstock.jem.gwt.client.panels.administration.nodessystem.inspector;

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
		 | MACHINE CPU Percent     |
		 +-------------------------*/
		TextColumn<LightMemberSample> machineCpu = new TextColumn<LightMemberSample>() {
			@Override
			public String getValue(LightMemberSample memberSample) {
				double cpu = memberSample.getCpuPercent();
				String data = NumberFormat.getFormat("##0.00").format(cpu*100);
				return data + " %";
			}
		};
		machineCpu.setSortable(true);
		table.addColumn(machineCpu, "Cpu");
		
		/*-------------------------+
		 | PROCESS MEMORY AVAILABLE|
		 +-------------------------*/
		TextColumn<LightMemberSample> memoryAvail = new TextColumn<LightMemberSample>() {
			@Override
			public String getValue(LightMemberSample memberSample) {
				return NumberFormat.getFormat("###,##0 MB").format((double)memberSample.getMemoryAvailable()/1024D/1024D);
			}
		};
		memoryAvail.setSortable(true);
		table.addColumn(memoryAvail, "Memory available");		

		/*-------------------------+
		 | PROCESS MEMORY FREE     |
		 +-------------------------*/
		TextColumn<LightMemberSample> memoryFree = new TextColumn<LightMemberSample>() {
			@Override
			public String getValue(LightMemberSample memberSample) {
				return NumberFormat.getFormat("###,##0 MB").format((double)memberSample.getMemoryFree()/1024D/1024D);
			}
		};
		memoryFree.setSortable(true);
		table.addColumn(memoryFree, "Memory free");		
		
		/*-------------------------+
		 | PROCESS CPU PERCENT     |
		 +-------------------------*/
		TextColumn<LightMemberSample> processCpu = new TextColumn<LightMemberSample>() {
			@Override
			public String getValue(LightMemberSample memberSample) {
				double cpu = memberSample.getProcessCpuPercent();
				String data = NumberFormat.getFormat("##0.00").format(cpu*100);
				return data + " %";
			}
		};
		processCpu.setSortable(true);
		table.addColumn(processCpu, "Process Cpu");		

		/*-------------------------+
		 | PROCESS CPU TOTAL       |
		 +-------------------------*/
		TextColumn<LightMemberSample> processCpuTotal = new TextColumn<LightMemberSample>() {
			@Override
			public String getValue(LightMemberSample memberSample) {
				return NumberFormat.getFormat("###,###,##0 ms").format(memberSample.getProcessTotalCpu());
			}
		};
		processCpuTotal.setSortable(true);
		table.addColumn(processCpuTotal, "Process Cpu Total");

		/*-------------------------+
		 | PROCESS MEMORY USED     |
		 +-------------------------*/
		TextColumn<LightMemberSample> processMemoryUsed = new TextColumn<LightMemberSample>() {
			@Override
			public String getValue(LightMemberSample memberSample) {
				return NumberFormat.getFormat("###,##0 MB").format(memberSample.getProcessMemoryUsed()/1024d/1024d);
			}
		};
		processMemoryUsed.setSortable(true);
		table.addColumn(processMemoryUsed, "Process Memory used");		

		return new NodesComparator(0);

	}

}