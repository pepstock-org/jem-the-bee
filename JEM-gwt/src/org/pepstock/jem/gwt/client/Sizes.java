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
package org.pepstock.jem.gwt.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * Contains all the fixed sizes used by the UI to adjust the component's size 
 * @author Andrea "Stock" Stocchero
 */
@SuppressWarnings("javadoc")
public final class Sizes {

	public static final String IE8_USER_AGENT_SUBSTRING = "MSIE 8";
	
	public static final String IE10_USER_AGENT_SUBSTRING = "MSIE 10";
	
	public static final String IE_USER_AGENT_SUBSTRING = "MSIE";

	public static final int SPACING = 4;
	
	public static final int CHART_HEIGHT = 240;
	
	public static final int HEADER = 88, FOOTER = 18;
	
	public static final int SEARCHER_WIDGET_HEIGHT = 44;
	
	public static final int STATUS_PANEL_HEADER_PX = 30;
	
	public static final int NODE_LIST_HEADER_PX = 40;
	
	public static final int NODE_LIST_WIDTH = 250;
	
	public static final int GRS_HEADER_PX = 20;
	
	public static final int SPLIT_PANEL_WEST_DEFAULT_SIZE = 200;

	public static final int SPLIT_PANEL_SEPARATOR = 8;
	
	public static final String HUNDRED_PERCENT = "100%";
	
	public static final int TABBAR_HEIGHT_PX = 33;

	public static final int INSPECTOR_HEADER_HEIGHT_PX = 75;
	
	public static final int INSPECTOR_FOOTER_HEIGHT_PX = 60;
	
	public static final int INSPECTOR_ADMIN_HEADER_PX = 40;
	
	public static final int MAIN_VERTICAL_PANEL_PADDING_TOP_LEFT_RIGHT = 4;
	public static final int MAIN_VERTICAL_PANEL_PADDING_BOTTOM = 2;

	public static final int MAIN_TAB_PANEL_PADDING_TOP_LEFT_RIGHT = 6;
	public static final int MAIN_TAB_PANEL_PADDING_BOTTOM = 1;
	public static final int MAIN_TAB_PANEL_BORDER = 1;

	/**
	 * To avoid any instantiation
	 */
	private Sizes() {
	}

	public static final String toString(int pixel){
		return pixel + "px";
	}

	public static final int toNumber(String pixelString) {
		int pxIndex = pixelString.indexOf("px");
		String px = pixelString.substring(0, pxIndex);
		return Integer.parseInt(px);
	}

	public static final String getWidthStringHtml(Widget w) {
		return w.asWidget().getElement().getStyle().getWidth();
	}
	
	public static final String getHeightStringHtml(Widget w) {
		return w.asWidget().getElement().getStyle().getHeight();
	}
	
	public static final int getWidthHtml(Widget w) {
		String s = w.asWidget().getElement().getStyle().getWidth();
		return getPixelValue(s);
	}
	
	public static final int getHeightHtml(Widget w) {
		String s = w.asWidget().getElement().getStyle().getHeight();
		return getPixelValue(s);
	}

	public static final int getPixelValue(String stringValue) {
		return Integer.parseInt(stringValue.substring(0, stringValue.length()-2));
	}
	
	public static final String deltizeAsString(String size, int delta) {
		return Integer.parseInt(size.substring(0, size.length()-2))-delta + "px";
	}
	/**
	 * Set the {@link TabPanel} width based on it's max tabs width and show it's first Tab as selected
	 * @param tabPanel
	 */
	public static final void setWidthAsMaxChildrenWidth(final TabPanel tabPanel) {
		setWidthAsMaxChildrenWidth(tabPanel, 0);
	}

	/**
	 * Set the {@link TabPanel} width based on it's max tabs width
	 * @param tabPanel the {@link TabPanel}
	 * @param finalSelectedTab selected Tab index
	 */
	public static final void setWidthAsMaxChildrenWidth(final TabPanel tabPanel, final int finalSelectedTab) {
		Scheduler.get().scheduleDeferred(new SetWidthAsMaxChildernWidthCommand(tabPanel, finalSelectedTab));
	}
	
	private static class SetWidthAsMaxChildernWidthCommand implements ScheduledCommand {
		
		private final TabPanel tabPanel;
		private final int finalSelectedTab;
		
		private SetWidthAsMaxChildernWidthCommand(final TabPanel tabPanel, final int finalSelectedTab) {
			this.tabPanel = tabPanel;
			this.finalSelectedTab = finalSelectedTab;
		}
		
		
		private int getMaxTabWidth(final TabPanel tabPanel) {
			int max = 0;
			int tabCount = tabPanel.getTabBar().getTabCount();
			for (int i=0; i<tabCount; i++) {
				tabPanel.selectTab(i);
				int tabWidth = tabPanel.getOffsetWidth();
				if (max < tabWidth) {
					max = tabWidth;
				}
			}
			return max;
		}
		
		@Override
		public void execute() {
			int calculatedWidth = getMaxTabWidth(tabPanel);
			tabPanel.selectTab(finalSelectedTab);
			tabPanel.setWidth(calculatedWidth + "px");
			tabPanel.selectTab(finalSelectedTab);
		}
	}
	
	/**
	 * Returns <code>true</code> if native event is on passed widget, otherwise <code>false</code>.
	 * @param event fired event
	 * @param widget widget to check if event is on it
	 * @return <code>true</code> if native event is on passed widget, otherwise <code>false</code>
	 */
	public static boolean isEventInsideWidget(NativeEvent event, Widget widget){
		int x = event.getClientX();
		int y = event.getClientY();

		int top = widget.getAbsoluteTop();
		int bottom = top + widget.getOffsetHeight();
		int left = widget.getAbsoluteLeft();
		int right = left + widget.getOffsetWidth();

		if (x < left || x > right ){
			return false;
		} else if (y < top || y > bottom ){
			return false;
		}
		return true;
	}
}