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

/**
 * A {@link FilterableHeader} by {@link String}
 * @author Andrea "Stock" Stocchero
 */
public class TextFilterableHeader extends FilterableHeader<String> {

	private String text;
	
	/**
	 * Builds the header
	 * @param text the header text
	 * @param filterName the associated filter name
	 */
	public TextFilterableHeader(String text, String filterName) {
		this(text, filterName, null);
	}
	
	/**
	 * Builds the header
	 * @param text the header text
	 * @param filterName the associated filter name
	 * @param helpPattern an help text displayed to let user write input correctly
	 */
	public TextFilterableHeader(String text, String filterName, String helpPattern) {
		super(new TextFilteredCell(helpPattern), filterName);
		((TextFilteredCell)getCell()).setHeader(this);
		this.text = text;
	}
	
	@Override
	public String getValue() {
		return text;
	}

}