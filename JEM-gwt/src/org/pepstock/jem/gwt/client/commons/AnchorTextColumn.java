/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.commons;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Anchor;

/**
 * A table column that contains {@link Anchor}
 * @author Marco "Fuzzo" Cuccato
 *
 * @param <T> Type of rendered object
 */
public abstract class AnchorTextColumn<T> extends Column<T, String> {

	private static SafeHtmlRenderer<String> anchorRenderer = new AbstractSafeHtmlRenderer<String>() {
		@Override
		public SafeHtml render(String object) {
			SafeHtmlBuilder sb = new SafeHtmlBuilder();
			sb.appendHtmlConstant("<a href=\"javascript:;\">")
					.appendEscaped(object).appendHtmlConstant("</a>");
			return sb.toSafeHtml();
		}
	};

	/**
	 * Constructor that adds an AnchorRenderer to a ClickableTextCell
	 */
	public AnchorTextColumn() {
		super(new ClickableTextCell(anchorRenderer));
		
		setFieldUpdater(new FieldUpdater<T, String>() {
			@Override
			public void update(int index, T object, String value) {
				// fire inspect event
				onClick(index, object, value);
			}
		});

		
	}

	/**
	 * Executed when a click event is fired on the anchor
	 * @param index the current row index of the object
	 * @param object the base object to be updated
	 * @param value the new value of the field being updated
	 */
	public abstract void onClick(int index, T object, String value);
	
	/**
	 * Returns the anchor's text
	 * @param object The rendered object
	 */
	@Override
	public abstract String getValue(T object);

}