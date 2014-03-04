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
package org.pepstock.jem.gwt.client.panels.administration.internalmaps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.panels.administration.commons.AdminPanel;
import org.pepstock.jem.gwt.client.panels.administration.commons.Instances;
import org.pepstock.jem.gwt.client.panels.administration.current.EntriesChart;
import org.pepstock.jem.gwt.client.panels.administration.current.QueueData;
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
	
	private EntriesChart chartEntries = new EntriesChart();
	private TableContainer<LightMemberSample> nodes = new TableContainer<LightMemberSample>(new NodesTable());

	private ScrollPanel scroller = new ScrollPanel(nodes);
	private Map<String, QueueData> mapData = new HashMap<String, QueueData>();
	
	private VerticalPanel entriesPanel = new VerticalPanel();

	/**
	 * 
	 */
	public OverviewPanel() {
		add(entriesPanel);
		add(scroller);
	}

	/**
	 * @param memberKey 
	 * 
	 */
	public void load(){
    	mapData.clear();
    	
    	for (LightMemberSample msample : Instances.getCurrentSample().getMembers()){
    		if (msample != null){
    			for (LightMapStats map : msample.getInternalMapsStats().values()){
    				if (map !=null){
    					QueueData data = null;
    					if (mapData.containsKey(map.getName())){
    						data = mapData.get(map.getName());
    					} else {
    						data = new QueueData();
    						data.setQueue(map.getName());
    						int lastDot = map.getName().lastIndexOf('.') + 1;
    						String key = map.getName().substring(lastDot);
    						data.setKey(key);
    						data.setTime(msample.getTime());
    					}

    					data.setEntries(map.getOwnedEntryCount() + data.getEntries());
    					mapData.put(map.getName(), data);
    				}
    			}
    		}
    	}
    	nodes.getUnderlyingTable().setRowData(Instances.getCurrentSample().getMembers());
    	loadChart();
	}

	private void loadChart(){
		//
		chartEntries.setData(new ArrayList<QueueData>(mapData.values()));
		if (entriesPanel.getWidgetCount() == 0){
			entriesPanel.add(chartEntries.asWidget());
		}
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	super.onResize(availableWidth, availableHeight);
    	
    	int chartWidth = getWidth();
   	
		chartEntries.setWidth(chartWidth);
		chartEntries.setHeight(Sizes.CHART_HEIGHT);
		
	
		int height = getHeight() - Sizes.CHART_HEIGHT;
    	height = Math.max(height, 1);
		
		scroller.setHeight(Sizes.toString(height));
		scroller.setWidth(Sizes.toString(getWidth()));
    }

}