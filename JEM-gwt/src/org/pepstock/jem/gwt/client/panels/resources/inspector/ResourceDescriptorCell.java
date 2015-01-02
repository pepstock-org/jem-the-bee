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
package org.pepstock.jem.gwt.client.panels.resources.inspector;

import org.pepstock.jem.node.resources.definition.ResourceDescriptor;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class ResourceDescriptorCell extends AbstractCell<ResourceDescriptor> {

	/* (non-Javadoc)
	 * @see com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
	 */
	@Override
	public void render(Context context, ResourceDescriptor value, SafeHtmlBuilder sb) {
		if (value != null) {
		
			sb.appendHtmlConstant("<table cellpadding=0 cellspacing=0><tr><td width='100px' style='font-size: 1.5em; padding: 4px 4px 4px 4px;' valign='top'><b>");
			sb.appendEscaped(value.getType());
			sb.appendHtmlConstant("</b></td><td style='font-size: 0.9em; padding: 4px 4px 4px 4px;' valign='middle'>");
			sb.appendEscaped(value.getDescription());
			sb.appendHtmlConstant("</td></tr></table>");
		}
	}
}