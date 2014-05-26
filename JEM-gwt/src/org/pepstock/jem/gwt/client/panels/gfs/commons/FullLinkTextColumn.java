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
package org.pepstock.jem.gwt.client.panels.gfs.commons;

import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.gwt.client.commons.Images;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.cellview.client.Column;

/**
 * 
 * @author Marco "Fuzzo" Cuccato
 *
 * @param <T> Type of rendered object
 */
public abstract class FullLinkTextColumn extends Column<GfsFile, GfsFile> {

	private static SafeHtmlRenderer<GfsFile> anchorRenderer = new AbstractSafeHtmlRenderer<GfsFile>() {
		@Override
        public SafeHtml render(GfsFile object) {
			SafeHtmlBuilder sb = new SafeHtmlBuilder();
			sb.appendHtmlConstant("<table>");
			sb.appendHtmlConstant("<tr><td align='left' valign='middle'>");
			String imageUrl = null;
	        if (object.isDirectory()) {
	        	imageUrl = "<img src=\""+Images.INSTANCE.folder().getSafeUri().asString()+"\" style=\"border-style: none\" />";
	        } else { 
	        	imageUrl =  "<img src=\""+Images.INSTANCE.file().getSafeUri().asString()+"\" style=\"border-style: none\" />";
	        }
			sb.appendHtmlConstant("<a href=\"javascript:;\">").appendHtmlConstant(imageUrl).appendHtmlConstant("</a>");
			sb.appendHtmlConstant("</td>");
			sb.appendHtmlConstant("<td align='left' valign='middle'>");
			sb.appendHtmlConstant("<a href=\"javascript:;\">").appendEscaped(object.getName()).appendHtmlConstant("</a/>");
			sb.appendHtmlConstant("</td></tr></table>");
			return sb.toSafeHtml();
        }
	};
	
	/**
	 * Contructor that adds an AnchorRenderer to a ClickableTextCell
	 */
	public FullLinkTextColumn() {
		super(new ItemNameCell(anchorRenderer));
		
		setFieldUpdater(new FieldUpdater<GfsFile, GfsFile>() {
			@Override
			public void update(int index, GfsFile object, GfsFile value) {
				// fire inspect event
				onClick(index, object);
			}
		});
	}

	/**
	 * Executed when a click event is fired on the anchor
	 * @param index
	 * @param object
	 * @param value
	 */
	public abstract void onClick(int index, GfsFile object);
	
	/**
	 * Return the anchor's text
	 * @param object The rendered object
	 */
	@Override
	public final GfsFile getValue(GfsFile object){
		return object;
	}
	
	
}