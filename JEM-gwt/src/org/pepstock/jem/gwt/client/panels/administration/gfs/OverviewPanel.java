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
package org.pepstock.jem.gwt.client.panels.administration.gfs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.charts.PieData;
import org.pepstock.jem.gwt.client.panels.administration.commons.AdminPanel;
import org.pepstock.jem.gwt.client.panels.administration.commons.Instances;
import org.pepstock.jem.gwt.client.panels.components.TableContainer;
import org.pepstock.jem.node.stats.LightMemberSample;
import org.pepstock.jem.node.stats.LightMemberSampleComparator;
import org.pepstock.jem.node.stats.LightSample;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class OverviewPanel extends AdminPanel implements ResizeCapable {
	
	private GfsChart chart = new GfsChart();
	private TableContainer<LightMemberSample> gfs = new TableContainer<LightMemberSample>(new GfsTable());

	private ScrollPanel scroller = new ScrollPanel(gfs);
	private List<PieData> mapData = new LinkedList<PieData>();
	
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

		LightMemberSample msample = Instances.getLastSample().getMembers().iterator().next();
		if (msample != null){
			long tot = msample.getGfsFree() + msample.getGfsUsed();
			
			PieData dataFree = new PieData();
			dataFree.setKey("Free");
			dataFree.setValue(msample.getGfsFree());
			dataFree.setPercent(msample.getGfsFree()/(double)tot);
			
			
			PieData dataUsed = new PieData();
			dataUsed.setKey("Used");
			dataUsed.setValue(msample.getGfsUsed());
			dataUsed.setPercent(msample.getGfsUsed()/(double)tot);
			
			mapData.add(0, dataFree);
			mapData.add(mapData.size(), dataUsed);
 		}
		
    	List<LightMemberSample> list = new ArrayList<LightMemberSample>();
    	
    	for (LightSample sample : Instances.getSamples()){
    		for (LightMemberSample membersample : sample.getMembers()){
    			if (membersample.getMemberKey().equalsIgnoreCase(msample.getMemberKey())){
    				list.add(membersample);
    			}
    		}
    	}
    	Collections.sort(list, new LightMemberSampleComparator());
		gfs.getUnderlyingTable().setRowData(list);
		loadChart();
	}

	private void loadChart(){
		//
		chart.setData(mapData);
		if (entriesPanel.getWidgetCount() == 0) {
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