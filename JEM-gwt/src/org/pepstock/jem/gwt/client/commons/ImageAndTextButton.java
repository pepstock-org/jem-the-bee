/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013  Marco "Fuzzo" Cuccato
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

import org.pepstock.jem.log.JemRuntimeException;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;

/**
 * Builds a button with both image and a text 
 * @author Marco "Fuzzo" Cuccato
 *
 */
public final class ImageAndTextButton extends Button {

	private static final String TEXT_LEFT_PADDING = " ";
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	/**
	 * Used to set if the text should be at right or at bottom of image 
	 * @author Marco "Fuzzo" Cuccato
	 */
	public enum TextPosition {
		/**
		 * Constant to be used to set the text next to image
		 */
		RIGHT, 
		/**
		 * Constant to be used to set the text under the image
		 */
		BOTTOM;
	}
	
	private String label;
	private TextPosition textPosition = TextPosition.RIGHT; 

	/**
	 * Builds the button with the necessary parts 
	 * @param icon the {@link ImageResource} to be used as the button image
	 * @param label the button label
	 * @param position the {@link TextPosition}
	 */
	public ImageAndTextButton(ImageResource icon, String label, TextPosition position) {
		this.textPosition = position;
		setText(label);
		setIcon(icon);
		// overrides min/max height/width css default attributes
		addStyleName(Styles.INSTANCE.common().noMinMaxHeightWidth());
	}
	
	/**
	 * Set the button icon
	 * @param imageResource the {@link ImageResource} to be used
	 */
	public final void setIcon(ImageResource imageResource) {
		Image img = new Image(imageResource);
		img.addStyleName(Styles.INSTANCE.common().verticalAlignMiddle());
		DOM.insertBefore(getElement(), img.getElement(),
				DOM.getFirstChild(getElement()));
	}

	@Override
	public final void setText(String text) {
		this.label = text;
		com.google.gwt.user.client.Element textElement;
		switch (textPosition) {
		case RIGHT:
			textElement = DOM.createElement("span");
			textElement.setInnerText(TEXT_LEFT_PADDING + text);
			break;
		case BOTTOM:
			textElement = DOM.createElement("div");
			textElement.setInnerText(text);
			break;
		default:
			throw new JemRuntimeException("Unable to know text position"); 
		}
		textElement.addClassName(Styles.INSTANCE.common().verticalAlignMiddle());
		DOM.insertChild(getElement(), textElement, 0);
	}

	@Override
	public String getText() {
		return this.label;
	}

}