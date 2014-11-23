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
package org.pepstock.jem.gwt.client.panels.resources.inspector.widgets;

import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.UpdateListener;
import org.pepstock.jem.node.resources.ResourceProperty;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;

/**
 * Creates a table to manage key-value map..
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2	
 *
 */
public class CustomPropertiesTable extends AbstractTable<ResourceProperty> {

	/**
	 * Adds all columns to table, defining the sort columns too.
	 */
	@Override
	public IndexedColumnComparator<ResourceProperty> initCellTable(final CellTable<ResourceProperty> table) {
		/*-------------------------+
		 | KEY                |
		 +-------------------------*/
		
		Column<ResourceProperty, String> key = new Column<ResourceProperty, String>(
				new EditTextCell()) {
			@Override
			public String getValue(ResourceProperty property) {
				return property.getName();
			}
		};
		key.setSortable(false);
		key.setFieldUpdater(new KeyFieldUpdater());	
		table.addColumn(key, "Key");
		table.setColumnWidth(key, 50, Unit.PCT);
		
		/*-------------------------+
		 | VALUE       |
		 +-------------------------*/
		Column<ResourceProperty, String> value = new Column<ResourceProperty, String>(
				new EditTextCell()) {
			@Override
			public String getValue(ResourceProperty property) {
				return property.getValue();
			}
		};
		value.setSortable(false);
		value.setFieldUpdater(new ValueFieldUpdater());	
		table.addColumn(value, "Value");
		table.setColumnWidth(value, 50, Unit.PCT);
		return new CustomPropertiesComparator(1);

	}

	private class KeyFieldUpdater implements FieldUpdater<ResourceProperty, String> {
		@Override
		public void update(int index, ResourceProperty property, String valueParm) {
			String value = valueParm;
			if (value !=null && value.trim().length() > 0){
				// set value blanks
				property.setName(value);
			} else {
				// set value blanks
				property.setName(CustomPropertiesEditor.NO_VALUE);
				property.setValue(CustomPropertiesEditor.NO_VALUE);
			}
			InspectListener<ResourceProperty> listener = getInspectListener();
			if (listener instanceof UpdateListener<?>){
				UpdateListener<ResourceProperty> ulistener= (UpdateListener<ResourceProperty>) listener;
				ulistener.update(property);
			}
		}
	}
	
	private static class ValueFieldUpdater implements FieldUpdater<ResourceProperty, String> {
		@Override
		public void update(int index, ResourceProperty property, String valueParm) {
			String value = valueParm;
			if (value !=null && value.trim().length() > 0){
				property.setValue(value);
			} else {
				// set value blanks
				property.setValue(CustomPropertiesEditor.NO_VALUE);
			}
		}
	}
}
