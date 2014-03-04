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
package org.pepstock.jem.gwt.client.panels.roles;

import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.AnchorTextColumn;
import org.pepstock.jem.gwt.client.commons.JemConstants;
import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.gwt.client.commons.TextFilterableHeader;
import org.pepstock.jem.node.security.Role;
import org.pepstock.jem.util.filters.fields.RoleFilterFields;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.view.client.SelectionModel;

/**
 * Creates all columns to show into table, defening teh sorter too.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class RolesTable extends AbstractTable<Role> {	
	
	/**
	 * Adds all columns to table, defining the sort columns too.
	 */
    @Override
    public IndexedColumnComparator<Role> initCellTable(CellTable<Role> table) {
		@SuppressWarnings("unchecked")
		final SelectionModel<Role> selectionModel = (SelectionModel<Role>) table.getSelectionModel();
		Column<Role, Boolean> checkColumn = new Column<Role, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(Role role) {
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
				for (Role r : getTable().getVisibleItems()) {
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
				for (Role r : getTable().getVisibleItems()) {
					getTable().getSelectionModel().setSelected(r, value);
				}
			}
		});
				
		table.setColumnWidth(checkColumn, 23, Unit.PX);
		table.addColumn(checkColumn, checkHeader);

		AnchorTextColumn<Role> name = new AnchorTextColumn<Role>() {
			@Override
			public void onClick(int index, Role object, String value) {
				getInspectListener().inspect(object);
			}

			@Override
			public String getValue(Role role) {
				return role.getName();
			}
		};
		name.setSortable(true);
		table.addColumn(name, new TextFilterableHeader("Name", RoleFilterFields.NAME.getName()));
		
		TextColumn<Role> removable = new TextColumn<Role>() {
			@Override
			public String getValue(Role role) {
				return String.valueOf(role.isRemovable());
			}
		};
		removable.setSortable(true);
		table.addColumn(removable, new TextFilterableHeader("Removable", RoleFilterFields.REMOVABLE.getName()));

		
		PermissionsColumn permissions = new PermissionsColumn();
		table.addColumn(permissions, new TextFilterableHeader("Permissions", RoleFilterFields.PERMISSIONS.getName()));
		
		UsersColumn users = new UsersColumn();
		table.addColumn(users, new TextFilterableHeader("Users", RoleFilterFields.USERS.getName()));

		TextColumn<Role> lastModified = new TextColumn<Role>() {
			@Override
			public String getValue(Role role) {
				if (role.getLastModified() == null){
					return "";
				}
				return JemConstants.DATE_TIME_FULL.format(role.getLastModified()); 

			}
		};
		lastModified.setSortable(true);
		table.addColumn(lastModified, new TextFilterableHeader("Modified", RoleFilterFields.MODIFIED.getName(), RoleFilterFields.MODIFIED.getPattern()));

		TextColumn<Role> user = new TextColumn<Role>() {
			@Override
			public String getValue(Role role) {
				return role.getUser() != null ? role.getUser() : "";
			}
		};
		user.setSortable(true);
		table.addColumn(user, new TextFilterableHeader("Modified by", RoleFilterFields.MODIFIED_BY.getName()));
		
        return new RolesComparator(1);
	}
    
    private static class UsersColumn extends TextColumn<Role> {
		private final String imageHtml = AbstractImagePrototype.create(Images.INSTANCE.user()).getHTML();
		
		@Override
		public String getValue(Role role) {
			if (role.getUsers().isEmpty()){
				return "";
			}
			return role.getUsers().toString();
		}
		@Override
		public void render(Context context, Role value, SafeHtmlBuilder sb) {
			// Value can be null, so do a null check..
			if (value == null) {
				return;
			} else if (value.getUsers().isEmpty()){
				return;
			}

			sb.appendHtmlConstant("<table>");

			// Add the contact image.
			sb.appendHtmlConstant("<tr><td>");
			sb.appendHtmlConstant(imageHtml);
			sb.appendHtmlConstant("</td>");

			// Add the name and address.
			sb.appendHtmlConstant("<td align='left' valign='middle'> Users: <b>");
			sb.appendEscaped(value.getUsers().toString());
			sb.appendHtmlConstant("</b></td></tr></table>");
		}
    }
    
    private static class PermissionsColumn extends TextColumn<Role> {
		private final String imageHtml = AbstractImagePrototype.create(Images.INSTANCE.permission()).getHTML();
		
		@Override
		public String getValue(Role role) {
			if (role.getPermissions().isEmpty()){
				return "";
			}
			return role.getPermissions().toString();
		}
		
		@Override
		public void render(Context context, Role value, SafeHtmlBuilder sb) {
			// Value can be null, so do a null check..
			if (value == null) {
				return;
			} else if (value.getPermissions().isEmpty()){
				return;
			}

			sb.appendHtmlConstant("<table>");

			// Add the contact image.
			sb.appendHtmlConstant("<tr><td>");
			sb.appendHtmlConstant(imageHtml);
			sb.appendHtmlConstant("</td>");

			// Add the name and address.
			sb.appendHtmlConstant("<td align='left' valign='middle'> Permissions: <b>");
			sb.appendEscaped(value.getPermissions().toString());
			sb.appendHtmlConstant("</b></td></tr></table>");
		}
    }
}