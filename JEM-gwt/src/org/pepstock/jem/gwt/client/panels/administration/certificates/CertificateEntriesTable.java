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
package org.pepstock.jem.gwt.client.panels.administration.certificates;

import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.JemConstants;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.node.security.CertificateEntry;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.SelectionModel;

/**
 * Creates all columns for certificates to show into table, defining the sorter too.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public class CertificateEntriesTable extends AbstractTable<CertificateEntry> {

	/**
	 * Adds all columns to table, defining the sort columns too.
	 */
	@Override
	public IndexedColumnComparator<CertificateEntry> initCellTable(CellTable<CertificateEntry> table) {
		
		/*-------------------------+
		 | Selector                |
		 +-------------------------*/
		@SuppressWarnings("unchecked")
		final SelectionModel<CertificateEntry> selectionModel = (SelectionModel<CertificateEntry>) table.getSelectionModel();
		Column<CertificateEntry, Boolean> checkColumn = new Column<CertificateEntry, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(CertificateEntry entry) {
				return selectionModel.isSelected(entry);
			}
		};

		/*-------------------------+
		 | Selector header         |
		 +-------------------------*/
		CheckboxCell headerCheckBox = new CheckboxCell(true, false);
		Header<Boolean> checkHeader = new Header<Boolean>(headerCheckBox) {
			// sets header
			@Override
			public Boolean getValue() {
				// if no items, is not selectable
				if (getTable().getVisibleItems().isEmpty()) {
					return false;
				}
				// scans all visible objects
				for (CertificateEntry entry : getTable().getVisibleItems()) {
					// if there is a element already select, header is not selectable
					if (!getTable().getSelectionModel().isSelected(entry)) {
						return false;
					}
				}
				// all items arre selected
				return true;
			}
		};
		
		// updater which select and deselect all elements 
		checkHeader.setUpdater(new ValueUpdater<Boolean>() {
			@Override
			public void update(Boolean value) {
				for (CertificateEntry entry : getTable().getVisibleItems()) {
					getTable().getSelectionModel().setSelected(entry, value);
				}
			}
		});
				
		table.setColumnWidth(checkColumn, 23, Unit.PX);
		table.addColumn(checkColumn, checkHeader);

		
		/*-------------------------+
		 | Alias                   |
		 +-------------------------*/
	    TextColumn<CertificateEntry> name = new TextColumn<CertificateEntry>() {
			@Override
			public String getValue(CertificateEntry object) {
				return object.getAlias();
			}
		};
		name.setSortable(true);
		table.addColumn(name, "Alias");
		
		/*-------------------------+
		 | issuerDN                |
		 +-------------------------*/
		TextColumn<CertificateEntry> principal = new TextColumn<CertificateEntry>() {
			@Override
			public String getValue(CertificateEntry entry) {
				return String.valueOf(entry.getIssuer());
			}
		};
		table.addColumn(principal, "Issuer");

		/*-------------------------+
		 | subjectDN               |
		 +-------------------------*/
		TextColumn<CertificateEntry> subject = new TextColumn<CertificateEntry>() {
			@Override
			public String getValue(CertificateEntry entry) {
				return String.valueOf(entry.getSubject());
			}
		};
		table.addColumn(subject, "Subject");

		/*-------------------------+
		 | NotBefore               |
		 +-------------------------*/
		TextColumn<CertificateEntry> begin = new TextColumn<CertificateEntry>() {
			@Override
			public String getValue(CertificateEntry entry) {
				if (entry.getNotBefore() == null){
					return "";
				}
				return JemConstants.DATE_TIME_FULL.format(entry.getNotBefore()); 
			}
		};
		begin.setSortable(true);
		table.addColumn(begin, "Valid Not Before");
		
		/*-------------------------+
		 | NotAfter                |
		 +-------------------------*/
		TextColumn<CertificateEntry> after = new TextColumn<CertificateEntry>() {
			@Override
			public String getValue(CertificateEntry entry) {
				if (entry.getNotAfter() == null){
					return "";
				}				
				return JemConstants.DATE_TIME_FULL.format(entry.getNotAfter()); 
			}
		};
		after.setSortable(true);
		table.addColumn(after, "Valid Not After");

		// sets comparator
		return new CertificateEntriesComparator(0);

	}

}