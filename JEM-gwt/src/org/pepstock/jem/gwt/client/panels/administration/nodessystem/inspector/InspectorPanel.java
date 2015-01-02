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
package org.pepstock.jem.gwt.client.panels.administration.nodessystem.inspector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.pepstock.jem.gwt.client.ColorsHex;
import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.charts.gflot.TimeCountLineChart;
import org.pepstock.jem.gwt.client.charts.gflot.TimePercentLineChart;
import org.pepstock.jem.gwt.client.panels.administration.commons.AdminPanel;
import org.pepstock.jem.gwt.client.panels.administration.commons.BackListener;
import org.pepstock.jem.gwt.client.panels.administration.commons.InspectorHeader;
import org.pepstock.jem.gwt.client.panels.administration.commons.Instances;
import org.pepstock.jem.gwt.client.panels.components.TableContainer;
import org.pepstock.jem.node.stats.LightMemberSample;
import org.pepstock.jem.node.stats.LightMemberSampleComparator;
import org.pepstock.jem.node.stats.LightSample;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class InspectorPanel extends AdminPanel implements ResizeCapable {

	@SuppressWarnings("javadoc")
    public static final int MACHINE_CPU_PERCENT = 0, PROCESS_CPU_PERCENT = 1, PROCESS_MEMORY_UTIL = 2;

	final TabPanel mainTabPanel = new TabPanel();
	
	private TimePercentLineChart machineCpuPercentChart = new TimePercentLineChart();
	private TimePercentLineChart processCpuPercentChart = new TimePercentLineChart();
	private TimeCountLineChart processMemoryUsedChart = new TimeCountLineChart();

	private boolean machineCpuPercentChartLoaded;
	private boolean processCpuPercentChartLoaded;
	private boolean processMemoryUsedChartLoaded;

	private TableContainer<LightMemberSample> nodes = new TableContainer<LightMemberSample>(new NodesTable());
	private ScrollPanel scroller = new ScrollPanel(nodes);
	
	private InspectorHeader header = new InspectorHeader("Host:");
	
	private List<SystemData> listData = new ArrayList<SystemData>();
	
	private SimplePanel mCpuPanel = new SimplePanel();
	private SimplePanel pCpuPanel = new SimplePanel();
	private SimplePanel pMemPanel = new SimplePanel();
	
	/**
	 * 
	 */
	public InspectorPanel() {
		super();
		
		mainTabPanel.add(mCpuPanel, "Machine CPU %", false);
		mainTabPanel.add(pCpuPanel, "Process CPU %", false);
		mainTabPanel.add(pMemPanel, "Process Memory used", false);

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
    	    		SystemData data = new SystemData();
    	    		data.setTime(sample.getTime());
    	    		
    	    		data.setMachineCpuPercent(msample.getCpuPercent()*100);
    	    		data.setProcessCpuPercent(msample.getProcessCpuPercent()*100);
    	    		data.setProcessMemoryUtil((long) ((double)msample.getProcessMemoryUsed()/1024d/1024d));
    	    		
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
    	machineCpuPercentChartLoaded = false;
    	processCpuPercentChartLoaded = false;
    	processMemoryUsedChartLoaded = false;
    	mainTabPanel.selectTab(0, true);
	}

	private void loadChart(int selected) {
		String[] times = new String[listData.size()];
		for (int i=0; i<listData.size(); i++) {
			SystemData sd = listData.get(i);
			times[i] = sd.getTime();
		}
		if (selected == MACHINE_CPU_PERCENT){
			if (!machineCpuPercentChartLoaded){
				double[] values = new double[listData.size()];
				for (int i=0; i<listData.size(); i++) {
					values[i] = listData.get(i).getMachineCpuPercent();
				}
				machineCpuPercentChart.setTimeAndDatas(times, values, ColorsHex.LIGHT_RED.getCode(), "Time", "Cpu %");

				mCpuPanel.setWidget(machineCpuPercentChart);
				machineCpuPercentChartLoaded = true;
			}
		} else if (selected == PROCESS_CPU_PERCENT){
			if (!processCpuPercentChartLoaded){
				double[] values = new double[listData.size()];
				for (int i=0; i<listData.size(); i++) {
					values[i] = listData.get(i).getProcessCpuPercent();
				}
				processCpuPercentChart.setTimeAndDatas(times, values, ColorsHex.LIGHT_RED.getCode(), "Time", "Cpu %");

				pCpuPanel.setWidget(processCpuPercentChart);
				processCpuPercentChartLoaded = true;
			}
		} else {
			if (!processMemoryUsedChartLoaded){
				long[] values = new long[listData.size()];
				for (int i=0; i<listData.size(); i++) {
					values[i] = listData.get(i).getProcessMemoryUtil();
				}
				processMemoryUsedChart.setTimeAndDatas(times, values, ColorsHex.LIGHT_RED.getCode(), "Time", "Megabytes");
				pMemPanel.setWidget(processMemoryUsedChart);
				processMemoryUsedChartLoaded = true;
			}	
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
    	
		machineCpuPercentChart.setWidth(chartWidth);
		processCpuPercentChart.setWidth(chartWidth);
		processMemoryUsedChart.setWidth(chartWidth);
		
		machineCpuPercentChart.setHeight(Sizes.CHART_HEIGHT);
		processCpuPercentChart.setHeight(Sizes.CHART_HEIGHT);
		processMemoryUsedChart.setHeight(Sizes.CHART_HEIGHT);
		
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