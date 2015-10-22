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
package org.pepstock.jem.gwt.client.panels.jobs.running;

import java.util.Date;

import org.pepstock.jem.Jcl;
import org.pepstock.jem.Job;
import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.AnchorTextColumn;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.gwt.client.commons.TextFilterableHeader;
import org.pepstock.jem.gwt.client.commons.TimeDisplayUtils;
import org.pepstock.jem.util.filters.fields.JobFilterFields;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.view.client.SelectionModel;

/**
 * Creates all columns to show into table, defening the sorter too.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class RunningTable extends AbstractTable<Job> {	
	
	/**
	 * Builds the running table
	 * @param filterableHeaders <code>true</code> if contains filterable headers
	 */
	public RunningTable(boolean filterableHeaders) {
		super(filterableHeaders);
	}

	/**
	 * Adds all columns to table, defining the sort columns too.
	 */
	@Override
	public IndexedColumnComparator<Job> initCellTable(CellTable<Job> table) {
		@SuppressWarnings("unchecked")
		final SelectionModel<Job> selectionModel = (SelectionModel<Job>) table.getSelectionModel();
		Column<Job, Boolean> checkColumn = new Column<Job, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(Job job) {
				return selectionModel.isSelected(job);
			}
		};

		CheckboxCell headerCheckBox = new CheckboxCell(true, false);
		Header<Boolean> checkHeader = new Header<Boolean>(headerCheckBox) {
			// sets HEADER
			@Override
			public Boolean getValue() {
				// check if is empty
				if (getTable().getVisibleItems().isEmpty()) {
					return false;
				}
				
				// scans items
				for (Job j : getTable().getVisibleItems()) {
					// if at least one is selected, headr couldn't be chosen
					if (!getTable().getSelectionModel().isSelected(j)) {
						return false;
					}
				}
				// here all elements are selected
				return true;
			}
		};
		
		// updater 
		checkHeader.setUpdater(new ValueUpdater<Boolean>() {
			@Override
			public void update(Boolean value) {
				for (Job j : getTable().getVisibleItems()) {
					getTable().getSelectionModel().setSelected(j, value);
				}
			}
		});
				
		table.setColumnWidth(checkColumn, AbstractTable.DEFAULT_CHECK_COLUMN_WIDTH, Unit.PX);

	    // construct a column that uses anchorRenderer
	    AnchorTextColumn<Job> name = new AnchorTextColumn<Job>() {
			@Override
			public String getValue(Job object) {
				return object.getName();
			}

			@Override
			public void onClick(int index, Job object, String value) {
				getInspectListener().inspect(object);
			}
		};
		name.setSortable(true);

	    RunningTextColumn type = new RunningTextColumn() {
			@Override
			public String getValue(Job job) {
				return job.getJcl().getType();
			}
		};
		type.setSortable(true);

		RunningTextColumn userid = new RunningTextColumn() {
			@Override
			public String getValue(Job job) {
				// if surrogates, use ONLY user in JCL definition
				return (job.isUserSurrogated()) ? job.getJcl().getUser() : job.getUser();
			}
		
		};
		userid.setSortable(true);
		
		RunningTextColumn step = new RunningTextColumn() {
			@Override
			public String getValue(Job job) {
				if (job.getCurrentStep() != null){
					return job.getCurrentStep().getName();
				} else { 
					return "";
				}
			}
		};
		
		RunningTextColumn domain = new RunningTextColumn() {
			@Override
			public String getValue(Job job) {
				Jcl jcl = job.getJcl();
				return jcl.getDomain();
			}

		};
		domain.setSortable(true);
		
		RunningTextColumn affinity = new RunningTextColumn() {
			@Override
			public String getValue(Job job) {
				Jcl jcl = job.getJcl();
				return jcl.getAffinity();
			}
		};
		affinity.setSortable(true);

		RunningTextColumn runningTime = new RunningTextColumn() {
			@Override
			public String getValue(Job job) {
				Date startedTime = job.getStartedTime();
				return TimeDisplayUtils.getReadableTimeDiff(startedTime); 
			}
		};
		runningTime.setSortable(true);

		RunningTextColumn memory = new RunningTextColumn() {
			@Override
			public String getValue(Job job) {
				return String.valueOf(job.getJcl().getMemory());
			}
		};
		memory.setSortable(true);

		RunningTextColumn member = new RunningTextColumn() {
			@Override
			public String getValue(Job job) {
				return job.getMemberLabel();
			}
		};
		member.setSortable(true);
		
		/*
		 * Add the headers, type based on filterableHeader value
		 */
		table.addColumn(checkColumn, checkHeader);
		if (hasFilterableHeaders()) {
			table.addColumn(name, new TextFilterableHeader("Name", JobFilterFields.NAME.getName()));
			table.addColumn(type, new TextFilterableHeader("Type", JobFilterFields.TYPE.getName()));
			table.addColumn(userid, new TextFilterableHeader("User", JobFilterFields.USER.getName()));
			table.addColumn(step, new TextFilterableHeader("Step", JobFilterFields.STEP.getName()));
			table.addColumn(domain, new TextFilterableHeader("Domain", JobFilterFields.DOMAIN.getName()));
			table.addColumn(affinity, new TextFilterableHeader("Affinity", JobFilterFields.AFFINITY.getName()));
			table.addColumn(runningTime, new TextFilterableHeader("Running time", JobFilterFields.RUNNING_TIME.getName(), JobFilterFields.RUNNING_TIME.getPattern()));
			table.addColumn(memory, new TextFilterableHeader("Memory (MB)", JobFilterFields.MEMORY.getName()));
			table.addColumn(member, new TextFilterableHeader("Member", JobFilterFields.MEMBER.getName()));
		} else {
			table.addColumn(name, "Name");
			table.addColumn(type, "Type");
			table.addColumn(userid, "User");
			table.addColumn(step, "Step");
			table.addColumn(domain, "Domain");
			table.addColumn(affinity, "Affinity");
			table.addColumn(runningTime, "Running time");
			table.addColumn(memory, "Memory (MB)");
			table.addColumn(member, "Member");
		}

		return new RunningJobComparator(1);

	}

}