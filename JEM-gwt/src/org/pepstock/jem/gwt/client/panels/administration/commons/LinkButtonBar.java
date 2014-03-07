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
package org.pepstock.jem.gwt.client.panels.administration.commons;

import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.Styles;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author Andrea "Stock" Stocchero
 * 
 */
public class LinkButtonBar extends FlowPanel {

	static {
		Styles.INSTANCE.linkButtonBarStyle().ensureInjected();
	}

	private List<Anchor> anchors = new LinkedList<Anchor>();

	private InspectListener<Integer> listener = null;

	private Anchor selected = null;

	/**
	 * @param elements
	 */
	public LinkButtonBar(final BarElement... elements) {
		addStyleName(Styles.INSTANCE.linkButtonBarStyle().bar());

		if (elements != null) {
			for (int i = 0; i < elements.length; i++) {
				if (elements[i].getLabel() != null) {
					final Anchor anchorElement = new Anchor(elements[i].getLabel());
					anchorElement.addClickHandler(new AnchorClickHandler(elements[i]));
					// base style
					anchorElement.setStyleName(Styles.INSTANCE.linkButtonBarStyle().button());

					if (i == 0) {
						anchorElement.setStyleName(Styles.INSTANCE.linkButtonBarStyle().selectedButton());
						selected = anchorElement;
					}
					add(anchorElement);
					anchors.add(anchorElement);
				}
			}
		}
	}

	/**
	 * @return the listener
	 */
	public InspectListener<Integer> getListener() {
		return listener;
	}

	/**
	 * @param listener
	 *            the listener to set
	 */
	public void setListener(InspectListener<Integer> listener) {
		this.listener = listener;
	}

	class AnchorClickHandler implements ClickHandler {

		private BarElement element = null;

		AnchorClickHandler(BarElement element) {
			this.element = element;
		}

		@Override
		public void onClick(ClickEvent event) {
			resetColor(element);
			if (listener != null) {
				listener.inspect(Integer.valueOf(element.getValue()));
			}
		}
	}

	/**
	 * @param element
	 */
	private void resetColor(BarElement element) {
		if (selected != null) {
			selected.setStyleName(Styles.INSTANCE.linkButtonBarStyle().button());
		}

		if (!anchors.isEmpty()) {
			if (element.getValue() == BarElement.BACK.getValue()) {
				Anchor backElement = anchors.get(anchors.size()-1);
				backElement.setStyleName(Styles.INSTANCE.linkButtonBarStyle().button());
				Anchor firstElement = anchors.get(0);
				firstElement.setStyleName(Styles.INSTANCE.linkButtonBarStyle().selectedButton());
				selected = firstElement;
			} else {
				for (Anchor anchor : anchors) {
					String anchorText = anchor.getText();
					if (anchorText.equalsIgnoreCase(element.getLabel())) {
						anchor.setStyleName(Styles.INSTANCE.linkButtonBarStyle().selectedButton());
						selected = anchor;
					}
				}
			}
		}
	}
}