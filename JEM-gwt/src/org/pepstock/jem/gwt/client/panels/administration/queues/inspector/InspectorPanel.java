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
package org.pepstock.jem.gwt.client.panels.administration.queues.inspector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.pepstock.jem.gwt.client.ColorsHex;
import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.charts.gflot.TimeCountLineChart;
import org.pepstock.jem.gwt.client.panels.administration.commons.AdminPanel;
import org.pepstock.jem.gwt.client.panels.administration.commons.BackListener;
import org.pepstock.jem.gwt.client.panels.administration.commons.InspectorHeader;
import org.pepstock.jem.gwt.client.panels.administration.commons.Instances;
import org.pepstock.jem.gwt.client.panels.administration.queues.DetailedQueueData;
import org.pepstock.jem.gwt.client.panels.components.TableContainer;
import org.pepstock.jem.node.stats.LightMapStats;
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
public class InspectorPanel extends AdminPanel implements ResizeCapable {

	@SuppressWarnings("javadoc")
    public static final int ENTRIES = 0, HITS = 1, LOCKED = 2, WAITS = 3, GETS = 4, PUTS = 5, REMOVES = 6;

	final TabPanel mainTabPanel = new TabPanel();
	
	private TimeCountLineChart chartEntries = new TimeCountLineChart();
	private TimeCountLineChart chartHits = new TimeCountLineChart();
	private TimeCountLineChart chartLocked = new TimeCountLineChart();
	private TimeCountLineChart chartWaits = new TimeCountLineChart();
	private TimeCountLineChart chartGets = new TimeCountLineChart();
	private TimeCountLineChart chartPuts = new TimeCountLineChart();
	private TimeCountLineChart chartRemoves = new TimeCountLineChart();

	private boolean chartEntriesLoaded;
	private boolean chartHitsLoaded;
	private boolean chartLockedLoaded;
	private boolean chartWaitsLoaded;
	private boolean chartGetsLoaded;
	private boolean chartPutsLoaded;
	private boolean chartRemovesLoaded;
	
	private TableContainer<DetailedQueueData> queues = new TableContainer<DetailedQueueData>(new QueuesTable());

	private ScrollPanel scroller = new ScrollPanel(queues);
	
	private InspectorHeader header = new InspectorHeader("Queue:");
	private List<DetailedQueueData> listData = new ArrayList<DetailedQueueData>();

	private VerticalPanel entPanel = new VerticalPanel();
	private VerticalPanel hitPanel = new VerticalPanel();
	private VerticalPanel lokPanel = new VerticalPanel();
	private VerticalPanel waitPanel = new VerticalPanel();
	private VerticalPanel getPanel = new VerticalPanel();
	private VerticalPanel putPanel = new VerticalPanel();
	private VerticalPanel remPanel = new VerticalPanel();
	
	/**
	 * 
	 */
	public InspectorPanel() {
		super();
		
		mainTabPanel.add(entPanel, "Entries", false);
		mainTabPanel.add(hitPanel, "Hits", false);
		mainTabPanel.add(lokPanel, "Locked", false);
		mainTabPanel.add(waitPanel, "LockWaits", false);
		mainTabPanel.add(getPanel, "Gets", false);
		mainTabPanel.add(putPanel, "Puts", false);
		mainTabPanel.add(remPanel, "Removes", false);
		

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
	 * @param queueName 
	 * 
	 */
	public void load(String queueName){
    	listData.clear();
  	
    	for (LightSample sample : Instances.getSamples()){
    		DetailedQueueData data = new DetailedQueueData();
    		data.setTime(sample.getTime());

    		for (LightMemberSample msample : sample.getMembers()){
    			LightMapStats map = msample.getMapsStats().get(queueName);
    	    	data.setEntries(map.getOwnedEntryCount() + data.getEntries());
    	    	data.setGets(map.getHits() + data.getHits());
    	    	data.setGets(map.getLockedEntryCount() + data.getLocked());
    	    	data.setGets(map.getLockWaitCount() + data.getLockWaits());
    	    	data.setGets(map.getNumberOfGets() + data.getGets());
    	    	data.setPuts(map.getNumberOfPuts() + data.getPuts());
    	    	data.setRemoves(map.getNumberOfRemoves() + data.getRemoves());
    		}
    		listData.add(data);
    	}
		header.setTitle(queueName);

    	Collections.sort(listData, new Comparator<DetailedQueueData>() {
			@Override
            public int compare(DetailedQueueData arg0, DetailedQueueData arg1) {
	            return arg0.getTime().compareTo(arg1.getTime());
            }
		});
    	queues.getUnderlyingTable().setRowData(listData);
    	
		chartEntriesLoaded = false;
		chartHitsLoaded = false;
		chartLockedLoaded = false;
		chartWaitsLoaded = false;
		chartGetsLoaded = false;
		chartPutsLoaded = false;
		chartRemovesLoaded = false;
    	
    	mainTabPanel.selectTab(0, true);
	}
    
	private void loadChart(int selected) {

		boolean allLoaded = chartEntriesLoaded && chartHitsLoaded && chartLockedLoaded;
		allLoaded = allLoaded && chartWaitsLoaded && chartGetsLoaded;
		allLoaded = allLoaded && chartPutsLoaded && chartRemovesLoaded;

		if (!allLoaded) {
			String[] times = new String[listData.size()];
			long[] values;
			// load times
			for (int i=0; i<listData.size(); i++) {
				DetailedQueueData dqd = listData.get(i);
				times[i] = dqd.getTime();
			}

			switch (selected) {
			case ENTRIES:
				if (!chartEntriesLoaded) {
					values = setChartData(chartEntries, times, getEntries(), ColorsHex.LIGHT_CYAN.getCode(), "Entries", entPanel);
					chartEntriesLoaded = true;
				}
				break;
			case HITS:
				if (!chartHitsLoaded) {
					values = setChartData(chartHits, times, getHits(), ColorsHex.LIGHT_CYAN.getCode(), "Hits", hitPanel);
					chartHitsLoaded = true;
				}
				break;
			case LOCKED:
				if (!chartLockedLoaded) {
					values = setChartData(chartLocked, times, getLocked(), ColorsHex.LIGHT_CYAN.getCode(), "Locked", lokPanel);
					chartLockedLoaded = true;
				}
				break;
			case WAITS:
				if (!chartWaitsLoaded) {
					values = getWaits();
					setChartData(chartWaits, times, values, ColorsHex.LIGHT_CYAN.getCode(), "Waits", waitPanel);
					chartWaitsLoaded = true;
				}
				break;
			case GETS:
				if (!chartGetsLoaded) {
					values = setChartData(chartGets, times, getGets(), ColorsHex.LIGHT_CYAN.getCode(), "Gets", getPanel);
					chartGetsLoaded = true;
				}
				break;
			case PUTS:
				if (!chartPutsLoaded) {
					values = setChartData(chartPuts, times, getPuts(), ColorsHex.LIGHT_CYAN.getCode(), "Puts", putPanel);
					chartPutsLoaded = true;
				}
				break;
			case REMOVES:
				if (!chartRemovesLoaded) {
					values = setChartData(chartRemoves, times, getRemoves(), ColorsHex.LIGHT_CYAN.getCode(), "Removes", remPanel);
					chartRemovesLoaded = true;
				}
				break;
			default:
				break;
			}
		}

	}

	private long[] setChartData(TimeCountLineChart chart, String[] times, long[] values, String color, String yAxixLabel, VerticalPanel container) {
		chart.setTimeAndDatas(times, values, color, "Time", yAxixLabel);
		if (container.getWidgetCount() == 0) {
			container.add(chart);
		}
		return values;
	}
	
	private long[] getEntries() {
		long[] values = new long[listData.size()];
		for (int i=0; i<listData.size(); i++) {
			DetailedQueueData dqd = listData.get(i);
			values[i] = dqd.getEntries();
		}
		return values;
	}

	private long[] getHits() {
		long[] values = new long[listData.size()];
		for (int i=0; i<listData.size(); i++) {
			DetailedQueueData dqd = listData.get(i);
			values[i] = dqd.getHits();
		}
		return values;
	}

	private long[] getLocked() {
		long[] values = new long[listData.size()];
		for (int i=0; i<listData.size(); i++) {
			DetailedQueueData dqd = listData.get(i);
			values[i] = dqd.getLocked();
		}
		return values;
	}

	private long[] getWaits() {
		long[] values = new long[listData.size()];
		for (int i=0; i<listData.size(); i++) {
			DetailedQueueData dqd = listData.get(i);
			values[i] = dqd.getLockWaits();
		}
		return values;
	}

	private long[] getGets() {
		long[] values = new long[listData.size()];
		for (int i=0; i<listData.size(); i++) {
			DetailedQueueData dqd = listData.get(i);
			values[i] = dqd.getGets();
		}
		return values;
	}

	private long[] getPuts() {
		long[] values = new long[listData.size()];
		for (int i=0; i<listData.size(); i++) {
			DetailedQueueData dqd = listData.get(i);
			values[i] = dqd.getPuts();
		}
		return values;
	}
	
	private long[] getRemoves() {
		long[] values = new long[listData.size()];
		for (int i=0; i<listData.size(); i++) {
			DetailedQueueData dqd = listData.get(i);
			values[i] = dqd.getRemoves();
		}
		return values;
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
    	
		chartEntries.setWidth(chartWidth);
		chartHits.setWidth(chartWidth);
		chartLocked.setWidth(chartWidth);
		chartWaits.setWidth(chartWidth);
		chartGets.setWidth(chartWidth);
		chartPuts.setWidth(chartWidth);
		chartRemoves.setWidth(chartWidth);


		chartEntries.setHeight(Sizes.CHART_HEIGHT);
		chartHits.setHeight(Sizes.CHART_HEIGHT);
		chartLocked.setHeight(Sizes.CHART_HEIGHT);
		chartWaits.setHeight(Sizes.CHART_HEIGHT);
		chartGets.setHeight(Sizes.CHART_HEIGHT);
		chartPuts.setHeight(Sizes.CHART_HEIGHT);
		chartRemoves.setHeight(Sizes.CHART_HEIGHT);
		
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
		
		header.setHeight(Sizes.toString(InspectorHeader.HEIGHT));
		header.setWidth(Sizes.toString(getWidth()));
    }
}