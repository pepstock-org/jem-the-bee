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

import com.google.gwt.resources.client.ImageResource;

/**
 * Enumeration with all images used to represent the node status
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public enum NodeStatusImages {

	@SuppressWarnings("javadoc")
    UNKNOWN("UNKNOWN", Images.INSTANCE.ledGray18()),
    @SuppressWarnings("javadoc")
	STARTING("STARTING", Images.INSTANCE.ledLightGreen18()),
	@SuppressWarnings("javadoc")
	INACTIVE("INACTIVE", Images.INSTANCE.ledBlue18()),
	@SuppressWarnings("javadoc")
	ACTIVE("ACTIVE", Images.INSTANCE.ledGreen18()),
	@SuppressWarnings("javadoc")
	DRAINED("DRAINED", Images.INSTANCE.ledRed18()),
	@SuppressWarnings("javadoc")
	DRAINING("DRAINING", Images.INSTANCE.ledYellow18()),
	@SuppressWarnings("javadoc")
	SHUTTING_DOWN("SHUTTING_DOWN", Images.INSTANCE.ledGray18());

	private String value;
	private ImageResource image;

	/**
	 * Private constructor
	 * @param value text of status
	 * @param image image which represent the status
	 */
	private NodeStatusImages(String value, ImageResource image) {
		this.value = value;
		this.image = image;
	}
	
	/**
	 * Returns the image of status
	 * @return the image of status
	 */
	public ImageResource getImage() {
		return image;
	}
	
	/**
	 * Returns the text of status
	 */
	@Override
	public String toString() {
		return value;
	}

}
