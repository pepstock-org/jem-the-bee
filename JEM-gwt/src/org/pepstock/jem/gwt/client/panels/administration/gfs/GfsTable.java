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
package org.pepstock.jem.gwt.client.panels.administration.gfs;

import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.gwt.client.panels.administration.commons.LightMemberSampleColumns;
import org.pepstock.jem.node.stats.LightMemberSample;

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
public class GfsTable extends AbstractTable<LightMemberSample> {
	
	protected static final NumberFormat MB_FORMAT = NumberFormat.getFormat("###,###,##0 MB");
	protected static final NumberFormat PERCENT_FORMAT = NumberFormat.getFormat("##0.00 %"); 
	
	/**
	 *  Empty constructor
	 */
	public GfsTable() {
	}

	/**
	 * Adds all columns to table, defining the sort columns too.
	 */
	@Override
	public IndexedColumnComparator<LightMemberSample> initCellTable(CellTable<LightMemberSample> table) {
		
		/*-------------------------+
		 | TIME                    |
		 +-------------------------*/
		table.addColumn(LightMemberSampleColumns.TIME_SORTABLE, "Time");

		/*-------------------------+
		 | Free MB                 |
		 +-------------------------*/
		TextColumn<LightMemberSample> freeMb = new TextColumn<LightMemberSample>() {
			@Override
			public String getValue(LightMemberSample memberSample) {
				return MB_FORMAT.format(memberSample.getGfsFree()/1024D);
			}
		};
		freeMb.setSortable(true);
		table.addColumn(freeMb, "Free");

		/*-------------------------+
		 | Free %%                 |
		 +-------------------------*/
		TextColumn<LightMemberSample> freePercent = new TextColumn<LightMemberSample>() {
			@Override
			public String getValue(LightMemberSample memberSample) {
				long tot = memberSample.getGfsFree() + memberSample.getGfsUsed();
				return PERCENT_FORMAT.format(memberSample.getGfsFree()/(double)tot);
			}
		};
		freePercent.setSortable(true);
		table.addColumn(freePercent, "Free %");
		
		/*-------------------------+
		 | Used MB                 |
		 +-------------------------*/
		TextColumn<LightMemberSample> usedMb = new TextColumn<LightMemberSample>() {
			@Override
			public String getValue(LightMemberSample memberSample) {
				return MB_FORMAT.format(memberSample.getGfsUsed()/1024D);
			}
		};
		usedMb.setSortable(true);
		table.addColumn(usedMb, "Used");
	
		/*-------------------------+
		 | Used %%                 |
		 +-------------------------*/
		TextColumn<LightMemberSample> usedPercent = new TextColumn<LightMemberSample>() {
			@Override
			public String getValue(LightMemberSample memberSample) {
				long tot = memberSample.getGfsFree() + memberSample.getGfsUsed();
				return PERCENT_FORMAT.format(memberSample.getGfsUsed()/(double)tot);
			}
		};
		usedPercent.setSortable(true);
		table.addColumn(usedPercent, "Used %");

		return new GfsComparator(0);

	}

}