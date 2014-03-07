/**

    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Marco "Cuc" Cuccato
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

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * A gmail-style button tooltip
 * @author Marco "Fuzzo" Cuccato
 */
public class Tooltip extends PopupPanel {
	
	static {
		Styles.INSTANCE.tooltip().ensureInjected();
	}

	private static final int PADDING_BOTTOM = -5;
	
	private FocusWidget widget = null;
	private FadeAnimation animation = null;
	
	/**
	 * Build a tooltip and adds it to a {@link FocusWidget}
	 * @param widget the widget that will show this tooltip
	 * @param tooltipText the tooltip text
	 */
	public Tooltip(FocusWidget widget, String tooltipText) {
		super(true, false);
		this.widget = widget;
		setStyleName(Styles.INSTANCE.tooltip().tooltipBox());
		
		Grid content = new Grid(2, 1);
		content.setBorderWidth(0);
		content.setCellPadding(0);
		content.setCellSpacing(0);
		content.setStyleName(Styles.INSTANCE.tooltip().tooltipBox());
		
		Image arrow = new Image(Images.INSTANCE.tooltipArrow());
		content.getRowFormatter().setStyleName(0, Styles.INSTANCE.tooltip().tooltipArrow());
		
		HTML text = new HTML(tooltipText);
		text.setStyleName(Styles.INSTANCE.tooltip().tooltipText());
		
		content.setWidget(0, 0, arrow);
		content.setWidget(1, 0, text);
		
		setWidget(content);
		
		// add mouse handlers
		widget.addMouseOverHandler(new OnMouseOver());
		widget.addMouseOutHandler(new OnMouseOut());
		
		// enable animation
		setAnimationEnabled(true);
		animation = new FadeAnimation(this.getElement());
	}

	public FocusWidget getWidget() {
		return widget;
	}
	
	@Override
	protected void onLoad() {
		/*
		 * tooltip will appears:
		 * - 5 pixel under the widget
		 * - in the horizontal middle of widget
		 */
		int tooltipAbsoluteTop = widget.getAbsoluteTop() + widget.getOffsetHeight() + PADDING_BOTTOM;
		int tooltipAbsoluteLeft = widget.getAbsoluteLeft() + (widget.getOffsetWidth() / 2) - (getOffsetWidth() / 2);
		setPopupPosition(tooltipAbsoluteLeft, tooltipAbsoluteTop);
	}

	// mouse over handler: show the tooltip
	private class OnMouseOver implements MouseOverHandler {
		
		@Override
		public void onMouseOver(MouseOverEvent event) {
			animation.fade(50, 1.0);
			show();
		}
	}
	
	// mouse out handler: hide the tooltip
	private class OnMouseOut implements MouseOutHandler {
		@Override
		public void onMouseOut(MouseOutEvent event) {
			animation.fade(50, 1.0);
			hide();
		}
	}
}
