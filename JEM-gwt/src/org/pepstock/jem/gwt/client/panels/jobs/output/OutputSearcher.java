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
package org.pepstock.jem.gwt.client.panels.jobs.output;

import org.pepstock.jem.gwt.client.panels.jobs.commons.JobsSearcher;
import org.pepstock.jem.gwt.client.security.PreferencesKeys;

import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.CheckBox;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class OutputSearcher extends JobsSearcher {
	
	private CheckBox history = null;

	/**
	 * @param preferenceKey
	 */
	public OutputSearcher() {
		super(PreferencesKeys.JOB_SEARCH_OUTPUT);
		history = new CheckBox();
		history.setText("Search on history", Direction.LTR);
		add(history);
	}
	
	/**
	 * Returns true if the user checked the box to perform the query on database
	 * @return true if the user checked the box to perform the query on database
	 */
	public boolean isHistorySelected(){
		return history.getValue();
	}

}
