/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.client.panels.administration.memory;

import org.pepstock.jem.gwt.client.commons.AbstractTable;
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
public class MemoryTable extends AbstractTable<Detail> {
	
	/**
	 *  Empty constructor
	 */
	public MemoryTable() {
	}

	/**
	 * Adds all columns to table, defining the sort columns too.
	 */
	@Override
	public IndexedColumnComparator<Detail> initCellTable(CellTable<Detail> table) {
		
		/*-------------------------+
		 | TIME                    |
		 +-------------------------*/
		TextColumn<Detail> time = new TextColumn<Detail>() {
			@Override
			public String getValue(Detail memberSample) {
				return memberSample.getTime();
			}
		};
		time.setSortable(true);
		table.addColumn(time, "Time");

		/*-------------------------+
		 | Free MB                 |
		 +-------------------------*/
		TextColumn<Detail> freeMb = new TextColumn<Detail>() {
			@Override
			public String getValue(Detail memberSample) {
				return  NumberFormat.getFormat("###,###,##0 MB").format(memberSample.getFree()/1024D/1024D);
			}
		};
		freeMb.setSortable(true);
		table.addColumn(freeMb, "Free");

		/*-------------------------+
		 | Free %%                 |
		 +-------------------------*/
		TextColumn<Detail> freePercent = new TextColumn<Detail>() {
			@Override
			public String getValue(Detail memberSample) {
				return  NumberFormat.getFormat("##0.00 %").format(memberSample.getFreePercent());
			}
		};
		freePercent.setSortable(true);
		table.addColumn(freePercent, "Free %");
		
		/*-------------------------+
		 | Used MB                 |
		 +-------------------------*/
		TextColumn<Detail> usedMb = new TextColumn<Detail>() {
			@Override
			public String getValue(Detail memberSample) {
				return  NumberFormat.getFormat("###,###,##0 MB").format(memberSample.getUsed()/1024D/1024D);
			}
		};
		usedMb.setSortable(true);
		table.addColumn(usedMb, "Used");
	
		/*-------------------------+
		 | Used %%                 |
		 +-------------------------*/
		TextColumn<Detail> usedPercent = new TextColumn<Detail>() {
			@Override
			public String getValue(Detail memberSample) {
				return  NumberFormat.getFormat("##0.00 %").format(memberSample.getUsedPercent());
			}
		};
		usedPercent.setSortable(true);
		table.addColumn(usedPercent, "Used %");

		return new MemoryComparator(0);

	}

}