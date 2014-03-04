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
package org.pepstock.jem.gwt.client.about;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.AbstractInspector;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.UITools;
import org.pepstock.jem.node.About;
import org.pepstock.jem.node.NodeLicense;

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
		Anchor site = new Anchor("www.pepstock.org");
		site.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Window.open("http://www.pepstock.org/", "_blank", "");
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
	    aboutTable.getColumnFormatter().setWidth(1, "80%");

	    aboutTable.setHTML(0, 0, "Site");
	    aboutTable.setWidget(0, 1, site);
	    
	    aboutTable.setHTML(1, 0, "Version");
	    aboutTable.setHTML(1, 1, about.getVersion());

	    aboutTable.setHTML(2, 0, "Build time");
	    aboutTable.setHTML(2, 1, about.getCreationTime());

		Label licensesLabel = new Label("Installed Licences");
		licensesLabel.setWidth(Sizes.HUNDRED_PERCENT);
		licensesLabel.setStyleName(Styles.INSTANCE.inspector().title());
		licensesLabel.addStyleName(Styles.INSTANCE.common().bold());
	    
		FlexTable licensesTable = new FlexTable();
		licensesTable.setCellPadding(10);
		licensesTable.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
	    
		if (!about.getLicenses().isEmpty()) {
		    int row = 0;
		    for (NodeLicense license : about.getLicenses()) {
		    	StringBuilder builder = new StringBuilder();
		    	builder.append("Module: <b>"+license.getModule()+"</b>, ");
		    	builder.append("File name: <b>"+license.getFileName()+"</b><br/>");
		    	builder.append("Subject: <b>"+license.getSubject()+"</b>, ");
		    	builder.append("Email: <b>"+license.getEmail()+"</b>, ");
		    	builder.append("Issue date: <b>"+license.getIssuedate()+"</b>, ");
		    	builder.append("Expire date: <b>"+license.getExpiredate()+"</b>, ");
		    	builder.append("Max nodes: <b>"+license.getMaxnodes()+"</b>");
		    	licensesTable.setHTML(row, 0, builder.toString());
			    row++;
		    }
		} else {
			licensesTable.setHTML(0, 0, "(none)");
		}
	    
	    // styles
	    UITools.setFlexTableRowStyles(aboutTable, 
	    		Styles.INSTANCE.inspector().rowDark(), 
	    		Styles.INSTANCE.inspector().rowLight());
	    
	    UITools.setFlexTableRowStyles(licensesTable, 
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
