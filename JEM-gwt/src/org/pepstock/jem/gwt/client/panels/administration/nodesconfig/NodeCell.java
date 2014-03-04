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
package org.pepstock.jem.gwt.client.panels.administration.nodesconfig;

import org.pepstock.jem.NodeInfoBean;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class NodeCell extends AbstractCell<NodeInfoBean> {

	/* (non-Javadoc)
	 * @see com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
	 */
	@Override
	public void render(Context context, NodeInfoBean value, SafeHtmlBuilder sb) {
		if (value != null) {
			sb.appendHtmlConstant("<table cellpadding=0 cellspacing=0><tr><td style='padding: 5px 3px 1px 3px;'>");
			sb.appendEscaped(value.getLabel());
			sb.appendHtmlConstant("</td></tr><tr><td style='font-size: 0.7em; padding: 1px 3px 5px 3px;'>");
			sb.appendEscaped(value.getHostname());
			sb.appendHtmlConstant("</td></tr></table>");
		}
	}
}