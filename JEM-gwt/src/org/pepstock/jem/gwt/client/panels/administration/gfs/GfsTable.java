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
package org.pepstock.jem.gwt.client.panels.administration.gfs;

import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.gwt.client.panels.administration.commons.LightMemberSampleColumns;
import org.pepstock.jem.node.stats.FileSystemUtilization;
import org.pepstock.jem.node.stats.LightMemberSample;
import org.pepstock.jem.util.MemorySize;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;

/**
 * Creates all columns to show into table, defining the sorter too.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class GfsTable extends AbstractTable<LightMemberSample> {
	
	private String fileSystemName = null;
	
	private GfsComparator comparator = null;
	
	protected static final NumberFormat MB_FORMAT = NumberFormat.getFormat("###,###,##0 MB");
	protected static final NumberFormat PERCENT_FORMAT = NumberFormat.getFormat("##0.00 %"); 

	/**
	 * Empty constructor
	 */
    public GfsTable() {

    }

	/**
	 * @return the fileSystemName
	 */
	public String getFileSystemName() {
		return fileSystemName;
	}

	/**
	 * @param fileSystemName the fileSystemName to set
	 */
	public void setFileSystemName(String fileSystemName) {
		this.fileSystemName = fileSystemName;
	}

	/**
	 * Adds all columns to table, defining the sort columns too.
	 */
	@Override
	public IndexedColumnComparator<LightMemberSample> initCellTable(CellTable<LightMemberSample> table) {
		this.comparator = new GfsComparator(0, this);
		
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
				FileSystemUtilization futil = Util.getFileSystemUtilization(memberSample, fileSystemName);
				return MB_FORMAT.format(futil.getFree()/(double)MemorySize.KB);
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
				FileSystemUtilization futil = Util.getFileSystemUtilization(memberSample, fileSystemName);
				long tot = futil.getFree() + futil.getUsed();
				return PERCENT_FORMAT.format(futil.getFree()/(double)tot);
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
				FileSystemUtilization futil = Util.getFileSystemUtilization(memberSample, fileSystemName);
				return MB_FORMAT.format(futil.getUsed()/(double)MemorySize.KB);
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
				FileSystemUtilization futil = Util.getFileSystemUtilization(memberSample, fileSystemName);
				long tot = futil.getFree() + futil.getUsed();
				return PERCENT_FORMAT.format(futil.getUsed()/(double)tot);
			}
		};
		usedPercent.setSortable(true);
		table.addColumn(usedPercent, "Used %");

		return this.comparator;

	}

}