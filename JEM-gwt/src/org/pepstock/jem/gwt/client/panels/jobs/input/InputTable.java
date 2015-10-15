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

import org.pepstock.jem.Jcl;
import org.pepstock.jem.Job;
import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.AnchorTextColumn;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.JemConstants;
import org.pepstock.jem.gwt.client.commons.TextFilterableHeader;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.commons.UpdateListener;
import org.pepstock.jem.gwt.client.log.LogClient;
import org.pepstock.jem.gwt.client.security.ClientPermissions;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.util.filters.fields.JobFilterFields;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.SelectionModel;

/**
 * Creates all columns to show into table, defening the sorter too.
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class InputTable extends AbstractTable<Job> {	
	
	/**
	 * Builds the input table
	 * @param filterableHeaders <code>true</code> if contains filterable headers
	 */
	public InputTable(boolean filterableHeaders) {
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
			// imposta lo stato dell'header!
			@Override
			public Boolean getValue() {
				// se e' vuoto, niente e' selezionato/selezionabile
				if (getTable().getVisibleItems().isEmpty()) {
					return false;
				}
				
				// altrimenti testo
				for (Job j : getTable().getVisibleItems()) {
					// se almeno un elemento non e' selezionato, l'header non deve essere selezionato
					if (!getTable().getSelectionModel().isSelected(j)) {
						return false;
					}
				}
				// altrimenti se arrivo qui, tutti gli elementi sono selezionati
				return true;
			}
		};
		
		// updater che seleziona o deseleziona tutti gli elementi visibili in base al "valore" dell'header
		checkHeader.setUpdater(new ValueUpdater<Boolean>() {
			@Override
			public void update(Boolean value) {
				for (Job j : getTable().getVisibleItems()) {
					getTable().getSelectionModel().setSelected(j, value);
				}
			}
		});
		table.setColumnWidth(checkColumn, 23, Unit.PX);
		table.addColumn(checkColumn, checkHeader);
		
		/*-------------------------+
		 | NAME                    |
		 +-------------------------*/
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
		if (hasFilterableHeaders()) {
			table.addColumn(name, new TextFilterableHeader("Name", JobFilterFields.NAME.getName()));
		} else {
			table.addColumn(name, "Name");
		}
		
		/*-------------------------+
		 | TYPE                    |
		 +-------------------------*/
		TextColumn<Job> type = new TextColumn<Job>() {
			@Override
			public String getValue(Job job) {
				return job.getJcl().getType();
			}
		};
		type.setSortable(true);
		if (hasFilterableHeaders()) {
			table.addColumn(type, new TextFilterableHeader("Type", JobFilterFields.TYPE.getName()));
		} else {
			table.addColumn(type, "Type");
		}

		/*-------------------------+
		 | USER                    |
		 +-------------------------*/
		TextColumn<Job> userid = new TextColumn<Job>() {
			@Override
			public String getValue(Job job) {
				// if surrogates, use ONLY user in JCL definition
				return (job.isUserSurrogated()) ? job.getJcl().getUser() : job.getUser();
			}
		};
		userid.setSortable(true);
		if (hasFilterableHeaders()) {
			table.addColumn(userid, new TextFilterableHeader("User", JobFilterFields.USER.getName()));
		} else {
			table.addColumn(userid, "User");
		}

		/*-------------------------+
		 | ENVIRONMENT             |
		 +-------------------------*/
		if (ClientPermissions.isAuthorized(Permissions.JOBS_UPDATE)){
			Column<Job, String> environment = new Column<Job, String>(
					new EditTextCell()) {
				@Override
				public String getValue(Job object) {
					return object.getJcl().getEnvironment();
				}
			};
			environment.setFieldUpdater(new FieldUpdater<Job, String>() {
				@Override
				public void update(int index, Job job, String value) {
					if (value !=null && value.trim().length() > 0){
						if (!value.equalsIgnoreCase(job.getJcl().getEnvironment())){
							job.getJcl().setEnvironment(value);
							updateJob(job);
						}
						return;
					}
					refresh();
				}
			});	
			if (hasFilterableHeaders()) {
			    table.addColumn(environment, new TextFilterableHeader("Environment", JobFilterFields.ENVIRONMENT.getName()));
			} else {
			    table.addColumn(environment, "Environment");
			}
		} else {
			TextColumn<Job> environment = new TextColumn<Job>() {
				@Override
				public String getValue(Job job) {
					Jcl jcl = job.getJcl();
					return jcl.getEnvironment();
				}
			};
			environment.setSortable(true);
			if (hasFilterableHeaders()) {
			    table.addColumn(environment, new TextFilterableHeader("Environment", JobFilterFields.ENVIRONMENT.getName()));
			} else {
			    table.addColumn(environment, "Environment");
			}
		}
		/*-------------------------+
		 | DOMAIN                  |
		 +-------------------------*/
	    if (ClientPermissions.isAuthorized(Permissions.JOBS_UPDATE)){
	    	Column<Job, String> domain = new Column<Job, String>(
	    			new EditTextCell()) {
	    		@Override
	    		public String getValue(Job object) {
	    			return object.getJcl().getDomain();
	    		}
	    	};
	    	domain.setSortable(true);
	    	domain.setFieldUpdater(new FieldUpdater<Job, String>() {
	    		@Override
	    		public void update(int index, Job job, String valueParm) {
	    			String value = valueParm;
	    			if (value !=null){
	    				if (value.trim().length() == 0){
	    					value = Jcl.DEFAULT_DOMAIN;
	    				}
	    				if (!value.equalsIgnoreCase(job.getJcl().getDomain())){
	    					job.getJcl().setDomain(value);
	    					updateJob(job);
	    				}
	    				return;
	    			}
	    			refresh();
	    		}
	    	});
	    	if (hasFilterableHeaders()) {
	    		table.addColumn(domain, new TextFilterableHeader("Domain", JobFilterFields.DOMAIN.getName()));
	    	} else {
	    		table.addColumn(domain, "Domain");
	    	}
	    } else {
	    	TextColumn<Job> domain = new TextColumn<Job>() {
	    		@Override
	    		public String getValue(Job job) {
	    			Jcl jcl = job.getJcl();
	    			return jcl.getDomain();
	    		}
	    	};
	    	domain.setSortable(true);
	    	if (hasFilterableHeaders()) {
	    		table.addColumn(domain, new TextFilterableHeader("Domain", JobFilterFields.DOMAIN.getName()));
	    	} else {
	    		table.addColumn(domain, "Domain");
	    	}
	    }
	    
		/*-------------------------+
		 | STATIC AFFINITIES       |
		 +-------------------------*/	    
	    if (ClientPermissions.isAuthorized(Permissions.JOBS_UPDATE)){
	    	Column<Job, String> affinity = new Column<Job, String>(
	    			new EditTextCell()) {
	    		@Override
	    		public String getValue(Job object) {
	    			return object.getJcl().getAffinity();
	    		}
	    	};
	    	affinity.setSortable(true);
	    	affinity.setFieldUpdater(new FieldUpdater<Job, String>() {
	    		@Override
	    		public void update(int index, Job job, String valueParm) {
	    			String value = valueParm;
	    			if (value !=null){
	    				if (value.trim().length() == 0){
	    					value = Jcl.DEFAULT_AFFINITY;
	    				}
	    				if (!value.equalsIgnoreCase(job.getJcl().getAffinity())){
	    					job.getJcl().setAffinity(value);
	    					updateJob(job);
	    				}
	    				return;
	    			}
	    			refresh();
	    		}
	    	});	
	    	if (hasFilterableHeaders()) {
	    		table.addColumn(affinity, new TextFilterableHeader("Affinity", JobFilterFields.AFFINITY.getName()));
	    	} else {
	    		table.addColumn(affinity, "Affinity");
	    	}
	    } else {
	    	TextColumn<Job> affinity = new TextColumn<Job>() {
	    		@Override
	    		public String getValue(Job job) {
	    			Jcl jcl = job.getJcl();
	    			return jcl.getAffinity();
	    		}
	    	};
	    	affinity.setSortable(true);
	    	if (hasFilterableHeaders()) {
	    		table.addColumn(affinity, new TextFilterableHeader("Affinity", JobFilterFields.AFFINITY.getName()));
	    	} else {
	    		table.addColumn(affinity, "Affinity");
	    	}	
	    }
	    
		/*-------------------------+
		 | SUBMITTED DATE          |
		 +-------------------------*/
		TextColumn<Job> submittedDate = new TextColumn<Job>() {
			@Override
			public String getValue(Job job) {
				return JemConstants.DATE_TIME_FULL.format(job.getSubmittedTime()); 
			}
		};
		submittedDate.setSortable(true);
		if (hasFilterableHeaders()) {
			table.addColumn(submittedDate, new TextFilterableHeader("Submitted time", JobFilterFields.SUBMITTED_TIME.getName(), JobFilterFields.SUBMITTED_TIME.getPattern()));
		} else {
			table.addColumn(submittedDate, "Submitted time");
		}

		/*-------------------------+
		 | PRIORITY                |
		 +-------------------------*/
		if (ClientPermissions.isAuthorized(Permissions.JOBS_UPDATE)){
			Column<Job, String> priority = new Column<Job, String>(
					new EditTextCell()) {
				@Override
				public String getValue(Job job) {
					Jcl jcl = job.getJcl();
					return String.valueOf(jcl.getPriority());
				}
			};
			priority.setSortable(true);
			priority.setFieldUpdater(new FieldUpdater<Job, String>() {
				@Override
				public void update(int index, Job job, String value) {
					if (value !=null){
						int prio = job.getJcl().getPriority();
						try {
							prio = Integer.parseInt(value);
							if (prio != job.getJcl().getPriority()){
								job.getJcl().setPriority(prio);
								updateJob(job);
								return;
							}
						} catch (Exception e){
							LogClient.getInstance().warning(e.getMessage(), e);
							new Toast(MessageLevel.ERROR, "Value '"+value+"' assigned is NOT valid.", "Priority not changed!").show();
						}
					}
					refresh();
				}
			});	
	    	if (hasFilterableHeaders()) {
	    		table.addColumn(priority, new TextFilterableHeader("Priority", JobFilterFields.PRIORITY.getName()));
	    	} else {
	    		table.addColumn(priority, "Priority");
	    	}
		} else {
			TextColumn<Job> priority = new TextColumn<Job>() {
				@Override
				public String getValue(Job job) {
					Jcl jcl = job.getJcl();
					return String.valueOf(jcl.getPriority());
				}
			};
	    	if (hasFilterableHeaders()) {
	    		table.addColumn(priority, new TextFilterableHeader("Priority", JobFilterFields.PRIORITY.getName()));
	    	} else {
	    		table.addColumn(priority, "Priority");
	    	}
		}
		
		/*-------------------------+
		 | MEMORY                  |
		 +-------------------------*/
		if (ClientPermissions.isAuthorized(Permissions.JOBS_UPDATE)){
			Column<Job, String> memory = new Column<Job, String>(
					new EditTextCell()) {
				@Override
				public String getValue(Job job) {
					Jcl jcl = job.getJcl();
					return String.valueOf(jcl.getMemory());
				}
			};
			memory.setSortable(true);
			memory.setFieldUpdater(new FieldUpdater<Job, String>() {
				@Override
				public void update(int index, Job job, String value) {
					if (value !=null){
						int mem = job.getJcl().getMemory();
						try {
							mem = Integer.parseInt(value);
							if (mem != job.getJcl().getMemory()){
								job.getJcl().setMemory(mem);
								updateJob(job);
								return;
							}
						} catch (Exception e){
							LogClient.getInstance().warning(e.getMessage(), e);
							new Toast(MessageLevel.ERROR, "Value '"+value+"' assigned is NOT valid.", "Memory not changed!").show();
						}
					}
					refresh();
				}
			});	
	    	if (hasFilterableHeaders()) {
	    		table.addColumn(memory, new TextFilterableHeader("Memory (MB)", JobFilterFields.MEMORY.getName()));
	    	} else {
	    		table.addColumn(memory, "Memory (MB)");
	    	}
		} else {
			TextColumn<Job> memory = new TextColumn<Job>() {
				@Override
				public String getValue(Job job) {
					Jcl jcl = job.getJcl();
					return String.valueOf(jcl.getMemory());
				}
			};
	    	if (hasFilterableHeaders()) {
	    		table.addColumn(memory, new TextFilterableHeader("Memory (MB)", JobFilterFields.MEMORY.getName()));
	    	} else {
	    		table.addColumn(memory, "Memory (MB)");
	    	}
		}
		
		/*-------------------------+
		 | HOLD                    |
		 +-------------------------*/
		TextColumn<Job> hold = new TextColumn<Job>() {
			@Override
			public String getValue(Job job) {
				Jcl jcl = job.getJcl();				
				return (jcl.isHold()) ? "hold" : "";
			}
		};
		hold.setSortable(true);
		table.addColumn(hold, "Hold");
		
		return new InputJobComparator(1);
	}
	
	private void refresh(){
		updateJob(null);
	}
	
	private void updateJob(Job job){
		InspectListener<Job> listener = getInspectListener();
		if (listener instanceof UpdateListener<?>){
			UpdateListener<Job> ulistener= (UpdateListener<Job>) listener;
			ulistener.update(job);
		}
	}
}