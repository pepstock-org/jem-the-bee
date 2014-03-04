/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.gwt.client.charts;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.0
 */
public class ChartColor {
	
	private int red = 0;
	
	private int green = 0;
	
	private int blue = 0;

	/**
	 * @param red
	 * @param green
	 * @param blue
	 */
    public ChartColor(int red, int green, int blue) {
	    super();
	    this.red = red;
	    this.green = green;
	    this.blue = blue;
    }

	/**
	 * @return the red
	 */
	public int getRed() {
		return red;
	}

	/**
	 * @return the green
	 */
	public int getGreen() {
		return green;
	}

	/**
	 * @return the blue
	 */
	public int getBlue() {
		return blue;
	}

}
