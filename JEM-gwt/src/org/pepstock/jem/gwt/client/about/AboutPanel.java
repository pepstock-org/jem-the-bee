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
package org.pepstock.jem.gwt.client.about;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.AbstractInspector;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.UITools;
import org.pepstock.jem.node.About;
import org.pepstock.jem.util.ColumnIndex;
import org.pepstock.jem.util.RowIndex;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Panel which contains all JEM installation information.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class AboutPanel extends AbstractInspector {

	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.inspector().ensureInjected();
	}
	
	private ScrollPanel main = new ScrollPanel();
	
	/**
	 * Builds panel with all fields
	 * 
	 * @param about 
	 * 
	 */
	public AboutPanel(About about) {
		super(false);
		
		// link to PEPSTOCK site
		Anchor jemSite = new Anchor("www.jemthebee.org");
		jemSite.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Window.open("http://www.jemthebee.org/", "_blank", "");
			}
		});

		// link to PEPSTOCK site
		Anchor site = new Anchor("www.pepstock.org");
		site.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Window.open("http://www.pepstock.org/", "_blank", "");
			}
		});

		// link to PEPSTOCK site
		Anchor docSite = new Anchor("GutHub wiki");
		docSite.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Window.open("https://github.com/pepstock-org/jem-the-bee/wiki", "_blank", "");
			}
		});

		VerticalPanel tableContainer = new VerticalPanel();
		tableContainer.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		tableContainer.setSpacing(10);

		Label aboutLabel = new Label("About JEM");
		aboutLabel.setWidth(Sizes.HUNDRED_PERCENT);
		aboutLabel.setStyleName(Styles.INSTANCE.inspector().title());
		aboutLabel.addStyleName(Styles.INSTANCE.common().bold());
		
		FlexTable aboutTable = new FlexTable();
		aboutTable.setCellPadding(10);
	    aboutTable.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
	    aboutTable.addStyleName(Styles.INSTANCE.common().noWrap());
	    aboutTable.getColumnFormatter().setWidth(ColumnIndex.COLUMN_2, "80%");

	    aboutTable.setHTML(RowIndex.ROW_1,ColumnIndex.COLUMN_1, "JEM the BEE site");
	    aboutTable.setWidget(RowIndex.ROW_1,ColumnIndex.COLUMN_2, jemSite);

	    aboutTable.setHTML(RowIndex.ROW_2,ColumnIndex.COLUMN_1, "Community site");
	    aboutTable.setWidget(RowIndex.ROW_2,ColumnIndex.COLUMN_2, site);

	    aboutTable.setHTML(RowIndex.ROW_3,ColumnIndex.COLUMN_1, "Documentation");
	    aboutTable.setWidget(RowIndex.ROW_3,ColumnIndex.COLUMN_2, docSite);
	    
	    aboutTable.setHTML(RowIndex.ROW_4,ColumnIndex.COLUMN_1, "Version");
	    aboutTable.setHTML(RowIndex.ROW_4,ColumnIndex.COLUMN_2, about.getVersion());

	    aboutTable.setHTML(RowIndex.ROW_5,ColumnIndex.COLUMN_1, "Build time");
	    aboutTable.setHTML(RowIndex.ROW_5,ColumnIndex.COLUMN_2, about.getCreationTime());

		Label licensesLabel = new Label("Licence");
		licensesLabel.setWidth(Sizes.HUNDRED_PERCENT);
		licensesLabel.setStyleName(Styles.INSTANCE.inspector().title());
		licensesLabel.addStyleName(Styles.INSTANCE.common().bold());
	    
		FlexTable licensesTable = new FlexTable();
		licensesTable.setCellPadding(10);
		licensesTable.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
		
		StringBuilder builder = new StringBuilder();
		builder.append("<center>GNU GENERAL PUBLIC LICENSE</center><br/>");
		builder.append("<center>Version 3, 29 June 2007</center><br/>");
		builder.append("<br/>");
		builder.append("<center>Copyright (C) 2007 Free Software Foundation, Inc. http://fsf.org/</center><br/>");
		builder.append("<center>Everyone is permitted to copy and distribute verbatim copies</center><br/>");
		builder.append("<center>of this license document, but changing it is not allowed.</center>");
		licensesTable.setHTML(RowIndex.ROW_1,ColumnIndex.COLUMN_1, builder.toString());
	    
	    // styles
	    UITools.setFlexTableRowStyles(aboutTable, 
	    		Styles.INSTANCE.inspector().rowDark(), 
	    		Styles.INSTANCE.inspector().rowLight());
	    
	    // populate main panel
	    tableContainer.add(aboutLabel);
	    tableContainer.add(aboutTable);

	    tableContainer.add(licensesLabel);
	    tableContainer.add(licensesTable);

	    // add main to scroller
	    main.add(tableContainer);

	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.AbstractInspector#getHeader()
	 */
    @Override
    public FlexTable getHeader() {
	    return new AboutHeader(this);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.AbstractInspector#getContent()
	 */
    @Override
    public Panel getContent() {
    	main.setSize(Sizes.toString(getAvailableWidth()), Sizes.toString(getAvailableHeight()));
	    return main;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.AbstractInspector#getActions()
	 */
    @Override
    public Panel getActions() {
	    return null;
    }

}
