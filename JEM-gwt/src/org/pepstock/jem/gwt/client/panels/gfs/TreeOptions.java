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
package org.pepstock.jem.gwt.client.panels.gfs;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.security.ClientPermissions;
import org.pepstock.jem.node.security.Permissions;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Component which shows output produced by job. Uses highlighter in PLAIN to show data.<br>
 * Every file is requested by RPC only when the user asks for. 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
@SuppressWarnings("javadoc")
public class TreeOptions extends VerticalPanel {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
    public static final String DATA_OPTION = "data";

    public static final String LIBRARY_OPTION = "library";
   
	public static final String SOURCES_OPTION = "sources";
	
	public static final String CLASS_OPTION = "class";

	public static final String BINARY_OPTION = "binary";

	private InspectListener<String> listener = null;
	
	private CellPanel selected = null;
	
	public TreeOptions() {
		setWidth(Sizes.HUNDRED_PERCENT);
		setHeight(Sizes.HUNDRED_PERCENT);
		
		// Root of output tree
		HorizontalPanel header = new HorizontalPanel();
		header.setHorizontalAlignment(ALIGN_LEFT);
		header.setVerticalAlignment(ALIGN_MIDDLE);
		header.setSpacing(8);
		header.add(new Image(Images.INSTANCE.driveOpen24()));
		header.add(new Label("Global file system"));
		
		DisclosurePanel admin = new DisclosurePanel();
		admin.setHeader(header);
		admin.setAnimationEnabled(true);
		admin.setOpen(true);
		

		CellPanel data = createItem("Data", DATA_OPTION, Images.INSTANCE.folderRed24(), Permissions.GFS_DATA);
		CellPanel lib = createItem("Library", LIBRARY_OPTION, Images.INSTANCE.folderRed24(), Permissions.GFS_LIBRARY);
		CellPanel src = createItem("Sources", SOURCES_OPTION, Images.INSTANCE.folderRed24(), Permissions.GFS_SOURCES);
		CellPanel classes = createItem("Class", CLASS_OPTION, Images.INSTANCE.folderRed24(), Permissions.GFS_CLASS);
		CellPanel bin = createItem("Binary", BINARY_OPTION, Images.INSTANCE.folderRed24(), Permissions.GFS_BINARY);

		
		/*-------------------------+
		 | Add options             |
		 +-------------------------*/			
		VerticalPanel statusPanel = new VerticalPanel();
		if (data != null) {
			statusPanel.add(data);
		}
		if (lib != null) {
			statusPanel.add(lib);
		}
		if (src != null) {
			statusPanel.add(src);
		}
		if (classes != null) {
			statusPanel.add(classes);
		}
		if (bin != null) {
			statusPanel.add(bin);
		}
		
		if (statusPanel.getElement().getChildCount() > 0){
			admin.setContent(statusPanel);
			Grid grid = new Grid(1, 1);
			grid.setWidget(0, 0, admin);
			add(grid);
		}
	}
	
	
	
	/**
	 * @param listener the listener to set
	 */
	public void setListener(InspectListener<String> listener) {
		this.listener = listener;
	}

	
	private void selectPanel(CellPanel select){
		if (select.equals(selected)){
			return;
		}
		if (selected != null){
			selected.setStyleName(Styles.INSTANCE.common().adminUnselectedTreeItem());
		}
		select.setStyleName(Styles.INSTANCE.common().adminSelectedTreeItem());
		selected = select;
	}
	
	/**
	 * 
	 * @param description
	 * @param option
	 * @return
	 */
	private final CellPanel createItem(String description, final String option, ImageResource icon, String permission){
		if (!ClientPermissions.isAuthorized(Permissions.GFS, permission)) {
			return null;
		}
	
		/*-------------------------+
		 | Node inspect            |
		 +-------------------------*/	
	
		// this is the inside panel
		final HorizontalPanel options = new HorizontalPanel();
		options.setHorizontalAlignment(ALIGN_LEFT);
		options.setVerticalAlignment(ALIGN_MIDDLE);
		options.setSpacing(8);

		// create the link
		final Anchor anchor = new Anchor(description);
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (listener != null){
					listener.inspect(option);
					selectPanel(options);
				}
			}
		});
		
		// add the icon if present
		if (icon != null) {
			options.add(new Image(icon));
		}
		// add the link
		options.add(anchor);
		setCellWidth(anchor, Sizes.HUNDRED_PERCENT);
		return options;
	}

}