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
package org.pepstock.jem.gwt.client.panels.jobs.running;

import org.pepstock.jem.Job;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.TextColumn;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public abstract class RunningTextColumn extends TextColumn<Job> {
	
	@Override
	public void render(Context context, Job job, SafeHtmlBuilder sb) {
		// Value can be null, so do a null check..
		if (job == null) {
			return;
		}
		if (job.getRunningStatus() == Job.WAITING_FOR_RESOURCES){
			sb.appendHtmlConstant(createWaitingLabel(getValue(job)));
		} else {
			super.render(context, job, sb);
		}
	}
	
	private String createWaitingLabel(String value){
		StringBuilder sb = new StringBuilder();
		sb.append("<font color='#c0c0c0'>").append(value).append("</font>");
		return sb.toString();
	}
}