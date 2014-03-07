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
package org.pepstock.jem.gwt.client.panels.administration.redo;

import java.util.Collection;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.administration.commons.AdminPanel;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.persistence.RedoStatement;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class OverviewPanel extends AdminPanel implements ResizeCapable {

	private RedoTableContainer nodes = new RedoTableContainer(new RedoTable());

	private ScrollPanel scroller = new ScrollPanel(nodes);

	/**
	 * 
	 */
	public OverviewPanel() {
		super();
		add(scroller);
	}

	/**
	 * 
	 */
	public void load() {
		Loading.startProcessing();
		
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.STATS_MANAGER.getAllRedoStatements(new GetAllRedoStatementsAsyncCallback());
			}
	    });
	}
	
	private class GetAllRedoStatementsAsyncCallback extends ServiceAsyncCallback<Collection<RedoStatement>> {
		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Get REDO error!").show();
		}

		@Override
		public void onJemSuccess(Collection<RedoStatement> result) {
			if (result != null){
				nodes.getRedosTable().setRowData(result);
			} else {
				new Toast(MessageLevel.WARNING, "The result of get all REDO statements is empty.", "REDO null!").show();
			}
		}
		
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
        }
	}
	
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	super.onResize(availableWidth, availableHeight);
		
		scroller.setHeight(Sizes.toString(getHeight()));
		scroller.setWidth(Sizes.toString(getWidth()));
    }
}