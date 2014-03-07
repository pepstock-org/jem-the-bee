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
package org.pepstock.jem.gwt.client.panels;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.ViewStackPanel;
import org.pepstock.jem.gwt.client.panels.gfs.BinaryViewer;
import org.pepstock.jem.gwt.client.panels.gfs.ClassViewer;
import org.pepstock.jem.gwt.client.panels.gfs.DataViewer;
import org.pepstock.jem.gwt.client.panels.gfs.LibraryViewer;
import org.pepstock.jem.gwt.client.panels.gfs.SourcesViewer;
import org.pepstock.jem.gwt.client.panels.gfs.TreeOptions;

import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Administration entry point. Under construction
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class Gfs extends SplitLayoutPanel implements InspectListener<String>, ResizeCapable {

	private ViewStackPanel viewStack = new ViewStackPanel();
	
	private DataViewer data = new DataViewer();
	
	private LibraryViewer lib = new LibraryViewer();
	
	private SourcesViewer src = new SourcesViewer();
	
	private ClassViewer clazz = new ClassViewer();
	
	private BinaryViewer bin = new BinaryViewer();
	
	private TreeOptions options = new TreeOptions();
	
	private boolean resized = false;

	/**
	 * Empty constructor
	 */
	public Gfs() {
		options.setListener(this);
		addWest(options, Sizes.SPLIT_PANEL_WEST_DEFAULT_SIZE);
		
		viewStack.add(new VerticalPanel());
		viewStack.add(data);
		viewStack.add(lib);
		viewStack.add(src);
		viewStack.add(clazz);
		viewStack.add(bin);
		add(viewStack);

	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.InspectListener#inspect(java.lang.Object)
	 */
    @Override
    public void inspect(String option) {
		if (option.equalsIgnoreCase(TreeOptions.DATA_OPTION)) {
			viewStack.showStack(1);
			data.load();
		} else if (option.equalsIgnoreCase(TreeOptions.LIBRARY_OPTION)) {
			viewStack.showStack(2);
			lib.load();
		} else if (option.equalsIgnoreCase(TreeOptions.SOURCES_OPTION)) {
			viewStack.showStack(3);
			src.load();
		} else if (option.equalsIgnoreCase(TreeOptions.CLASS_OPTION)) {
			viewStack.showStack(4);
			clazz.load();
		} else if (option.equalsIgnoreCase(TreeOptions.BINARY_OPTION)) {
			viewStack.showStack(5);
			bin.load();
		}
	}

    @Override
    public void onResize() {
    	resized = options.getOffsetWidth() != Sizes.SPLIT_PANEL_WEST_DEFAULT_SIZE;
        super.onResize();
        onResize(getOffsetWidth(), getOffsetHeight());
    }
    
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
	   setSize(Sizes.toString(availableWidth), Sizes.toString(availableHeight));
	
	   int desiredWidth = availableWidth - ( resized ? options.getOffsetWidth() : Sizes.SPLIT_PANEL_WEST_DEFAULT_SIZE) - 
			   Sizes.SPLIT_PANEL_SEPARATOR;  
	   int desiredHeight = availableHeight;
	   
		for (Widget w : viewStack.getWidgets()) {
			w.setSize(Sizes.toString(desiredWidth), Sizes.toString(desiredHeight));
			if (w instanceof ResizeCapable) {
				((ResizeCapable) w).onResize(desiredWidth, desiredHeight);
			}
		}
  
    }

}