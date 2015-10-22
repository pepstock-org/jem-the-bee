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
package org.pepstock.jem.gwt.client.commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.log.LogClient;
import org.pepstock.jem.gwt.client.security.CurrentUser;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellList.Resources;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Container which shows the list of history searchings already done.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public final class SearcherHistoryView extends PopupPanel {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	/**
	 * Height of History ROW
	 */
	public static final int CELL_HEIGHT = 20;

	/**
	 * Padding of History ROW
	 */
	public static final int CELL_PADDING = 2;
	
	private HistoryCell cell = new HistoryCell();
	
	private CellList<String> cellList = null;

	
	private ScrollPanel cellListScroller = new ScrollPanel();

	private SearcherHistoryTextBox searcherTextBox = null;
	
	private Image searchImage = null;
	
	private String preferenceKey = null;
	
	private boolean onClickToRemove = false; 
	
	/**
	 * Creates the object saving the preferenceKey
	 * @param preferenceKey preference key to remove items when asked
	 * 
	 */
	public SearcherHistoryView(String preferenceKey) {
		this.preferenceKey = preferenceKey;
		setStyleName(Styles.INSTANCE.common().searcherHistory());
		// Create a CellList that uses the cell.
		cellList = new CellList<String>(cell, (Resources) GWT.create(CellTableStyle.class));
		
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		// Add a selection model to handle user selection.
		final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
		cellList.setSelectionModel(selectionModel);
		
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				if (!onClickToRemove){
					select(selectionModel.getSelectedObject());
				} else {
					onClickToRemove = false;
				}
			}
		});

	    cellListScroller.setWidget(cellList);

	    VerticalPanel scrollHolder = new VerticalPanel();
	    scrollHolder.setSize(Sizes.HUNDRED_PERCENT, Sizes.HUNDRED_PERCENT);
	    
	    scrollHolder.add(cellListScroller);

	    add(scrollHolder);
	}
	
	/**
	 * Sets size of object
	 */
	public void setSize(String width, String height){
		super.setSize(width, height);
		cellListScroller.setSize(width, height);
	}
	
	/**
	 * @return the searcherTextBox
	 */
	public SearcherHistoryTextBox getSearcherTextBox() {
		return searcherTextBox;
	}

	/**
	 * @param searcherTextBox the searcherTextBox to set
	 */
	public void setSearcherTextBox(SearcherHistoryTextBox searcherTextBox) {
		this.searcherTextBox = searcherTextBox;
	}

	
	
	/**
	 * @return the preferenceKey
	 */
	public String getPreferenceKey() {
		return preferenceKey;
	}

	/**
	 * @param preferenceKey the preferenceKey to set
	 */
	public void setPreferenceKey(String preferenceKey) {
		this.preferenceKey = preferenceKey;
	}

	/**
	 * @return the searchImage
	 */
	public Image getSearchImage() {
		return searchImage;
	}

	/**
	 * @param searchImage the searchImage to set
	 */
	public void setSearchImage(Image searchImage) {
		this.searchImage = searchImage;
	}

	/**
	 * @param history
	 */
	public void setRowData(Collection<String> history){
	    // Set the total row count. 
	    cellList.setRowCount(history.size(), true);
	    // Push the data into the widget.
	    cellList.setRowData(0, new ArrayList<String>(history));
	    cellList.redraw();
	}

	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event) {
		super.onPreviewNativeEvent(event);
		switch (event.getTypeInt()){
		// with ESC close the popup panel
		case Event.ONKEYDOWN:
			if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE){
				hide();
			}
			break;
		case Event.ONCLICK:
			hideIfOutside(event);
			break;
		default:
			break;
		}
	}
	
	private void hideIfOutside(NativePreviewEvent event) {
		// with click, check if is inside or outside 
		// if outside or on search image, popup will be closed
		if (isShowing()) {
			boolean insidePopup = Sizes.isEventInsideWidget(event.getNativeEvent(), this);
			if (!insidePopup){
				boolean insideImage = Sizes.isEventInsideWidget(event.getNativeEvent(), getSearchImage());
				if (!insideImage){
					hide();
				}
			}
		}
	}
	
	/**
	 * Called when you select a history item
	 * @param value string value of history item
	 */
	private void select(String value){
		// close popup
		hide();
		//checks value
		if (value != null && getSearcherTextBox() != null){
			// sets filter!
			getSearcherTextBox().setFilter(value);
		}
	}
	
	/**
	 * Cell of history item.
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 1.4
	 */
	class HistoryCell extends AbstractCell<String> {
		
		/* (non-Javadoc)
		 * @see com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
		 */
		@Override
		public void render(Context context, String value, SafeHtmlBuilder sb) {
			if (value != null) {
				AbstractImagePrototype proto = AbstractImagePrototype.create(Images.INSTANCE.delete());
				sb.appendHtmlConstant("<table cellpadding="+CELL_PADDING+" cellspacing=0 width='100%'><tr><td style='padding-left: 4px;' width='100%' height="+CELL_HEIGHT+">");
				sb.appendEscaped(value);
				sb.appendHtmlConstant("</td><td width='16px'>");
				sb.appendHtmlConstant(proto.getHTML());
				sb.appendHtmlConstant("</td></tr></table>");
			}
		}
		
		@Override
		public Set<String> getConsumedEvents() {
			// get consumed events from super
			Set<String> consumedEvents = super.getConsumedEvents();
			if (consumedEvents == null) {
				consumedEvents = new HashSet<String>();
			}
			// add my custom consumed event
			consumedEvents.add(BrowserEvents.MOUSEOVER);
			consumedEvents.add(BrowserEvents.MOUSEOUT);
			consumedEvents.add(BrowserEvents.CLICK);
			// return all
			return consumedEvents;
		}
		
		@Override
		public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
			try {
				// show filter panel only if there is a right-click event 
				if (BrowserEvents.MOUSEOVER.equals(event.getType())) {
					// set orange background
					parent.getStyle().setBackgroundColor("#ffe699");
					
				} else if (BrowserEvents.MOUSEOUT.equals(event.getType())) {
					// reset orange background
					parent.getStyle().setBackgroundColor("white");
					
				} else if (BrowserEvents.CLICK.equals(event.getType())) {
					onClickToRemove = true;
					int x = event.getClientX();
					int y = event.getClientY();

					int top = parent.getAbsoluteTop();
					int bottom = parent.getAbsoluteBottom();
					int left = parent.getAbsoluteRight() - 16;
					int right = parent.getAbsoluteRight();

					// checks if has click on history item
					// if yes, select as filter
					if (x < left || x > right ){
						select(value);
						return;
					} else if (y < top || y > bottom ){
						select(value);
						return;
					}
					// if here click is on delete image
					// so it removes element from history
					List<String> list = CurrentUser.getInstance().getListPreference(getPreferenceKey());
					list.remove(value);
					CurrentUser.getInstance().setLastUpdateTime();
					// if no items, close popup
					if (list.isEmpty()){
						hide();
					} else {
						// sets new data without removing
						setRowData(list);
					}
				}
			} catch (Exception e) {
				LogClient.getInstance().warning(e.getMessage(), e);
			}
		}
	}
	
}
