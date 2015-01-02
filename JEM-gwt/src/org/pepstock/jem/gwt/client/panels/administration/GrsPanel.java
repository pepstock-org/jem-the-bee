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
package org.pepstock.jem.gwt.client.panels.administration;



import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.SearchListener;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.administration.commons.AdminPanel;
import org.pepstock.jem.gwt.client.panels.administration.grs.CommandResultPanel;
import org.pepstock.jem.gwt.client.panels.administration.grs.ResourcesSearcher;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * Nodes table container for nodes
 * 
 * , InspectListener<NodeInfoBean>
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class GrsPanel extends AdminPanel implements SearchListener, ResizeCapable {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	private ResourcesSearcher searcher = new ResourcesSearcher();
	
	private CommandResultPanel resultPanel = new CommandResultPanel();

	/**
	 * Creates the UI by the argument (the table)
	 *  
	 * @param nodes table of nodes 
	 */
	public GrsPanel() {
		super();
		
		// add the always visibile searcher
		add(searcher);
		searcher.setSearchListener(this);
	
		resultPanel.setVisible(false);

		// add the second row to the main panel
		add(resultPanel);
	}
	
	/**
	 * 
	 */
	public void load(){
		search("*");
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.SearchListener#search(java.lang.String)
	 */
	@Override
	public void search(final String filter) {
		resultPanel.setVisible(false);
		searcher.setEnabled(false);
		Loading.startProcessing();
		
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.STATS_MANAGER.displayRequestors(filter, new DisplayRequestorsAsyncCallback());
				resultPanel.setVisible(true);
			}
	    });
	}
    
	private class DisplayRequestorsAsyncCallback extends ServiceAsyncCallback<String> {
		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Display command error!").show();
		}

		@Override
		public void onJemSuccess(String result) {
			if (result != null){
				resultPanel.setResult(result);
			} else {
				new Toast(MessageLevel.WARNING, "The result of dispaly requestors command is empty.", "Result empty!").show();
			}
		}
		
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
			searcher.setEnabled(true);
        }
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	super.onResize(availableWidth, availableHeight);
    	
    	// removes 2 spacing of secondRow
    	int resultHeight = getHeight() - 
    			Sizes.SEARCHER_WIDGET_HEIGHT;
    	

    	// removes 3 spacing of secondRow
    	int resultWidth = getWidth();

		resultPanel.setSize(Sizes.toString(resultWidth), Sizes.toString(resultHeight));
		resultPanel.onResize(resultWidth, resultHeight);
    }
    
    
}