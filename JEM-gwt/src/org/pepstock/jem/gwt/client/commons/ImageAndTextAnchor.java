/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015  Marco "Fuzzo" Cuccato
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

import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;

/**
 * Utility class for displaying an image (throught {@link ImageResource} with an {@link Anchor} 
 * @author Marco "Fuzzo" Cuccato
 *
 */
public final class ImageAndTextAnchor extends Anchor {

	private static final String TEXT_LEFT_PADDING = " ";
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	private String label;

	/**
	 * Build the object
	 * @param icon the {@link ImageResource} used as an icon
	 * @param label the anchor label
	 */
	public ImageAndTextAnchor(ImageResource icon, String label) {
		setText(label);
		setIcon(icon);
	}
	
	/**
	 * Set the icon 
	 * @param imageResource the {@link ImageResource} used as the icon
	 */
	public final void setIcon(ImageResource imageResource) {
		Image img = new Image(imageResource);
		img.addStyleName(Styles.INSTANCE.common().verticalAlignMiddle());
		DOM.insertBefore(getElement(), img.getElement(),
				DOM.getFirstChild(getElement()));
	}

	/**
	 * Set the anchored text
	 */
	@Override
	public final void setText(String text) {
		this.label = text;
		Element textElement;
		textElement = DOM.createElement("span");
		textElement.setInnerText(TEXT_LEFT_PADDING + text);
		textElement.addClassName(Styles.INSTANCE.common().verticalAlignMiddle());
		DOM.insertChild(getElement(), textElement, 0);
	}

	/**
	 * @see Anchor#getText()
	 */
	@Override
	public String getText() {
		return this.label;
	}

}