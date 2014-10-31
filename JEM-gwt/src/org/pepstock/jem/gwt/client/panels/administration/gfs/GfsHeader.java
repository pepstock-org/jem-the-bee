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
package org.pepstock.jem.gwt.client.panels.administration.gfs;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.panels.administration.commons.Instances;
import org.pepstock.jem.node.stats.FileSystemUtilization;
import org.pepstock.jem.node.stats.LightMemberSample;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Header of GFS panel, where you can choose the file system to show
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class GfsHeader extends FlexTable {
	
	/**
	 * Fixed HEIGHT ofthis component
	 */
	public static final int HEIGHT = 40;
	
	final ListBox typeCombo = new ListBox();
	
	private InspectListener<String> listener = null;

	/**
	 * Empty constructor
	 */
	public GfsHeader() {
		setHeight(HEIGHT+"px");
		setWidth(Sizes.HUNDRED_PERCENT);
		
		RowFormatter rf = getRowFormatter();
		rf.setVerticalAlign(0, HasVerticalAlignment.ALIGN_MIDDLE);
		
		FlexCellFormatter cf = getFlexCellFormatter();
		
		cf.setWidth(0, 0, Sizes.HUNDRED_PERCENT);
		cf.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		setHTML(0, 0, "FileSystem:");
		
		// set 30% of space for the combo box
		cf.setWidth(0, 1, "30%");
		
		// logoff button (and handler)
		typeCombo.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				fireSelection();
			}
		});
		cf.setWordWrap(0, 1, false);
		cf.setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		cf.setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_RIGHT);
		setWidget(0, 1, typeCombo);
	}

	/**
	 * If combo is empty, it loads all defined file systems names
	 */
    public void load() {
    	if (typeCombo.getItemCount() == 0){
    		LightMemberSample msample = Instances.getLastSample().getMembers().iterator().next();
    		for (FileSystemUtilization fsUtil : msample.getFileSystems()){
    			typeCombo.addItem(fsUtil.getName());
    		}
        	typeCombo.setSelectedIndex(0);
    	}
    	fireSelection();
    }
    
    /**
     * Informs the listener that an item in the combo has been chosen
     */
    private void fireSelection(){
		if (listener != null){
			listener.inspect(typeCombo.getItemText(typeCombo.getSelectedIndex()));
		}
    }


	/**
	 * @return the listener
	 */
	public InspectListener<String> getListener() {
		return listener;
	}


	/**
	 * @param listener the listener to set
	 */
	public void setListener(InspectListener<String> listener) {
		this.listener = listener;
	}   
}