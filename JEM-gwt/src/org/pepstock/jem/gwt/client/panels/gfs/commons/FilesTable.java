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
package org.pepstock.jem.gwt.client.panels.gfs.commons;

import java.util.Date;

import org.pepstock.jem.GfsFile;
import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.JemConstants;
import org.pepstock.jem.gwt.client.commons.FileSystemTableStyle;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTable.Resources;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;

/**
 * Creates all columns to show into table, defening teh sorter too.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public abstract class FilesTable extends AbstractTable<GfsFile> {

	/**
	 *  Empty constructor with default style
	 * @param preferenceKey preference key to extract previous choice
	 */
	public FilesTable() {
		super((Resources) GWT.create(FileSystemTableStyle.class), Integer.MAX_VALUE);
	}

	/**
	 * Abstract to implement
	 */
	@Override
	public final IndexedColumnComparator<GfsFile> initCellTable(CellTable<GfsFile> table){
		loadCellTable(table);
		return getIndexedColumnComparator();
	}
	
	/**
	 * @return index column comparator for sorting
	 */
	public abstract IndexedColumnComparator<GfsFile> getIndexedColumnComparator();
	/**
	 * Adds all columns to table, defining the sort columns too.
	 */

	private void loadCellTable(CellTable<GfsFile> table) {
		
		/*-------------------------+
		 | Name      |
		 +-------------------------*/
	    // construct a column that uses anchorRenderer
		Column<GfsFile, GfsFile> name = null;
		if (isOverviewOnly()){
			name = new FolderLinkTextColumn() {

				@Override
				public void onClick(int index, GfsFile object) {
					getInspectListener().inspect(object);
				}
			};
			name.setSortable(true);
			table.addColumn(name, "Name");
			
		} else {
			name = new FullLinkTextColumn() {

				@Override
				public void onClick(int index, GfsFile object) {
					getInspectListener().inspect(object);
				}
			};
			name.setSortable(true);
			table.addColumn(name, "Name");
		}
		/*-------------------------+
		 | Size  in bytes          |
		 +-------------------------*/
		TextColumn<GfsFile> size = new TextColumn<GfsFile>() {
			@Override
			public String getValue(GfsFile memberSample) {
				long size = memberSample.getLength();
				if (size >=0 ) {
					return NumberFormat.getFormat("###,###,###,###,###,##0").format(size);
				} else { 
					return "";
				}
			}
		};
		size.setSortable(true);
		table.addColumn(size, "Size (bytes)");

		TextColumn<GfsFile> lastMod = new TextColumn<GfsFile>() {
			@Override
			public String getValue(GfsFile file) {
				return JemConstants.DATE_TIME_FULL.format(new Date(file.getLastModified()));
			}
		};
		lastMod.setSortable(true);
		table.addColumn(lastMod, "Last modified");
	}
	
	/**
	 * @return
	 */
	public abstract boolean isOverviewOnly();
}