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
package org.pepstock.jem.gwt.client.notify;

import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.gwt.client.commons.NodeStatusImages;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Table which shows all emitted toasts
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class NotifyTable extends AbstractTable<ToastMessage> {

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.AbstractTable#initCellTable(com.google.gwt.user.cellview.client.CellTable)
	 */
	@Override
	public IndexedColumnComparator<ToastMessage> initCellTable(CellTable<ToastMessage> table) {

		/*-------------------------+
		 | Time                    |
		 +-------------------------*/
		TextColumn<ToastMessage> time = new TextColumn<ToastMessage>() {
			@Override
			public String getValue(ToastMessage entry) {
				DateTimeFormat dtf = DateTimeFormat.getFormat("HH:mm:ss");
				return dtf.format(entry.getDate()); 
			}
		};
		time.setSortable(true);
		table.addColumn(time, "Time");

		/*-------------------------+
		 | Level                   |
		 +-------------------------*/
		LevelColumn level = new LevelColumn();
		level.setSortable(true);
		table.addColumn(level, "Level");

		
		/*-------------------------+
		 | Title                   |
		 +-------------------------*/
	    TextColumn<ToastMessage> title = new TextColumn<ToastMessage>() {
			@Override
			public String getValue(ToastMessage object) {
				return object.getTitle();
			}
		};
		title.setSortable(true);
		table.addColumn(title, "Title");
		
		/*-------------------------+
		 | Message                 |
		 +-------------------------*/
		TextColumn<ToastMessage> message = new TextColumn<ToastMessage>() {
			@Override
			public String getValue(ToastMessage entry) {
				return entry.getMessage();
			}
			
			@Override
			public void render(Context context, ToastMessage object, SafeHtmlBuilder sb) {
				if (object == null) {
					return;
				}
				// use this renderer because the message could be HTML
				sb.appendHtmlConstant(object.getMessage());
			}

		};
		message.setSortable(true);
		table.addColumn(message, "Message");

		// sets comparator
		return new NotifyComparator(0);
	}
	
	private static class LevelColumn extends TextColumn<ToastMessage> {

		@Override
		public String getValue(ToastMessage object) {
			return String.valueOf(object.getLevel());	
		}

		@Override
		public void render(Context context, ToastMessage object, SafeHtmlBuilder sb) {
			if (object == null) {
				return;
			}
			
			NodeStatusImages statusObject;
			switch (object.getLevel()) {
			case INFO:
				statusObject = NodeStatusImages.ACTIVE;
				break;
			case WARNING:
				statusObject = NodeStatusImages.DRAINING;
				break;
			case ERROR:
				statusObject = NodeStatusImages.DRAINED;
				break;
			default:
				statusObject = NodeStatusImages.UNKNOWN;
				break;
			}
			
			sb.appendHtmlConstant("<table width=100%>");
			// Add the contact image.
			sb.appendHtmlConstant("<tr width=100%><td width=100% align='center'>");
			String imageHtml = AbstractImagePrototype.create(statusObject.getImage()).getHTML();
			sb.appendHtmlConstant(imageHtml);
			sb.appendHtmlConstant("</td></tr></table>");
		}
	}
	
}
