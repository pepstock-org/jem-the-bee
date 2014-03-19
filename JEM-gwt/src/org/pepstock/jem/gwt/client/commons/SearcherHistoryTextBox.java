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
package org.pepstock.jem.gwt.client.commons;

import java.util.List;

import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.security.CurrentUser;
import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Container with text box, history icon and search button. It wraps all widget and setting the border is the same of textbox.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class SearcherHistoryTextBox extends FlexTable {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	private boolean focus = false;

	private String preferenceKey = null;
	
	private final TextBox textBox = new TextBox();
		
	private SearcherHistoryView view = null;
	
	private Button searchButton = null;
	
	private final Image historyImage = new Image(Images.INSTANCE.history());

	/**
	 * Constructs object
	 */
    public SearcherHistoryTextBox() {
		addStyleName(Styles.INSTANCE.common().searcher());
		textBox.setStyleName(Styles.INSTANCE.common().searcherTextBox());

		// listen blur to change style, removing outline 
    	textBox.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				if (focus) {
					removeStyleName(Styles.INSTANCE.common().searcherFocus());
					focus = false;
				}
			}
		});
    	// listen focus to change style, adding outline
    	textBox.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				if (!focus) {
					addStyleName(Styles.INSTANCE.common().searcherFocus());
					focus = true;
				}
			}
		});
    	
    	setCellPadding(2);
    	setCellSpacing(0);
    	setWidget(0, 0, textBox);
    	
    	// adds HISTORY image
		historyImage.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
		
		// activates change cursor handlers
		historyImage.addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				historyImage.getElement().getStyle().setCursor(Cursor.POINTER);
			}
		});
		// activates change cursor handlers
		historyImage.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				historyImage.getElement().getStyle().setCursor(Cursor.AUTO);
			}
		});
		setWidget(0, 1, historyImage);
        historyImage.addClickHandler(new HistoryImageClickHandler());
        
        FlexCellFormatter formatter = getFlexCellFormatter();
        formatter.setWidth(0, 0, Sizes.HUNDRED_PERCENT);
        formatter.setWidth(0, 1, Sizes.toString(historyImage.getWidth()+1));
        formatter.setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);
    }
    
    private class HistoryImageClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
        	if (view != null){
        		// if here, means the suggest box is open, so hide it!
        		view.hide();
        	} else if (getPreferenceKey() != null){
        		// gets list of preferences
                List<String> list = CurrentUser.getInstance().getListPreference(getPreferenceKey());
				if (!list.isEmpty()){
					// calculates the position of popup panel with
					// preferences
					int left = getAbsoluteLeft();
					 // 1px for border of popup panel
					int width = getOffsetWidth() - 2;
					int top = getAbsoluteTop() + getOffsetHeight();
					int height = (SearcherHistoryView.CELL_HEIGHT  + SearcherHistoryView.CELL_PADDING * 2) * list.size();
					
					int totHeight = top + height;
					// if calculated height more than client height (so out of window)
					// reduces 2 times the top dimension
					if (totHeight > Window.getClientHeight()){
						height = Window.getClientHeight() - (top * 2);
					}
					
					// to fix issue #339
					if (Navigator.getUserAgent().contains(Sizes.IE10_USER_AGENT_SUBSTRING)){
						left = left + 1;
						top = top + 1;
					}
					
					// creates view history popup panel
					view = new SearcherHistoryView(getPreferenceKey());
					view.setSearcherTextBox(SearcherHistoryTextBox.this);
					view.setSearchImage(historyImage);
					view.setRowData(list);
					view.setPopupPosition(left, top);
					view.setSize(Sizes.toString(width), Sizes.toString(height));
					view.show();
					
					// adds close handler to set to null view
					view.addCloseHandler(new CloseHandler<PopupPanel>() {
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							view = null;
						}
					});
				} else {
					new Toast(MessageLevel.INFO, "History is empty. Perfom a search!", "No history!").show();
				}
        	}
        }
    }
    
    /**
     * 
     */
    public void hide(){
    	if (view != null){
    		view.hide();
    	} 
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
	 * @return the searchButton
	 */
	public Button getSearchButton() {
		return searchButton;
	}

	/**
	 * @param searchButton the searchButton to set
	 */
	public void setSearchButton(Button searchButton) {
		this.searchButton = searchButton;
	}

	/**
	 * 
	 * @param filter
	 */
	public void setFilter(String filter){
		textBox.setText(filter);
		if (searchButton != null && searchButton.isEnabled()){
			searchButton.click();
		}
	}

	/**
     * 
     * @param readOnly
     */
    public void setReadOnly(boolean readOnly){
    	textBox.setReadOnly(readOnly);
    	if (readOnly){
    		addStyleName(Styles.INSTANCE.common().searcherUnabled());
    	} else {
    		removeStyleName(Styles.INSTANCE.common().searcherUnabled());
    	}
    }
    
    /**
     * Wrap TextBox method
     * @param handler
     */
    public void addKeyPressHandler(KeyPressHandler handler){
    	textBox.addKeyPressHandler(handler);
    }

    /**
     * Wrap TextBox method
     * @param handler
     */
    public void addKeyUpHandler(KeyUpHandler handler){
    	textBox.addKeyUpHandler(handler);
    }

    /**
     * Wrap TextBox method
     * @param handler
     */
    public void addKeyDownHandler(KeyDownHandler handler){
    	textBox.addKeyDownHandler(handler);
    }

    /**
     * Wrap TextBox method
     * @param text
     */
    public void setText(String text){
    	textBox.setText(text);
    }
    
    /**
     * Wrap TextBox method
     * @return textBox value
     */
    public String getText(){
    	return textBox.getText();
    }

    /**
     * Wrap TextBox method
     * @param focus
     */
    public void setFocus(boolean focus){
    	textBox.setFocus(focus);
    }

    /**
     * Wrap TextBox method
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
    	textBox.setEnabled(enabled);
    }
    
}
