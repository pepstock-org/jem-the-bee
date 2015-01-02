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
package org.pepstock.jem.gwt.client.panels.administration.commons;

import java.util.List;

import org.pepstock.jem.NodeInfoBean;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.administration.nodesconfig.Separator;
import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Is a container of editor, inside a popup.<br>
 * The usage of popup is necessary to check if the user is leaving the editor without saving the updates he has done.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class EditorContainer extends PopupPanel {
	
	static {
		Styles.INSTANCE.administration().ensureInjected();
	}
	
	private SingleSelectionModel<NodeInfoBean> model = null;
	
	private Separator separator = null;
	
	private List<AdminEditor> editors = null;
	
	/**
	 * Applies thte style to popup
	 */
	public EditorContainer() {
		super(false, false);
		setStyleName(Styles.INSTANCE.administration().editorContainer());
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.PopupPanel#onPreviewNativeEvent(com.google.gwt.user.client.NativePreviewEvent)
	 */
	@Override
	protected void onPreviewNativeEvent(final NativePreviewEvent event) {
		if (event.getTypeInt() == Event.ONCLICK) {
			onClick(event);
		}
	}
	
	private void onClick(NativePreviewEvent event) {
		// catches CLICK
		// if popup is showed, do something otherwise ignore
		if (isShowing()){
			// checks if user clicks on popup
			boolean insidePopup = Sizes.isEventInsideWidget(event.getNativeEvent(), this);
			// checks if user clicks on panel separator between list of nodes and editors
			// separator could be null if the editor container is for environment one (without nodes list)
			boolean isOnSeparator = false;
			if (separator != null){
				isOnSeparator = Sizes.isEventInsideWidget(event.getNativeEvent(), separator.getCloseIcon()) ||
						Sizes.isEventInsideWidget(event.getNativeEvent(), separator.getOpenIcon());
			}
			// if user clicks outside, checks if the content is changed 
			if ((!insidePopup) && !(isOnSeparator)){
				// returns editor description 
				// if editor changes content of file
				// if null, no changes
				StringBuilder fileChanged = getChanged();
				if (fileChanged != null){
					// cancel event propagation
					// show toast to aler tthat the file is changed
					event.cancel();
					new Toast(MessageLevel.WARNING, "You are leaving the editor without saving your changes in "+fileChanged.toString()+".", 
							"Configuration changed without saving!").show();
				} else {
					// if we have nodes list
					// deselects the node
					if (model != null){
						NodeInfoBean node = model.getSelectedObject();
						model.setSelected(node, false);
					}
					hide();
				}
			} else {
				// doesn't care this click
				super.onPreviewNativeEvent(event);
			}
		}
	}
	
	/**
	 * Returns array of editors
	 * @return the editors
	 */
	public List<AdminEditor> getEditors() {
		return editors;
	}

	/**
	 * Sets array of editors
	 * @param editors the editors to set
	 */
	public void setEditors(List<AdminEditor> editors) {
		this.editors = editors;
	}
	
	/**
	 * Returns the selection model of nodes list, if there is
	 * @return the model
	 */
	public SingleSelectionModel<NodeInfoBean> getModel() {
		return model;
	}

	/**
	 * Sets the selection model of nodes list
	 * @param model the model to set
	 */
	public void setModel(SingleSelectionModel<NodeInfoBean> model) {
		this.model = model;
	}
	
	/**
	 * Returns the separator panel between editors and nodes list, if there is 
	 * @return the separator
	 */
	public Separator getSeparator() {
		return separator;
	}

	/**
	 * Sets the separator panel between editors and nodes list
	 * @param separator the separator to set
	 */
	public void setSeparator(Separator separator) {
		this.separator = separator;
	}

	/**
	 * Checks if the editors ahve some contents changed.
	 * if yes, returns the description of first editor, otherwise null.
	 * @return returns the description of first editor which has got a changed content, otherwise null
	 */
	private StringBuilder getChanged(){
		if (editors != null){
			StringBuilder result = new StringBuilder();
			for (AdminEditor editor : editors){
				if (editor.isChanged()){
					result.append("'").append(editor.getDescription()).append("'");
					return result;
				}
			}
		}
		return null;
	}
}
