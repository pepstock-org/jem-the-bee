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
package org.pepstock.jem.gwt.client.panels.jobs.commons.submitter;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.panels.jobs.commons.Submitter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * ActionsButtonPanel that user can do after a JCL uploading. At the moment we have 2 actions:<br>
 * <br>
 * <ul>
 * <li><b>Submot<b>: to submit JCL</li>
 * <li><b>Cancel<b>: to discard the updates</li>
 * </ul>
 * 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.2
 *
 */
public final class Actions extends HorizontalPanel {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}

	private Submitter submitter = null;

	/**
	 * Creates button bar to submit JCL
	 * 
	 * @param submitter manager to submit JCL
	 */
	public Actions(Submitter submitter) {
		setSpacing(Sizes.SPACING);
		setSubmitter(submitter);

		
		Button saveButton = new Button("Submit", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// do!
				getSubmitter().submit();
			}
		});
		saveButton.addStyleName(Styles.INSTANCE.common().defaultActionButton());
		add(saveButton);

		Button cancelButton = new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// do!
				getSubmitter().cancel();
			}
		});
		add(cancelButton);
	}

	/**
	 * @return the submitter
	 */
	public Submitter getSubmitter() {
		return submitter;
	}

	/**
	 * @param submitter the submitter to set
	 */
	public void setSubmitter(Submitter submitter) {
		this.submitter = submitter;
	}

	
	
}