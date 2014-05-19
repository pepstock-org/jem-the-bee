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
package org.pepstock.jem.gwt.client.panels.administration.workload;

import java.util.ArrayList;
import java.util.List;

import org.pepstock.jem.gwt.client.ColorsHex;
import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.charts.gflot.TimeCountLineChart;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.panels.administration.commons.AdminPanel;
import org.pepstock.jem.gwt.client.panels.administration.commons.Instances;
import org.pepstock.jem.gwt.client.panels.components.TableContainer;
import org.pepstock.jem.node.stats.LightMemberSample;
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
public class OverviewPanel extends AdminPanel implements ResizeCapable {
	
	@SuppressWarnings("javadoc")
    public static final int SUBMITTED_JOBS = 0, JCLS_CHACKED = 1;

	private final TabPanel mainTabPanel = new TabPanel();

	private TimeCountLineChart jobsSubmittedChart = new TimeCountLineChart();
	private TimeCountLineChart jclsCheckedChart = new TimeCountLineChart();
	private boolean jobsSubmittedChartLoaded;
	private boolean jclsCheckedChartLoaded;
	
	private TableContainer<LightMemberSample> nodes = new TableContainer<LightMemberSample>(new NodesTable());
	private ScrollPanel scroller = new ScrollPanel(nodes);
	
	private List<Workload> listData = new ArrayList<Workload>();
	
	private InspectListener<LightMemberSample> listener = null;
	
	private VerticalPanel jobPanel = new VerticalPanel();
	private VerticalPanel jclPanel = new VerticalPanel();
	
	/**
	 * 
	 */
	public OverviewPanel() {
		super();
		mainTabPanel.add(jobPanel, "Jobs submitted", false);
		mainTabPanel.add(jclPanel, "Jcl checked", false);

		mainTabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				loadChart(event.getSelectedItem());
			}
		});
		
		add(mainTabPanel);
		add(scroller);
		setCellVerticalAlignment(scroller, ALIGN_TOP);
	}

	/**
	 * @return the listener
	 */
	public InspectListener<LightMemberSample> getListener() {
		return listener;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(InspectListener<LightMemberSample> listener) {
		this.listener = listener;
		nodes.getUnderlyingTable().setInspectListener(listener);
	}
	/**
	 * @param memberKey 
	 * 
	 */
	public void load(){
    	listData.clear();
    	for (LightSample sample : Instances.getSamples()){
    		int totJob = 0;
    		int totJcl = 0;
    		for (LightMemberSample msample : sample.getMembers()){
    			if (msample != null){
    				totJob += msample.getNumberOfJOBSubmitted();
    				totJcl += msample.getNumberOfJCLCheck();
    			}
    		}
    		Workload data = new Workload();
    		data.setKey(sample.getTime());
    		data.setJobsSubmitted(totJob);
    		data.setJclsChecked(totJcl);
    		listData.add(data);
    	}
    	nodes.getUnderlyingTable().setRowData(Instances.getLastSample().getMembers());
    	jobsSubmittedChartLoaded = false;
    	jclsCheckedChartLoaded = false;
    	
    	mainTabPanel.selectTab(0, true);
	}
	
	private void loadChart(int selected) {
		String[] times = new String[listData.size()];
		long[] values = new long[listData.size()];
		for (int i=0; i<listData.size(); i++) {
			Workload w = listData.get(i);
			times[i] = w.getKey();
		}
		
		if (selected == SUBMITTED_JOBS) {
			if (!jobsSubmittedChartLoaded){
				for (int i=0; i<listData.size(); i++) {
					values[i] = listData.get(i).getJobsSubmitted();
				}
				jobsSubmittedChart.setTimeAndDatas(times, values, ColorsHex.randomColor().getCode(), "Time", "Jobs");
				
				if (jobPanel.getWidgetCount() == 0) {
					jobPanel.add(jobsSubmittedChart);
				}
				jobsSubmittedChartLoaded = true;
			}
		} else {
			if (!jclsCheckedChartLoaded){
				for (int i=0; i<listData.size(); i++) {
					values[i] = listData.get(i).getJclsChecked();
				}
				jclsCheckedChart.setTimeAndDatas(times, values, ColorsHex.randomColor().getCode(), "Time", "Jcls");
				
				if (jclPanel.getWidgetCount() == 0) {
					jclPanel.add(jclsCheckedChart);
				}
				jclsCheckedChartLoaded = true;
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
    			Sizes.MAIN_TAB_PANEL_BORDER ;

    	// bugs of IE8. setting height to tabpanel, it considers the height of tab bottom !!!
    	if (Navigator.getUserAgent().contains(Sizes.IE8_USER_AGENT_SUBSTRING)){
    		mainTabPanelHeight -= Sizes.TABBAR_HEIGHT_PX;
    	} 
    	
		jobsSubmittedChart.setWidth(chartWidth);
		jobsSubmittedChart.setHeight(Sizes.CHART_HEIGHT);
		jclsCheckedChart.setWidth(chartWidth);
		jclsCheckedChart.setHeight(Sizes.CHART_HEIGHT);
		
		mainTabPanel.setWidth(Sizes.toString(getWidth()));
		mainTabPanel.setHeight(Sizes.toString(mainTabPanelHeight));
		
		int height = getHeight() - mainTabPanelHeight;
    	// bugs of IE8. etting height to tabpanel, it considers the height of tab bottom !!!
    	if (Navigator.getUserAgent().contains(Sizes.IE8_USER_AGENT_SUBSTRING)){
    		height -= Sizes.TABBAR_HEIGHT_PX;
    	}
    	height = Math.max(height, 1);
		
		scroller.setHeight(Sizes.toString(height));
		scroller.setWidth(Sizes.toString(getWidth()));
    }
}