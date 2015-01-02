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
package org.pepstock.jem.gwt.client.panels.jobs.input;

import org.pepstock.jem.gwt.client.commons.AbstractInspector;
import org.pepstock.jem.gwt.client.events.EventBus;
import org.pepstock.jem.gwt.client.events.SubmitterClosedEvent;
import org.pepstock.jem.gwt.client.events.SubmitterClosedEventHandler;
import org.pepstock.jem.gwt.client.panels.jobs.commons.JobsBaseActions;
import org.pepstock.jem.node.Queues;

/**
 * @see org.pepstock.jem.gwt.client.panels.jobs.commons.JobsBaseActions
 * @author Andrea "Stock" Stocchero
 *
 */
public class InputActions extends JobsBaseActions {
	
	/**
	 * @see org.pepstock.jem.gwt.client.panels.jobs.commons.JobsBaseActions#JobsActions(String)
	 */
	public InputActions() {
		super(Queues.INPUT_QUEUE);
		
		// add handler to manage submitter switch
		EventBus.INSTANCE.addHandler(SubmitterClosedEvent.TYPE, new SubmitterClosedEventHandler() {
			@Override
			public void onSubmitterClosed(SubmitterClosedEvent event) {
				// close in any case the current opened submitter
				((AbstractInspector)event.getSource()).hide();
				// check if i should switch
				if (event.isSwitchSubmitter()) {
					if (event.getSource() instanceof MultiDragAndDropSubmitter) {
						openSubmitter(true);
					} else {
						openSubmitter(false);
					}
				} else {
					getSearcher().refresh();
				}
			}
		});
	}

}