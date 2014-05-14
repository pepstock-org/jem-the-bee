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
import java.util.List;
import java.util.Map;

import org.pepstock.jem.gwt.client.ColorsHex;
import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.charts.gflot.BarChart;
import org.pepstock.jem.gwt.client.charts.gflot.DataPoint;
import org.pepstock.jem.gwt.client.charts.gflot.SeriesData;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.panels.administration.commons.AdminPanel;
import org.pepstock.jem.gwt.client.panels.administration.commons.Instances;
import org.pepstock.jem.gwt.client.panels.components.TableContainer;
import org.pepstock.jem.node.stats.LightMapStats;
import org.pepstock.jem.node.stats.LightMemberSample;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.gflot.client.Axis;
import com.googlecode.gflot.client.options.TickFormatter;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class OverviewPanel extends AdminPanel implements ResizeCapable {
	
	private BarChart chart = new BarChart();
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
		chart.clearData();

		for (LightMemberSample msample : Instances.getLastSample().getMembers()){
			if (msample != null){
				for (LightMapStats map : msample.getMapsStats().values()){
					if (map != null){
						DetailedQueueData queueData = null;
						if (mapData.containsKey(map.getName())){
							queueData = mapData.get(map.getName());
						} else {
							queueData = new DetailedQueueData();
							queueData.setQueue(map.getName());
							int lastDot = map.getName().lastIndexOf('.') + 1;
							// key is the undotted queue name
							String key = map.getName().substring(lastDot);
							queueData.setKey(key);
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
		List<SeriesData<Double, Double>> chartData = new ArrayList<SeriesData<Double,Double>>();
		SeriesData<Double, Double> series = new SeriesData<Double, Double>();
		series.setColor(ColorsHex.LIGHT_GREEN);
		
		List<DataPoint<Double, Double>> dataPoints = new ArrayList<DataPoint<Double,Double>>();
		final List<String> queueNames = new ArrayList<String>(mapData.keySet().size());
		int i = 0;
		long maxXValue = 0; 
		for (String key : mapData.keySet()) {
			long entries = mapData.get(key).getEntries();
			if (maxXValue < entries) {
				maxXValue = entries;
			}
			queueNames.add(key);
			// a datapoint is the entries count (x axis) then the queue index (y axis)
			dataPoints.add(new DataPoint<Double, Double>((double)entries, (double)i++));
		}

		series.setDataPoints(dataPoints);
		chartData.add(series);

		chart.setHorizontal(true);
		chart.setLabelX("Entries");
		chart.setLabelY("Queues");
		
		chart.setMinXTickSize(1l);
		chart.setMinYTickSize(1l);
		
		chart.setTickDecimalsX(0);
		chart.setTickDecimalsY(0);
		
		chart.setMinX(0l);
		// max X axis value is the entries count plus a "margin"
		chart.setMaxX((long)Math.floor(maxXValue*1.25));
		
		chart.setMinY(-1l);
		// max Y value is the queue count
		chart.setMaxY((long)series.getDataPoints().size());
		
		// format the Y tick labels as the queue name
		chart.setTickFormatterY(new TickFormatter() {
			
			@Override
			public String formatTickValue(double tickValue, Axis axis) {
				String tickLabel;
				int tickIndex = (int)tickValue;
				if (tickIndex > -1 && tickIndex<queueNames.size()) {
					tickLabel = queueNames.get(tickIndex);
				} else {
					tickLabel = "";
				}
				return tickLabel;
			}
		});
		
		// set data to chart
		chart.setData(chartData);
		
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