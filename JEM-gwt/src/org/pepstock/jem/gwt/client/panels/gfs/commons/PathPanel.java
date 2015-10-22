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
package org.pepstock.jem.gwt.client.panels.gfs.commons;

import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Images;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.util.ColumnIndex;
import org.pepstock.jem.util.RowIndex;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class PathPanel extends FlexTable {

	private static final String PATH_SEPARATOR = "/";
	
	private String[] pathTokens = null;
	
	private InspectListener<GfsFile> listener = null;

	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.fileSystemBrowser().ensureInjected();
	}

	/**
	 * 
	 */
	public PathPanel() {
		addStyleName(Styles.INSTANCE.fileSystemBrowser().pathToken());
		setHeight(Sizes.toString(Sizes.SEARCHER_WIDGET_HEIGHT));
		
		RowFormatter rf = getRowFormatter();
		rf.setVerticalAlign(0, HasVerticalAlignment.ALIGN_MIDDLE);
		
		FlexCellFormatter cf = getFlexCellFormatter();
		cf.setWordWrap(0, 0, false);
		cf.setHorizontalAlignment(RowIndex.ROW_1,ColumnIndex.COLUMN_1, HasHorizontalAlignment.ALIGN_LEFT);
		
		setHTML(0, 0, "Path: ");

		// Home
		final Image homeImage = new Image(Images.INSTANCE.home124());
		homeImage.addClickHandler(new ClickHandler() {
			@Override
            public void onClick(ClickEvent event) {
	        	if (listener != null) {
	        		GfsFile file = new GfsFile();
	        		file.setName(".");
	        		file.setLongName(".");
	        		file.setDirectory(true);
	        		listener.inspect(file);
	        	}
	        }
		});
		homeImage.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				homeImage.getElement().getStyle().setCursor(Cursor.POINTER);
			}
		});
		setWidget(0, 1, homeImage);
		cf.addStyleName(0, 1, Styles.INSTANCE.common().widthFull());
		cf.setWordWrap(0, 1, false);
	}

	/**
	 * @return the listener
	 */
	public InspectListener<GfsFile> getListener() {
		return listener;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(InspectListener<GfsFile> listener) {
		this.listener = listener;
	}

	/**
	 * 
	 * @param file
	 * @param count
	 */
	public void set(GfsFile file, int count) {
		if ("*".equalsIgnoreCase(file.getLongName()) ||
				"/*".equalsIgnoreCase(file.getLongName()) ||
				".".equalsIgnoreCase(file.getLongName()) ||
				"/.".equalsIgnoreCase(file.getLongName())) {
			removeColumns();
			setCount(count);
			return;
		}
		pathTokens = file.getLongName().split(PATH_SEPARATOR);
		createPath();
		setCount(count);
	}
	
	/**
	 * @param count
	 */
	private void setCount(int count){
		int col = getCellCount(0) + 1;
		FlexCellFormatter cf = getFlexCellFormatter();
		cf.setWidth(ColumnIndex.COLUMN_1, col, Sizes.HUNDRED_PERCENT);
		cf.setHorizontalAlignment(RowIndex.ROW_1, col, HasHorizontalAlignment.ALIGN_RIGHT);
		setHTML(0, col, (count > 1) ? count+" files" : count+" file");
		cf.setWordWrap(0, col, false);
	}
	
	/**
	 * 
	 */
	private void removeColumns() {
		int cols = getCellCount(0);
		if (cols > 2) {
			removeCells(0, 2, cols-2);
		}
		adjustSizes();
	}

	/**
	 * 
	 */
	private void createPath() {
		removeColumns();
		FlexCellFormatter cf = getFlexCellFormatter();

		for (int i=0; i<pathTokens.length; i++) {
			int index0 = i*2+2;
			cf.setWordWrap(0, index0, false);
			cf.setHorizontalAlignment(RowIndex.ROW_1, index0, HasHorizontalAlignment.ALIGN_LEFT);

			setHTML(0, index0, PATH_SEPARATOR);
			
			int index1 = i*2+3;
			cf.setWordWrap(0, index1, false);
			cf.setHorizontalAlignment(RowIndex.ROW_1, index1, HasHorizontalAlignment.ALIGN_LEFT);

			setWidget(0, index1, createAnchor(i));
		}
		adjustSizes();
	}
	
	private void adjustSizes() {
		FlexCellFormatter cf = getFlexCellFormatter();
		int i;
		for (i=0; i<getCellCount(0); i++) {
			cf.removeStyleName(0, i, Styles.INSTANCE.common().widthFull());
		}
		cf.addStyleName(0, i-1, Styles.INSTANCE.common().widthFull());
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	private Anchor createAnchor(int index) {
		Anchor anchor = new Anchor(pathTokens[index]);
		anchor.addClickHandler(new MyClickHandler(index));
		return anchor;
	}

	
	/**
	 * 
	 * @author Andrea "Stock" Stocchero
	 *
	 */
	class MyClickHandler implements ClickHandler {

		private int index = 0;
		
		/**
		 * @param index 
		 * 
		 */
        public MyClickHandler(int index) {
	        this.index = index;
        }

		/* (non-Javadoc)
		 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
		 */
        @Override
        public void onClick(ClickEvent event) {
        	if (listener != null) {
        		String path = "";
        		for (int i=0; i<=index; i++) {
        			if (i==0) {
        				path = pathTokens[i];
        			} else { 
        				path = path + PATH_SEPARATOR + pathTokens[i];
        			}
        		}
        		GfsFile file = new GfsFile();
        		file.setName(pathTokens[index]);
        		file.setLongName(path);
        		file.setDirectory(true);
        		listener.inspect(file);
        	}
        }


	}
}
