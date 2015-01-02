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
package org.pepstock.jem.gwt.client.panels.administration.nodesqueues.inspector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pepstock.jem.gwt.client.ColorsHex;
import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.charts.gflot.TimeCountLineChart;
import org.pepstock.jem.gwt.client.panels.administration.commons.AdminPanel;
import org.pepstock.jem.gwt.client.panels.administration.commons.BackListener;
import org.pepstock.jem.gwt.client.panels.administration.commons.InspectorHeader;
import org.pepstock.jem.gwt.client.panels.administration.commons.Instances;
import org.pepstock.jem.gwt.client.panels.components.TableContainer;
import org.pepstock.jem.node.Queues;
import org.pepstock.jem.node.stats.LightMemberSample;
import org.pepstock.jem.node.stats.LightMemberSampleComparator;
import org.pepstock.jem.node.stats.LightSample;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class InspectorPanel extends AdminPanel implements ResizeCapable {
	
	@SuppressWarnings("javadoc")
    public static final int INPUT = 0, RUNNING = 1, OUTPUT = 2, ROUTING = 3;

	final TabPanel mainTabPanel = new TabPanel();
	
	private TimeCountLineChart chartInput = new TimeCountLineChart();
	private TimeCountLineChart chartRunning = new TimeCountLineChart();
	private TimeCountLineChart chartOutput = new TimeCountLineChart();
	private TimeCountLineChart chartRouting = new TimeCountLineChart();
	
	private boolean chartInputLoaded;
	private boolean chartRunningLoaded;
	private boolean chartOutputLoaded;
	private boolean chartRoutingLoaded;
	
	private TableContainer<LightMemberSample> nodes = new TableContainer<LightMemberSample>(new NodesTable());
	private ScrollPanel scroller = new ScrollPanel(nodes);
	
	private InspectorHeader header = new InspectorHeader("Host:");
	
	private List<NodeData> listData = new ArrayList<NodeData>();
	
	private VerticalPanel inputPanel = new VerticalPanel();
	private VerticalPanel runningPanel = new VerticalPanel();
	private VerticalPanel outputPanel = new VerticalPanel();
	private VerticalPanel routingPanel = new VerticalPanel();

	/**
	 * 
	 */
	public InspectorPanel() {
		mainTabPanel.add(inputPanel, "Input", false);
		mainTabPanel.add(runningPanel, "Running", false);
		mainTabPanel.add(outputPanel, "Output", false);
		mainTabPanel.add(routingPanel, "Routing", false);
		
		mainTabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				loadChart(event.getSelectedItem());
			}
		});
		
		add(header);
		add(mainTabPanel);
		add(scroller);
	}

	/**
	 * @return the listener
	 */
	public BackListener getListener() {
		return header.getListener();
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(BackListener listener) {
		header.setListener(listener);
	}

	/**
	 * @param memberKey 
	 * 
	 */
	public void load(String memberKey){
    	listData.clear();
    	
   	   	List<LightMemberSample> list = new ArrayList<LightMemberSample>();
   	   	boolean set = true;
   	 
    	for (LightSample sample : Instances.getSamples()){
    		for (LightMemberSample msample : sample.getMembers()){
    			if (msample.getMemberKey().equalsIgnoreCase(memberKey)){
    	    		NodeData data = new NodeData();
    	    		data.setTime(sample.getTime());
  
    	    		data.setInput(msample.getMapsStats().get(Queues.INPUT_QUEUE).getOwnedEntryCount());
    	    		data.setRunning(msample.getMapsStats().get(Queues.RUNNING_QUEUE).getOwnedEntryCount());
    	    		data.setOutput(msample.getMapsStats().get(Queues.OUTPUT_QUEUE).getOwnedEntryCount());
    	    		data.setRouting(msample.getMapsStats().get(Queues.ROUTING_QUEUE).getOwnedEntryCount());
    	    		
    	    		listData.add(data);
    				list.add(msample);
    				if (set){
    					set = false;
    					header.setTitle(msample.getMemberLabel()+" - "+msample.getMemberHostname());
    				}
    			}
    		}
    	}
    	Collections.sort(list, new LightMemberSampleComparator());
    	nodes.getUnderlyingTable().setRowData(list);

    	chartInputLoaded = false;
    	chartRunningLoaded = false;
    	chartOutputLoaded = false;
    	chartRoutingLoaded = false;
    	
    	mainTabPanel.selectTab(0, true);
	}
	
	private void loadChart(int selected) {
		switch (selected) {
		case INPUT:
			loadInputChart();
			break;
		case RUNNING:
			loadRunningChart();
			break;
		case OUTPUT:
			loadOutputChart();
			break;
		case ROUTING:
			loadRoutingChart();
			break;
		default:
			break;
		}
    }
	
	private void loadInputChart() {
		if (!chartInputLoaded) {
			String[] times = new String[listData.size()];
			long[] values = new long[listData.size()];
			for (int i=0; i<listData.size(); i++) {
				NodeData nd = listData.get(i);
				times[i] = nd.getTime();
				values[i] = nd.getInput();
			}
			chartInput.setTimeAndDatas(times, values, ColorsHex.YELLOW.getCode(), "Time", "Entries");
			if (inputPanel.getWidgetCount() == 0) {
				inputPanel.add(chartInput);
			}
			chartInputLoaded = true;
		}
	}
	
	private void loadRunningChart() {
		if (!chartRunningLoaded) {
			String[] times = new String[listData.size()];
			long[] values = new long[listData.size()];
			for (int i=0; i<listData.size(); i++) {
				NodeData nd = listData.get(i);
				times[i] = nd.getTime();
				values[i] = nd.getRunning();
			}
			chartRunning.setTimeAndDatas(times, values, ColorsHex.YELLOW.getCode(), "Time", "Entries");
			if (runningPanel.getWidgetCount() == 0) {
				runningPanel.add(chartRunning);
			}
			chartRunningLoaded = true;
		}
	}

	private void loadOutputChart() {
		if (!chartOutputLoaded) {
			String[] times = new String[listData.size()];
			long[] values = new long[listData.size()];
			for (int i=0; i<listData.size(); i++) {
				NodeData nd = listData.get(i);
				times[i] = nd.getTime();
				values[i] = nd.getOutput();
			}
			chartOutput.setTimeAndDatas(times, values, ColorsHex.YELLOW.getCode(), "Time", "Entries");
			if (outputPanel.getWidgetCount() == 0) {
				outputPanel.add(chartOutput);
			}
			chartOutputLoaded = true;
		}
	}

	private void loadRoutingChart() {
		if (!chartRoutingLoaded) {
			String[] times = new String[listData.size()];
			long[] values = new long[listData.size()];
			for (int i=0; i<listData.size(); i++) {
				NodeData nd = listData.get(i);
				times[i] = nd.getTime();
				values[i] = nd.getRouting();
			}
			chartRouting.setTimeAndDatas(times, values, ColorsHex.YELLOW.getCode(), "Time", "Entries");
			if (routingPanel.getWidgetCount() == 0) {
				routingPanel.add(chartRouting);
			}
			chartRoutingLoaded = true;
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	super.onResize(availableWidth, availableHeight);
    	
    	int chartWidth = getWidth() -  
    			Sizes.MAIN_TAB_PANEL_PADDING_TOP_LEFT_RIGHT -
    			Sizes.MAIN_TAB_PANEL_PADDING_TOP_LEFT_RIGHT -
    			Sizes.MAIN_TAB_PANEL_BORDER -
    			Sizes.MAIN_TAB_PANEL_BORDER;
    	
    	int mainTabPanelHeight = Sizes.TABBAR_HEIGHT_PX +
    			Sizes.CHART_HEIGHT + 
    			Sizes.MAIN_TAB_PANEL_PADDING_TOP_LEFT_RIGHT +
    			Sizes.MAIN_TAB_PANEL_PADDING_BOTTOM +
    			Sizes.MAIN_TAB_PANEL_BORDER;
    	
    	// bugs of IE8. etting height to tabpanel, it considers the height of tab bottom !!!
    	if (Navigator.getUserAgent().contains(Sizes.IE8_USER_AGENT_SUBSTRING)){
    		mainTabPanelHeight -= Sizes.TABBAR_HEIGHT_PX;
    	} 
    	
    	
		chartInput.setWidth(chartWidth);
		chartRunning.setWidth(chartWidth);
		chartRouting.setWidth(chartWidth);
		chartOutput.setWidth(chartWidth);

		chartInput.setHeight(Sizes.CHART_HEIGHT);
		chartRunning.setHeight(Sizes.CHART_HEIGHT);
		chartOutput.setHeight(Sizes.CHART_HEIGHT);
		chartRouting.setHeight(Sizes.CHART_HEIGHT);
    	
		mainTabPanel.setWidth(Sizes.toString(getWidth()));
		mainTabPanel.setHeight(Sizes.toString(mainTabPanelHeight));
		
		int height = getHeight() - mainTabPanelHeight - Sizes.INSPECTOR_ADMIN_HEADER_PX;
    	// bugs of IE8. etting height to tabpanel, it considers the height of tab bottom !!!
    	if (Navigator.getUserAgent().contains(Sizes.IE8_USER_AGENT_SUBSTRING)){
    		height -= Sizes.TABBAR_HEIGHT_PX;
    	}
		height = Math.max(height, 1);
		
		scroller.setHeight(Sizes.toString(height));
		scroller.setWidth(Sizes.toString(getWidth()));
		
		header.setHeight(Sizes.toString(Sizes.INSPECTOR_ADMIN_HEADER_PX));
		header.setWidth(Sizes.toString(getWidth()));
    }

}