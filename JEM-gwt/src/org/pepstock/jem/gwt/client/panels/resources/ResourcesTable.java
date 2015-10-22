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
package org.pepstock.jem.gwt.client.panels.resources;

import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.AnchorTextColumn;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.gwt.client.commons.JemConstants;
import org.pepstock.jem.gwt.client.commons.TextFilterableHeader;
import org.pepstock.jem.node.resources.Resource;
import org.pepstock.jem.node.resources.ResourceProperty;
import org.pepstock.jem.util.filters.fields.ResourceFilterFields;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.SelectionModel;

/**
 * Creates all columns to show into table, defening the sorter too.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class ResourcesTable extends AbstractTable<Resource> {	
	
	/**
	 * Adds all columns to table, defining the sort columns too.
	 */
    @Override
    public IndexedColumnComparator<Resource> initCellTable(CellTable<Resource> table) {
		@SuppressWarnings("unchecked")
		final SelectionModel<Resource> selectionModel = (SelectionModel<Resource>) table.getSelectionModel();
		Column<Resource, Boolean> checkColumn = new Column<Resource, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(Resource role) {
				return selectionModel.isSelected(role);
			}
		};

		CheckboxCell headerCheckBox = new CheckboxCell(true, false);
		Header<Boolean> checkHeader = new Header<Boolean>(headerCheckBox) {
			// imposta lo stato dell'header!
			@Override
			public Boolean getValue() {
				// se e' vuoto, niente e' selezionato/selezionabile
				if (getTable().getVisibleItems().isEmpty()) {
					return false;
				}
				
				// altrimenti testo
				for (Resource r : getTable().getVisibleItems()) {
					// se almeno un elemento non e' selezionato, l'header non deve essere selezionato
					if (!getTable().getSelectionModel().isSelected(r)) {
						return false;
					}
				}
				// altrimenti se arrivo qui, tutti gli elementi sono selezionati
				return true;
			}
		};
		
		// updater che seleziona o deseleziona tutti gli elementi visibili in base al "valore" dell'header
		checkHeader.setUpdater(new ValueUpdater<Boolean>() {
			@Override
			public void update(Boolean value) {
				for (Resource r : getTable().getVisibleItems()) {
					getTable().getSelectionModel().setSelected(r, value);
				}
			}
		});
				
		table.setColumnWidth(checkColumn, AbstractTable.DEFAULT_CHECK_COLUMN_WIDTH, Unit.PX);
		table.addColumn(checkColumn, checkHeader);

		AnchorTextColumn<Resource> name = new AnchorTextColumn<Resource>() {
			@Override
			public void onClick(int index, Resource object, String value) {
				getInspectListener().inspect(object);
			}

			@Override
			public String getValue(Resource resource) {
				return resource.getName();
			}
		};
		name.setSortable(true);
		table.addColumn(name, new TextFilterableHeader("Name", ResourceFilterFields.NAME.getName()));

		TextColumn<Resource> type = new TextColumn<Resource>() {
			@Override
			public String getValue(Resource resource) {
				return resource.getType();
			}
		};
		type.setSortable(true);
		table.addColumn(type, new TextFilterableHeader("Type", ResourceFilterFields.TYPE.getName()));

		
		AttributesColumn attributes = new AttributesColumn();
		table.addColumn(attributes, new TextFilterableHeader("Properties", ResourceFilterFields.PROPERTIES.getName()));

		TextColumn<Resource> lastModified = new TextColumn<Resource>() {
			@Override
			public String getValue(Resource resource) {
				if (resource.getLastModified() == null){
					return "";
				}
				return JemConstants.DATE_TIME_FULL.format(resource.getLastModified()); 
			}
		};
		lastModified.setSortable(true);
		table.addColumn(lastModified, new TextFilterableHeader("Modified", ResourceFilterFields.MODIFIED.getName(), ResourceFilterFields.MODIFIED.getPattern()));

		TextColumn<Resource> user = new TextColumn<Resource>() {
			@Override
			public String getValue(Resource resource) {
				return resource.getUser() != null ? resource.getUser() : "";
			}
		};
		user.setSortable(true);
		table.addColumn(user, new TextFilterableHeader("Modified by", ResourceFilterFields.MODIFIED_BY.getName()));
		
        return new ResourcesComparator(1);
	}
    
    private static class AttributesColumn extends TextColumn<Resource> {
    	@Override
		public String getValue(Resource resource) {
			int count = 0;
			String value = null;
			for (ResourceProperty property : resource.getProperties().values()){
				if (property.isVisible()){
					if (count == 0){
						value = property.getName() + " = " + property.getValue();
					} else {
						value = value +", " + property.getName() + " = " + property.getValue();	
					}
					count++;
				}
			}
			return value;
		}
		
		@Override
		public void render(Context context, Resource resource, SafeHtmlBuilder sb) {
			// Value can be null, so do a null check..
			if (resource == null) {
				return;
			}
			sb.appendHtmlConstant("<code>"+getValue(resource)+"</code>");
		}	
    }
    
}