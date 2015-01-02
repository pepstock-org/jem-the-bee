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
package org.pepstock.jem.gwt.client.panels.swarm.commons.inspector.config;

import org.pepstock.jem.gwt.client.commons.Styles;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * A cell that render an ip address in a beautiful way
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class NetworkCell extends AbstractCell<String> {
	
	static {
		Styles.INSTANCE.administration().ensureInjected();
	}

	/**
	 * The html of the image used for contacts.
	 */
	private final String imageHtml;

	/**
	 * @param image
	 */
	public NetworkCell(ImageResource image) {
		this.imageHtml = AbstractImagePrototype.create(image).getHTML();
	}

	@Override
	public void render(Context context, String value, SafeHtmlBuilder sb) {
		// Value can be null, so do a null check..
		if (value == null) {
			return;
		}

		sb.appendHtmlConstant("<table>");

		// Add the contact image.
		sb.appendHtmlConstant("<tr><td>");
		sb.appendHtmlConstant(imageHtml);
		sb.appendHtmlConstant("</td>");

		// Add the ip address.
		sb.appendHtmlConstant("<td align='left' valign='middle'> Ip address: <b>");
		sb.appendEscaped(value);
		sb.appendHtmlConstant("</b></td></tr></table>");
	}
}
