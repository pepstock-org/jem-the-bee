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
package org.pepstock.jem.gwt.client.panels.administration;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.panels.administration.redo.OverviewPanel;

import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class RedoStatementPanel extends VerticalPanel implements ResizeCapable {
	
	private OverviewPanel overview = new OverviewPanel();
	
	/**
	 * 
	 */
	public RedoStatementPanel() {
		super();
		add(overview);
	}
	
	/**
	 * 
	 */
	public void load(){
		overview.load();
	}
	

	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	setSize(Sizes.toString(availableWidth), Sizes.toString(availableHeight));
    	overview.onResize(availableWidth, availableHeight);
    }
    
}