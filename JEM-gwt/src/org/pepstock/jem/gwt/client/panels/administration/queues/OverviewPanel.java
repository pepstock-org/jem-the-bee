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
package org.pepstock.jem.gwt.client.panels.administration.queues;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.pepstock.jem.gwt.client.ColorsHex;
import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.Toolbox;
import org.pepstock.jem.gwt.client.charts.gflot.CounterHBarChart;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.panels.administration.commons.AdminPanel;
import org.pepstock.jem.gwt.client.panels.administration.commons.Instances;
import org.pepstock.jem.gwt.client.panels.components.TableContainer;
import org.pepstock.jem.node.stats.LightMapStats;
import org.pepstock.jem.node.stats.LightMemberSample;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class OverviewPanel extends AdminPanel implements ResizeCapable {
	
	private CounterHBarChart chart = new CounterHBarChart();
	private TableContainer<DetailedQueueData> queues = new TableContainer<DetailedQueueData>(new QueuesTable());
	
	private ScrollPanel scroller = new ScrollPanel(queues);
	private Map<String, DetailedQueueData> mapData = new HashMap<String, DetailedQueueData>();
	
	private InspectListener<DetailedQueueData> listener = null;

	private	VerticalPanel entriesPanel = new VerticalPanel();
	/**
	 * 
	 */
	public OverviewPanel() {
		add(entriesPanel);
		add(scroller);
	}

	/**
	 * @return the listener
	 */
	public InspectListener<DetailedQueueData> getListener() {
		return listener;
	}

	/**
	 * @param listener the listener to set
	 */
	public void setListener(InspectListener<DetailedQueueData> listener) {
		this.listener = listener;
		queues.getUnderlyingTable().setInspectListener(listener);
	}
	
	/**
	 * @param memberKey 
	 * 
	 */
	public void load() {
		mapData.clear();

		for (LightMemberSample msample : Instances.getLastSample().getMembers()){
			if (msample != null){
				for (LightMapStats map : msample.getMapsStats().values()){
					if (map != null) {
						DetailedQueueData queueData = null;
						if (mapData.containsKey(map.getName())){
							queueData = mapData.get(map.getName());
						} else {
							queueData = new DetailedQueueData();
							queueData.setFullName(map.getName());
							queueData.setShortName(Toolbox.getFromLastDoth(map.getName()));
							queueData.setTime(msample.getTime());
						}

						queueData.setEntries(map.getOwnedEntryCount() + queueData.getEntries());
						queueData.setGets(map.getNumberOfGets() + queueData.getGets());
						queueData.setPuts(map.getNumberOfPuts() + queueData.getPuts());
						queueData.setRemoves(map.getNumberOfRemoves() + queueData.getRemoves());

						queueData.setHits(map.getHits() + queueData.getHits());
						queueData.setLocked(map.getLockedEntryCount() + queueData.getLocked());
						queueData.setHits(map.getLockWaitCount() + queueData.getLockWaits());

						mapData.put(map.getName(), queueData);
					}
				}
			}
		}
		queues.getUnderlyingTable().setRowData(new ArrayList<DetailedQueueData>(mapData.values()));
		loadChart();
	}
	
	private void loadChart() {
		// convert from mapdata to datapoint
		long[] values = new long[mapData.size()];
		int i=0;
		for (String name : mapData.keySet()) {
			values[i] = mapData.get(name).getEntries();
			i++;
		}
		
		chart.setCountData(Toolbox.getFromLastDoth(mapData.keySet()), values, ColorsHex.LIGHT_GREEN.getCode(), "Entries", "Queues");
		
		// add chart to panel
		if (entriesPanel.getWidgetCount() == 0){
			entriesPanel.add(chart.asWidget());
		}
    }
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	super.onResize(availableWidth, availableHeight);
    	
    	int chartWidth = getWidth();
   	
		chart.setWidth(chartWidth);
		chart.setHeight(Sizes.CHART_HEIGHT);
		
		int height = getHeight() - Sizes.CHART_HEIGHT;
    	height = Math.max(height, 1);
		
		scroller.setHeight(Sizes.toString(height));
		scroller.setWidth(Sizes.toString(getWidth()));
    }
}