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
package org.pepstock.jem.gwt.client.panels.swarm;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.AnchorTextColumn;
import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.gwt.client.commons.TextFilterableHeader;
import org.pepstock.jem.gwt.client.security.PreferencesKeys;
import org.pepstock.jem.util.filters.fields.NodeFilterFields;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Creates all columns to show into table, defening the sorter too.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class NodesTable extends AbstractTable<NodeInfoBean> {


	/**
	 * Adds all columns to table, defining the sort columns too.
	 */
	@Override
	public IndexedColumnComparator<NodeInfoBean> initCellTable(CellTable<NodeInfoBean> table) {
		
	
		/*-------------------------+
		 | IPADDRESS AND PORT      |
		 +-------------------------*/
	    // construct a column that uses anchorRenderer
	    AnchorTextColumn<NodeInfoBean> name = new AnchorTextColumn<NodeInfoBean>() {
			@Override
			public String getValue(NodeInfoBean object) {
				return object.getLabel();
			}

			@Override
			public void onClick(int index, NodeInfoBean object, String value) {
				getInspectListener().inspect(object);
			}
		};
		name.setSortable(true);
		table.addColumn(name, new TextFilterableHeader("Name", NodeFilterFields.NAME.getName()));
		
		/*-------------------------+
		 | HOST NAME               |
		 +-------------------------*/
		TextColumn<NodeInfoBean> hostname = new TextColumn<NodeInfoBean>() {
			@Override
			public String getValue(NodeInfoBean nodeInfoBean) {
				return nodeInfoBean.getHostname();
			}
		};
		hostname.setSortable(true);
		table.addColumn(hostname, new TextFilterableHeader("Hostname", NodeFilterFields.HOSTNAME.getName()));

		/*-------------------------+
		 | ENVIRONMENT             |
		 +-------------------------*/
		TextColumn<NodeInfoBean> executionEnvironment = new TextColumn<NodeInfoBean>() {
			@Override
			public String getValue(NodeInfoBean nodeInfoBean) {
				return nodeInfoBean.getExecutionEnvironment().getEnvironment();
			}
		};
		executionEnvironment.setSortable(true);
		table.addColumn(executionEnvironment, new TextFilterableHeader("Environment", NodeFilterFields.ENVIRONMENT.getName()));

		/*-------------------------+
		 | STATUS                  |
		 +-------------------------*/
		StatusColumn status = new StatusColumn();
		status.setSortable(true);
		table.addColumn(status, new TextFilterableHeader("Status", NodeFilterFields.STATUS.getName()));
		
		/*-------------------------+
		 | OS NAME                 |
		 +-------------------------*/
		TextColumn<NodeInfoBean> systemName = new TextColumn<NodeInfoBean>() {
			@Override
			public String getValue(NodeInfoBean nodeInfoBean) {
				return nodeInfoBean.getSystemName();
			}
		};
		systemName.setSortable(true);
		table.addColumn(systemName, new TextFilterableHeader("OS", NodeFilterFields.OS.getName()));

		
		return new NodesComparator(0, PreferencesKeys.SWARM_SORT);

	}

	private static class StatusColumn extends TextColumn<NodeInfoBean> {

		@Override
		public String getValue(NodeInfoBean object) {
			if (object == null || object.getStatus() == null) {
				return "";
			}
			return object.getStatus();	
		}

		@Override
		public void render(Context context, NodeInfoBean object, SafeHtmlBuilder sb) {
			if (object == null || object.getStatus() == null || object.getStatus().trim().isEmpty()) {
				return;
			}
			String statusString = object.getStatus();
			NodeStatusImages statusObject;
			if (statusString.equals(NodeStatusImages.UNKNOWN.toString())) {
				statusObject = NodeStatusImages.UNKNOWN;
			} else if (statusString.equals(NodeStatusImages.STARTING.toString())) {
				statusObject = NodeStatusImages.STARTING;
			} else if (statusString.equals(NodeStatusImages.INACTIVE.toString())) {
				statusObject = NodeStatusImages.INACTIVE;
			} else if (statusString.equals(NodeStatusImages.ACTIVE.toString())) {
				statusObject = NodeStatusImages.ACTIVE;
			} else if (statusString.equals(NodeStatusImages.DRAINED.toString())) {
				statusObject = NodeStatusImages.DRAINED;
			} else if (statusString.equals(NodeStatusImages.DRAINING.toString())) {
				statusObject = NodeStatusImages.DRAINING;
			} else if (statusString.equals(NodeStatusImages.SHUTTING_DOWN.toString())) {
				statusObject = NodeStatusImages.SHUTTING_DOWN;
			} else {
				// the default!
				statusObject = NodeStatusImages.INACTIVE;
			}
			
			sb.appendHtmlConstant("<table>");
			// Add the contact image.
			sb.appendHtmlConstant("<tr><td>");
			String imageHtml = AbstractImagePrototype.create(statusObject.getImage()).getHTML();
			sb.appendHtmlConstant(imageHtml);
			sb.appendHtmlConstant("</td>");
			// Add the name and address.
			sb.appendHtmlConstant("<td align='left' valign='middle'>");
			sb.appendEscaped(statusString);
			if (!object.isOperational()){
				sb.appendEscaped(" (not operational)");
			}
			sb.appendHtmlConstant("</td></tr></table>");
		}
	}
	
	/**
	 * Map a node status to a colored led image 
	 * @author Marco "Fuzzo" Cuccato
	 */
	enum NodeStatusImages {
		
		UNKNOWN("UNKNOWN", Images.INSTANCE.ledGray18()),
		STARTING("STARTING", Images.INSTANCE.ledLightGreen18()),
		INACTIVE("INACTIVE", Images.INSTANCE.ledBlue18()),
		ACTIVE("ACTIVE", Images.INSTANCE.ledGreen18()),
		DRAINED("DRAINED", Images.INSTANCE.ledRed18()),
		DRAINING("DRAINING", Images.INSTANCE.ledYellow18()),
		SHUTTING_DOWN("SHUTTING_DOWN", Images.INSTANCE.ledGray18());

		private String value;
		private ImageResource image;

		private NodeStatusImages(String value, ImageResource image) {
			this.value = value;
			this.image = image;
		}
		
		public ImageResource getImage() {
			return image;
		}
		
		public String toString() {
			return value;
		}
	}
	
}