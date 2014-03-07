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
package org.pepstock.jem.gwt.client.panels;

import java.util.Collection;
import java.util.Collections;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.commons.ViewStackPanel;
import org.pepstock.jem.gwt.client.panels.administration.CertificatesPanel;
import org.pepstock.jem.gwt.client.panels.administration.ClusterConfigPanel;
import org.pepstock.jem.gwt.client.panels.administration.CurrentQueuesPanel;
import org.pepstock.jem.gwt.client.panels.administration.GfsPanel;
import org.pepstock.jem.gwt.client.panels.administration.GrsPanel;
import org.pepstock.jem.gwt.client.panels.administration.InternalMapsPanel;
import org.pepstock.jem.gwt.client.panels.administration.MemoryPanel;
import org.pepstock.jem.gwt.client.panels.administration.NodesCommandsPanel;
import org.pepstock.jem.gwt.client.panels.administration.NodesConfigPanel;
import org.pepstock.jem.gwt.client.panels.administration.NodesQueuesPanel;
import org.pepstock.jem.gwt.client.panels.administration.NodesSystemPanel;
import org.pepstock.jem.gwt.client.panels.administration.QueuesPanel;
import org.pepstock.jem.gwt.client.panels.administration.RedoStatementPanel;
import org.pepstock.jem.gwt.client.panels.administration.SecretUtilityPanel;
import org.pepstock.jem.gwt.client.panels.administration.TreeOptions;
import org.pepstock.jem.gwt.client.panels.administration.WorkloadPanel;
import org.pepstock.jem.gwt.client.panels.administration.commons.Instances;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.stats.LightSample;
import org.pepstock.jem.node.stats.SampleComparator;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Administration entry point
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public class Administration extends SplitLayoutPanel implements InspectListener<String>, ResizeCapable {
	
	private ViewStackPanel viewStack = new ViewStackPanel();
	
	private TreeOptions options = new TreeOptions();

	private WorkloadPanel workload = new WorkloadPanel();
	
	private NodesSystemPanel nodesSystem = new NodesSystemPanel();

	private NodesQueuesPanel nodesQueues = new NodesQueuesPanel();
	
	private QueuesPanel queues = new QueuesPanel();
	
	private InternalMapsPanel internals = new InternalMapsPanel();
	
	private NodesConfigPanel nodesConfig = new NodesConfigPanel();
	
	private NodesCommandsPanel nodesCmd = new NodesCommandsPanel();
	
	private GrsPanel grs = new GrsPanel();
	
	private SecretUtilityPanel sec = new SecretUtilityPanel();
	
	private CurrentQueuesPanel currentQueues = new CurrentQueuesPanel();
	
	private RedoStatementPanel redos = new RedoStatementPanel();
	
	private GfsPanel gfs = new GfsPanel();
	
	private MemoryPanel memory = new MemoryPanel();
	
	private CertificatesPanel certificate = new CertificatesPanel();
	
	private ClusterConfigPanel clusterConfig = new ClusterConfigPanel();
	
	private boolean resized = false;
	
	/**
	 * Empty constructor
	 */
	public Administration() {
		options.setListener(this);
		addWest(options, Sizes.SPLIT_PANEL_WEST_DEFAULT_SIZE);
		
		viewStack.add(new VerticalPanel());
		viewStack.add(workload);
		viewStack.add(nodesSystem);
		viewStack.add(nodesQueues);
		viewStack.add(queues);
		viewStack.add(grs);
		viewStack.add(nodesConfig);
		viewStack.add(nodesCmd);
		viewStack.add(currentQueues);
		viewStack.add(sec);
		viewStack.add(redos);
		viewStack.add(gfs);
		viewStack.add(memory);
		viewStack.add(certificate);
		viewStack.add(internals);
		viewStack.add(clusterConfig);
		add(viewStack);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.InspectListener#inspect(java.lang.Object)
	 */
    @Override
    public void inspect(String object) {
    	if (object.equalsIgnoreCase(TreeOptions.CURRENT_QUEUES_STATUS_OPTION)){
    		loadCurrentSample(object);
       	} else if (object.equalsIgnoreCase(TreeOptions.INTERNAL_MAPS_OPTION)){
       		loadCurrentSample(object);
    	} else if (object.equalsIgnoreCase(TreeOptions.GRS_CONTENTIONS_OPTION)){
    		onResultLoaded(object);
    	} else if (object.equalsIgnoreCase(TreeOptions.NODES_CONFIG_OPTION)){
    		onResultLoaded(object);
    	} else if (object.equalsIgnoreCase(TreeOptions.CLUSTER_CONFIG_OPTION)){
    		onResultLoaded(object);
    	} else if (object.equalsIgnoreCase(TreeOptions.NODES_COMMANDS_OPTION)){
    		onResultLoaded(object);
    	} else if (object.equalsIgnoreCase(TreeOptions.SECRET_UTILITY_OPTION)){
    		onResultLoaded(object);
    	} else if (object.equalsIgnoreCase(TreeOptions.CERTIFICATES_PANEL_OPTION)){
    		onResultLoaded(object);
    	} else {
    		loadAllSamples(object);
    	}
    }

    /**
     * @param option
     */
	public void onResultLoaded(String option) {
		if (option.equalsIgnoreCase(TreeOptions.WORKLOAD_JOBS_OPTION)) {
			viewStack.showStack(1);
			workload.load();
		} else if (option.equalsIgnoreCase(TreeOptions.NODES_SYSTEM_STATUS_OPTION)) {
			viewStack.showStack(2);
			nodesSystem.load();
		} else if (option.equalsIgnoreCase(TreeOptions.NODES_QUEUES_STATUS_OPTION)) {
			viewStack.showStack(3);
			nodesQueues.load();
		} else if (option.equalsIgnoreCase(TreeOptions.QUEUES_STATUS_OPTION)) {
			viewStack.showStack(4);
			queues.load();
		} else if (option.equalsIgnoreCase(TreeOptions.GRS_CONTENTIONS_OPTION)) {
			viewStack.showStack(5);
			grs.load();
		} else if (option.equalsIgnoreCase(TreeOptions.NODES_CONFIG_OPTION)) {
			viewStack.showStack(6);
			nodesConfig.load();
		} else if (option.equalsIgnoreCase(TreeOptions.NODES_COMMANDS_OPTION)) {
			viewStack.showStack(7);
			nodesCmd.load();
		} else if (option.equalsIgnoreCase(TreeOptions.CURRENT_QUEUES_STATUS_OPTION)) {
			viewStack.showStack(8);
			currentQueues.load();
		} else if (option.equalsIgnoreCase(TreeOptions.SECRET_UTILITY_OPTION)) {
			viewStack.showStack(9);
		} else if (option.equalsIgnoreCase(TreeOptions.REDO_PANEL_OPTION)) {
			viewStack.showStack(10);
			redos.load();
		} else if (option.equalsIgnoreCase(TreeOptions.GFS_PANEL_OPTION)) {
			viewStack.showStack(11);
			gfs.load();
		} else if (option.equalsIgnoreCase(TreeOptions.MEMORY_PANEL_OPTION)) {
			viewStack.showStack(12);
			memory.load();
		} else if (option.equalsIgnoreCase(TreeOptions.CERTIFICATES_PANEL_OPTION)) {
			viewStack.showStack(13);
		} else if (option.equalsIgnoreCase(TreeOptions.INTERNAL_MAPS_OPTION)) {
			viewStack.showStack(14);
			internals.load();
		} else if (option.equalsIgnoreCase(TreeOptions.CLUSTER_CONFIG_OPTION)) {
			viewStack.showStack(15);
			clusterConfig.load();
		}
	}

	private void loadAllSamples(final String what){
		Loading.startProcessing();
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				Services.STATS_MANAGER.getSamples(new GetSamplesAsyncCallback(what));		
			}
	    });
	}

	private class GetSamplesAsyncCallback extends ServiceAsyncCallback<Collection<LightSample>> {
		
		private final String what;
		
		public GetSamplesAsyncCallback(String what) {
			this.what = what;
		}
		
		@Override
        public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Get samples error!").show();
        }

		@Override
        public void onJemSuccess(Collection<LightSample> result) {
			if (result != null){
				if (!result.isEmpty()){
					Instances.setSamples(result);
					Instances.setLastSample(Collections.max(result, new SampleComparator()));
					onResultLoaded(what);
				} else {
					new Toast(MessageLevel.WARNING, "The result of samples is empty!<br>Probably JEM nodes started but they haven't create any statistics sample.", "Samples empty!").show();
				}
			} else {
				new Toast(MessageLevel.WARNING, "The result of samples is empty!<br>Probably JEM nodes started but they haven't create any statistics sample.", "Samples empty!").show();
			}
			
        }
		
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
        }
		
	}
	
	/**
	 * 
	 * @param what
	 */
	private void loadCurrentSample(final String what){
		Loading.startProcessing();

	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.STATS_MANAGER.getCurrentSample(new GetCurrentSampleAsyncCallback(what));		
			}
	    });
		
	}

	private class GetCurrentSampleAsyncCallback extends ServiceAsyncCallback<LightSample> {
		private final String what;
		
		public GetCurrentSampleAsyncCallback(String what) {
			this.what = what;
		}
		
		@Override
        public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Get last sample error!").show();
        }

		@Override
        public void onJemSuccess(LightSample result) {
			if (result != null){
				Instances.setCurrentSample(result);
				onResultLoaded(what);
			} else {
				new Toast(MessageLevel.WARNING, "The result of samples is empty!<br>Probably JEM nodes started but they haven't create any statistics sample.", "Sample empty!").show();
			}
        }
		
		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
        }
	}
	
    @Override
    public void onResize() {
    	resized = options.getOffsetWidth() != Sizes.SPLIT_PANEL_WEST_DEFAULT_SIZE;
        super.onResize();
        onResize(getOffsetWidth(), getOffsetHeight());
    }
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
	   setSize(Sizes.toString(availableWidth), Sizes.toString(availableHeight));
	   
	   int desiredWidth = availableWidth - 
			   ( resized ? options.getOffsetWidth() : Sizes.SPLIT_PANEL_WEST_DEFAULT_SIZE) - 
					   Sizes.SPLIT_PANEL_SEPARATOR; 
	   int desiredHeight = availableHeight;
	   
		for (Widget w : viewStack.getWidgets()) {
			w.setSize(Sizes.toString(desiredWidth), Sizes.toString(desiredHeight));
			if (w instanceof ResizeCapable) {
				((ResizeCapable) w).onResize(desiredWidth, desiredHeight);
			}
		}
    }
}