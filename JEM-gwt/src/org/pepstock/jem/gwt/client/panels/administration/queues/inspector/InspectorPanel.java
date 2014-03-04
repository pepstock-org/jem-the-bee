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
package org.pepstock.jem.gwt.client.panels.administration.queues.inspector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
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

	final TabPanel mainTabPanel = new TabPanel();
	
	private QueueDetailsChart chartEntries = new QueueDetailsChart(QueueDetailsChart.ENTRIES);
	private QueueDetailsChart chartHits = new QueueDetailsChart(QueueDetailsChart.HITS);
	private QueueDetailsChart chartLocked = new QueueDetailsChart(QueueDetailsChart.LOCKED);
	private QueueDetailsChart chartWaits = new QueueDetailsChart(QueueDetailsChart.WAITS);
	private QueueDetailsChart chartGets = new QueueDetailsChart(QueueDetailsChart.GETS);
	private QueueDetailsChart chartPuts = new QueueDetailsChart(QueueDetailsChart.PUTS);
	private QueueDetailsChart chartRemoves = new QueueDetailsChart(QueueDetailsChart.REMOVES);
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
    		data.setKey(sample.getTime());
    		data.setTime(sample.getKey());

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
    	
		chartEntries.setLoaded(false);
		chartHits.setLoaded(false);
		chartLocked.setLoaded(false);
		chartWaits.setLoaded(false);
		chartGets.setLoaded(false);
		chartPuts.setLoaded(false);
		chartRemoves.setLoaded(false);
    	
    	mainTabPanel.selectTab(0, true);
	}
    
	private void loadChart(int selected){
		if (selected == QueueDetailsChart.ENTRIES){
			loadChart(entPanel, chartEntries);
		} else if (selected == QueueDetailsChart.HITS){
			loadChart(hitPanel, chartHits);
		} else if (selected == QueueDetailsChart.LOCKED){
			loadChart(lokPanel, chartLocked);
		} else if (selected == QueueDetailsChart.WAITS){
			loadChart(waitPanel, chartWaits);
		} else if (selected == QueueDetailsChart.GETS){
			loadChart(getPanel, chartGets);
		} else if (selected == QueueDetailsChart.PUTS){
			loadChart(putPanel, chartPuts);
		} else if (selected == QueueDetailsChart.REMOVES){
			loadChart(remPanel, chartRemoves);
		}
    }
	
	private void loadChart(VerticalPanel parent, QueueDetailsChart chart){
		if (!chart.isLoaded()){
			//
			chart.setData(listData);
			if (parent.getWidgetCount() == 0) {
				parent.add(chart.asWidget());
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