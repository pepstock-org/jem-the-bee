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
package org.pepstock.jem.gwt.client.panels.nodes;

import java.util.Iterator;
import java.util.List;

import org.pepstock.jem.Jcl;
import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.commons.AbstractTable;
import org.pepstock.jem.gwt.client.commons.AnchorTextColumn;
import org.pepstock.jem.gwt.client.commons.IndexedColumnComparator;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.NodeStatusImages;
import org.pepstock.jem.gwt.client.commons.TextFilterableHeader;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.commons.UpdateListener;
import org.pepstock.jem.gwt.client.log.LogClient;
import org.pepstock.jem.gwt.client.panels.nodes.commons.JobsListInspector;
import org.pepstock.jem.gwt.client.security.ClientPermissions;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.ExecutionEnvironment;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.util.filters.fields.NodeFilterFields;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.SelectionModel;

/**
 * Creates all columns to show into table, defining the sorter too.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class NodesTable extends AbstractTable<NodeInfoBean> {

	/**
	 * Adds all columns to table, defining the sort columns too.
	 */
	@Override
	public IndexedColumnComparator<NodeInfoBean> initCellTable(final CellTable<NodeInfoBean> table) {
		/*-------------------------+
		 | CHECK BOX FOR SELECTION |
		 +-------------------------*/
		@SuppressWarnings("unchecked")
		final SelectionModel<NodeInfoBean> selectionModel = (SelectionModel<NodeInfoBean>) table.getSelectionModel();
		Column<NodeInfoBean, Boolean> checkColumn = new Column<NodeInfoBean, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(NodeInfoBean nodeInfoBean) {
				return selectionModel.isSelected(nodeInfoBean);
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
				for (NodeInfoBean n : getTable().getVisibleItems()) {
					// se almeno un elemento non e' selezionato, l'header non deve essere selezionato
					if (!getTable().getSelectionModel().isSelected(n)) {
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
				for (NodeInfoBean n : getTable().getVisibleItems()) {
					getTable().getSelectionModel().setSelected(n, value);
				}
			}
		});
				
		table.setColumnWidth(checkColumn, 23, Unit.PX);
		table.addColumn(checkColumn, checkHeader);

		
		/*-------------------------+
		 | IPADDRESS AND PORT      |
		 +-------------------------*/
	    // construct a column that uses anchorRenderer
	    AnchorTextColumn<NodeInfoBean> name = new AnchorTextColumn<NodeInfoBean>() {
			@Override
			public String getValue(NodeInfoBean object) {
				return object.getLabel();
			}

			@Override
			public void onClick(int index, NodeInfoBean object, String value) {
				getInspectListener().inspect(object);
			}
		};
		name.setSortable(true);
		table.addColumn(name, new TextFilterableHeader("Name", NodeFilterFields.NAME.getName()));
		
		/*-------------------------+
		 | HOST NAME               |
		 +-------------------------*/
		TextColumn<NodeInfoBean> hostname = new TextColumn<NodeInfoBean>() {
			@Override
			public String getValue(NodeInfoBean nodeInfoBean) {
				return nodeInfoBean.getHostname();
			}
		};
		hostname.setSortable(true);
		table.addColumn(hostname, new TextFilterableHeader("Hostname", NodeFilterFields.HOSTNAME.getName()));

		/*-------------------------+
		 | DOMAIN                  |
		 +-------------------------*/
		
		if (ClientPermissions.isAuthorized(Permissions.NODES, Permissions.NODES_UPDATE)){
			Column<NodeInfoBean, String> domain = new Column<NodeInfoBean, String>(
					new EditTextCell()) {
				@Override
				public String getValue(NodeInfoBean nodeInfoBean) {
					return nodeInfoBean.getExecutionEnvironment().getDomain();
				}
			};
			domain.setSortable(true);
			domain.setFieldUpdater(new DomainFieldUpdater());	
			table.addColumn(domain, new TextFilterableHeader("Domain", NodeFilterFields.DOMAIN.getName()));
		} else {
			TextColumn<NodeInfoBean> domain = new TextColumn<NodeInfoBean>() {
				@Override
				public String getValue(NodeInfoBean nodeInfoBean) {
					return nodeInfoBean.getExecutionEnvironment().getDomain();
				}
			};
			domain.setSortable(true);
			table.addColumn(domain, new TextFilterableHeader("Domain", NodeFilterFields.DOMAIN.getName()));
		}
		
		/*-------------------------+
		 | STATIC AFFINITIES       |
		 +-------------------------*/
		if (ClientPermissions.isAuthorized(Permissions.NODES, Permissions.NODES_UPDATE)){
			Column<NodeInfoBean, String> affinity = new Column<NodeInfoBean, String>(
					new EditTextCell()) {
				@Override
				public String getValue(NodeInfoBean nodeInfoBean) {
					return toAffinityString(nodeInfoBean.getExecutionEnvironment().getStaticAffinities());
				}
			};
			affinity.setSortable(true);
			affinity.setFieldUpdater(new FieldUpdater<NodeInfoBean, String>() {
				@Override
				public void update(int index, NodeInfoBean nodeInfoBean, String valueParm) {
					String value = valueParm;
					if (value !=null){
						if (value.trim().length() == 0){
							value = Jcl.DEFAULT_AFFINITY;
						}
						nodeInfoBean.getExecutionEnvironment().getStaticAffinities().clear();
						String[] affinities = value.split(",");
						for (int i=0; i<affinities.length; i++){
							nodeInfoBean.getExecutionEnvironment().getStaticAffinities().add(affinities[i]);
						}
						updateNode(nodeInfoBean);
						return;
					}
					refresh();
				}
			});	
			table.addColumn(affinity, new TextFilterableHeader("Static Affinities", NodeFilterFields.STATIC_AFFINITIES.getName()));
		} else {
			TextColumn<NodeInfoBean> affinity = new TextColumn<NodeInfoBean>() {
				@Override
				public String getValue(NodeInfoBean nodeInfoBean) {
					return toAffinityString(nodeInfoBean.getExecutionEnvironment().getStaticAffinities());
				}
			};
			affinity.setSortable(true);
			table.addColumn(affinity, new TextFilterableHeader("Static Affinities", NodeFilterFields.STATIC_AFFINITIES.getName()));
		}
		
		/*-------------------------+
		 | DYNAMIC AFFINITIES      |
		 +-------------------------*/
	    TextColumn<NodeInfoBean> dynAffinity = new TextColumn<NodeInfoBean>() {
	    	@Override
	    	public String getValue(NodeInfoBean nodeInfoBean) {
	    		return toAffinityString(nodeInfoBean.getExecutionEnvironment().getDynamicAffinities());
	    	}
	    };
	    dynAffinity.setSortable(true);
	    table.addColumn(dynAffinity, new TextFilterableHeader("Dynamic Affinities", NodeFilterFields.DYNAMIC_AFFINITIES.getName()));
		
		/*-------------------------+
		 | STATUS                  |
		 +-------------------------*/
		StatusColumn status = new StatusColumn();
		status.setSortable(true);
		table.addColumn(status, new TextFilterableHeader("Status", NodeFilterFields.STATUS.getName()));

		/*-------------------------+
		 | OS NAME                 |
		 +-------------------------*/
		TextColumn<NodeInfoBean> systemName = new TextColumn<NodeInfoBean>() {
			@Override
			public String getValue(NodeInfoBean nodeInfoBean) {
				return nodeInfoBean.getSystemName();
			}
		};
		systemName.setSortable(true);
		table.addColumn(systemName, new TextFilterableHeader("OS", NodeFilterFields.OS.getName()));

		/*-------------------------+
		 | Memory                  |
		 +-------------------------*/
		if (ClientPermissions.isAuthorized(Permissions.NODES, Permissions.NODES_UPDATE)){
			Column<NodeInfoBean, String> memory = new Column<NodeInfoBean, String>(
					new EditTextCell()) {
				@Override
				public String getValue(NodeInfoBean nodeInfoBean) {
					return String.valueOf(nodeInfoBean.getExecutionEnvironment().getMemory());
				}
			};
			memory.setSortable(true);
			memory.setFieldUpdater(new MemoryFieldUpdater());	
			table.addColumn(memory, new TextFilterableHeader("Memory (MB)", NodeFilterFields.MEMORY.getName()));
		} else {
			TextColumn<NodeInfoBean> memory = new TextColumn<NodeInfoBean>() {
				@Override
				public String getValue(NodeInfoBean nodeInfoBean) {
					return String.valueOf(nodeInfoBean.getExecutionEnvironment().getMemory());
				}
			};
			memory.setSortable(true);
			table.addColumn(memory, new TextFilterableHeader("Memory (MB)", NodeFilterFields.MEMORY.getName()));
		}
		
		/*-------------------------+
		 | Parallel jobs           |
		 +-------------------------*/
		if (ClientPermissions.isAuthorized(Permissions.NODES, Permissions.NODES_UPDATE)){
			Column<NodeInfoBean, String> parallelJobs = new Column<NodeInfoBean, String>(
					new EditTextCell()) {
				@Override
				public String getValue(NodeInfoBean nodeInfoBean) {
					return String.valueOf(nodeInfoBean.getExecutionEnvironment().getParallelJobs());
				}
			};
			parallelJobs.setSortable(true);
			parallelJobs.setFieldUpdater(new ParallelJobsFieldUpdater());	
			table.addColumn(parallelJobs, new TextFilterableHeader("Parallel Jobs", NodeFilterFields.PARALLEL_JOBS.getName()));
		} else {
			TextColumn<NodeInfoBean> parallelJobs = new TextColumn<NodeInfoBean>() {
				@Override
				public String getValue(NodeInfoBean nodeInfoBean) {
					return String.valueOf(nodeInfoBean.getExecutionEnvironment().getParallelJobs());
				}
			};
			parallelJobs.setSortable(true);
			table.addColumn(parallelJobs, new TextFilterableHeader("Parallel Jobs", NodeFilterFields.PARALLEL_JOBS.getName()));
		}
		

		/*-------------------------+
		 | CURRENT JOB             |
		 +-------------------------*/
	    TextColumn<NodeInfoBean> currentJob = new TextColumn<NodeInfoBean>() {
			@Override
			public String getValue(NodeInfoBean nodeInfoBean) {
				if (nodeInfoBean.getJobNames().isEmpty()){
					return "";
				} else if (nodeInfoBean.getJobNames().size() > 1){
					return String.valueOf(nodeInfoBean.getJobNames().size());
				}
				return nodeInfoBean.getJobNames().get(0);
			}

		};

		currentJob.setSortable(true);
		table.addColumn(currentJob, new TextFilterableHeader("Running Jobs", NodeFilterFields.CURRENT_JOB.getName()));
		
		// adds a cell handler to catch the click on column of running jobs
		// to show the list of jobs
		table.addCellPreviewHandler(new JobsListCellPreviewHandler(table));
		return new NodesComparator(1);

	}

	private static class JobsListCellPreviewHandler implements Handler<NodeInfoBean> {
		// saved inspector instance
		private JobsListInspector inspector = null;
		
		// index of table
		private int index = -1;
		
		private CellTable<NodeInfoBean> table;
		
		public JobsListCellPreviewHandler(CellTable<NodeInfoBean> table) {
			this.table = table;
		}
		
		/**
		 * hides inspector and set index to -1
		 */
		private void reset(){
			if (inspector != null && inspector.isShowing()){
				inspector.hide();
			}
			index = -1;
		}

		@Override
        public void onCellPreview(CellPreviewEvent<NodeInfoBean> event) {
			// if column is not running jobs return
			// if node doesn't have more than 2 jobs, ignore
			if ((event.getColumn() != 10) || (event.getValue().getJobNames().size() < 2)){
				reset();
				return;
			}
			// catches onclick and mouse events
			String type = event.getNativeEvent().getType();
			switch (Event.getTypeInt(type)){
			case Event.ONCLICK:
				onClick(event);
				break;
			case Event.ONMOUSEOVER:
				// sets cursor pointer
				table.getElement().getStyle().setCursor(Cursor.POINTER);
				break;
			case Event.ONMOUSEOUT:
				table.getElement().getStyle().setCursor(Cursor.AUTO);
				break;
			default:	
				break;
			}
        }
		
		private void onClick(CellPreviewEvent<NodeInfoBean> event) {
			// checks if insector is already showed
			if (inspector != null && inspector.isShowing()){
				if (index == event.getIndex()){
					return;
				}
				inspector.hide();
			}
			// saves index
			index = event.getIndex();
			inspector = new JobsListInspector(event.getValue());
			int x = event.getNativeEvent().getClientX() - JobsListInspector.WIDTH + 20;
			int y = event.getNativeEvent().getClientY() - 20;
			inspector.setPopupPosition(x, y);
			inspector.show();
		}
	}
	
	/**
	 * Transforms a list of string for affinities in a string to show
	 * @param affinities list of affinities
	 * @return a string with list content
	 */
	private String toAffinityString(List<String> affinities) {
		if (affinities == null || affinities.isEmpty()){
			return "";
		}
		Iterator<String> i = affinities.iterator();
		StringBuilder sb = new StringBuilder();
		for (;;) {
			String aff = i.next();
			sb.append(aff);
			if (!i.hasNext()){
				return sb.toString();
			}
			sb.append(",");
		}
	}

	/**
	 * Refreshes job list. using the update node
	 */
	private void refresh(){
		updateNode(null);
	}
	
	/**
	 * Notifies update listener that nod is changed
	 * @param node node changed
	 */
	private void updateNode(NodeInfoBean node){
		InspectListener<NodeInfoBean> listener = getInspectListener();
		if (listener instanceof UpdateListener<?>){
			UpdateListener<NodeInfoBean> ulistener= (UpdateListener<NodeInfoBean>) listener;
			ulistener.update(node);
		}
	}
	
	private static class StatusColumn extends TextColumn<NodeInfoBean> {
		
		@Override
		public String getValue(NodeInfoBean object) {
			if (object == null || object.getStatus() == null) {
				return "";
			}
			return object.getStatus();	
		}

		@Override
		public void render(Context context, NodeInfoBean object, SafeHtmlBuilder sb) {
			if (object == null || object.getStatus() == null || object.getStatus().trim().isEmpty()) {
				return;
			}
			String statusString = object.getStatus();
			NodeStatusImages statusObject;
			if (statusString.equals(NodeStatusImages.UNKNOWN.toString())) {
				statusObject = NodeStatusImages.UNKNOWN;
			} else if (statusString.equals(NodeStatusImages.STARTING.toString())) {
				statusObject = NodeStatusImages.STARTING;
			} else if (statusString.equals(NodeStatusImages.INACTIVE.toString())) {
				statusObject = NodeStatusImages.INACTIVE;
			} else if (statusString.equals(NodeStatusImages.ACTIVE.toString())) {
				statusObject = NodeStatusImages.ACTIVE;
			} else if (statusString.equals(NodeStatusImages.DRAINED.toString())) {
				statusObject = NodeStatusImages.DRAINED;
			} else if (statusString.equals(NodeStatusImages.DRAINING.toString())) {
				statusObject = NodeStatusImages.DRAINING;
			} else if (statusString.equals(NodeStatusImages.SHUTTING_DOWN.toString())) {
				statusObject = NodeStatusImages.SHUTTING_DOWN;
			} else {
				// the default!
				statusObject = NodeStatusImages.INACTIVE;
			}
			
			sb.appendHtmlConstant("<table>");
			// Add the contact image.
			sb.appendHtmlConstant("<tr><td>");
			String imageHtml = AbstractImagePrototype.create(statusObject.getImage()).getHTML();
			sb.appendHtmlConstant(imageHtml);
			sb.appendHtmlConstant("</td>");
			// Add the name and address.
			sb.appendHtmlConstant("<td align='left' valign='middle'>");
			sb.appendEscaped(statusString);
			if (!object.isOperational()){
				sb.appendEscaped(" (not operational)");
			}
			sb.appendHtmlConstant("</td></tr></table>");
		}		
	}

	private class MemoryFieldUpdater implements FieldUpdater<NodeInfoBean, String> {
		@Override
		public void update(int index, NodeInfoBean nodeInfoBean, String value) {
			if (!nodeInfoBean.isHasAffinitiyLoaders()){
				if (value !=null){
					int memoryValue = nodeInfoBean.getExecutionEnvironment().getMemory();
					if (value.trim().length() > 0){
						try {
							memoryValue = Integer.parseInt(value);
							if (memoryValue < ExecutionEnvironment.MINIMUM_MEMORY){
								new Toast(MessageLevel.WARNING, "You can't assign less memory than "+ExecutionEnvironment.MINIMUM_MEMORY+".", "Memory not changed!").show();
								memoryValue = nodeInfoBean.getExecutionEnvironment().getMemory();
							} else if (memoryValue > ExecutionEnvironment.MAXIMUM_MEMORY){
								new Toast(MessageLevel.WARNING, "You can't assign more memory than "+ExecutionEnvironment.MAXIMUM_MEMORY+".", "Memory not changed!").show();
								memoryValue = nodeInfoBean.getExecutionEnvironment().getMemory();
							}
						} catch (Exception ex){
							LogClient.getInstance().warning(ex.getMessage(), ex);
							new Toast(MessageLevel.ERROR, "Value '"+value+"' assigned is NOT valid.", "Memory not changed!").show();
							memoryValue = nodeInfoBean.getExecutionEnvironment().getMemory();
						}
						memoryValue = Math.max(Math.min(memoryValue, ExecutionEnvironment.MAXIMUM_MEMORY), ExecutionEnvironment.MINIMUM_MEMORY);
					}
					if (nodeInfoBean.getExecutionEnvironment().getMemory() != memoryValue){
						nodeInfoBean.getExecutionEnvironment().setMemory(memoryValue);
						updateNode(nodeInfoBean);
						return;
					}
				}
			} else {
				new Toast(MessageLevel.WARNING, "You can't change the memory value because the JEM node has an affintiy loader which calculates memory value.", "Memory not changed!").show();
			}
			refresh();
		}
	}
	
	private class DomainFieldUpdater implements FieldUpdater<NodeInfoBean, String> {
		@Override
		public void update(int index, NodeInfoBean nodeInfoBean, String valueParm) {
			String value = valueParm;
			if (value !=null){
				if (value.trim().length() == 0){
					value = Jcl.DEFAULT_DOMAIN;
				}
				if (!value.equalsIgnoreCase(nodeInfoBean.getExecutionEnvironment().getDomain())){
					nodeInfoBean.getExecutionEnvironment().setDomain(value);
					updateNode(nodeInfoBean);
				}
				return;
			}
			refresh();
		}
	}
	
	private class ParallelJobsFieldUpdater implements FieldUpdater<NodeInfoBean, String> {
		@Override
		public void update(int index, NodeInfoBean nodeInfoBean, String value) {
			if (!nodeInfoBean.isHasAffinitiyLoaders()){
				if (value !=null){
					int jobsValue = nodeInfoBean.getExecutionEnvironment().getParallelJobs();
					if (value.trim().length() > 0){
						try {
							jobsValue = Integer.parseInt(value);
							if (jobsValue < ExecutionEnvironment.MINIMUM_PARALLEL_JOBS){
								new Toast(MessageLevel.WARNING, "You can't assign less parallel jobs than "+ExecutionEnvironment.MINIMUM_PARALLEL_JOBS+".", "Parallel jobs not changed!").show();
								jobsValue = nodeInfoBean.getExecutionEnvironment().getParallelJobs();
							} else if (jobsValue > ExecutionEnvironment.MAXIMUM_PARALLEL_JOBS){
								new Toast(MessageLevel.WARNING, "You can't assign more parallel jobs than "+ExecutionEnvironment.MAXIMUM_PARALLEL_JOBS+".", "Parallel jobs not changed!").show();
								jobsValue = nodeInfoBean.getExecutionEnvironment().getParallelJobs();
							}
						} catch (Exception ex){
							LogClient.getInstance().warning(ex.getMessage(), ex);
							new Toast(MessageLevel.ERROR, "Value '"+value+"' assigned is NOT valid.", "Parallel jobs not changed!").show();
							jobsValue = nodeInfoBean.getExecutionEnvironment().getParallelJobs();
						}
						jobsValue = Math.max(Math.min(jobsValue, ExecutionEnvironment.MAXIMUM_PARALLEL_JOBS), ExecutionEnvironment.MINIMUM_PARALLEL_JOBS);
					}
					if (nodeInfoBean.getExecutionEnvironment().getParallelJobs() != jobsValue){
						nodeInfoBean.getExecutionEnvironment().setParallelJobs(jobsValue);
						updateNode(nodeInfoBean);
						return;
					}
				}
			} else {
				new Toast(MessageLevel.WARNING, "You can't change the parallel jobs value because the JEM node has an affintiy loader which calculates parallel jobs value.", "Parallel jobs not changed!").show();
			}
			refresh();
		}
	}
	

}
