/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Marco "Fuzzo" Cuccato
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

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Common helper methods for UI widgets styles 
 * @author Marco "Fuzzo" Cuccato
 *
 */
public final class UITools {

	private UITools() {
	}
	
	/**
	 * Set styles to FlexTable
	 * @param t
	 * @param oddRowStyle
	 * @param evenRowStyle
	 * @param keyStyle
	 */
	public static void setFlexTableStyles(FlexTable t, String oddRowStyle, String evenRowStyle, String keyStyle) {
		setFlexTableRowStyles(t, oddRowStyle, evenRowStyle);
		setColumnKeyValueStyle(t, keyStyle);
	}
	
	/**
	 * Make a Flextable easy readable setting some styles
	 * @param t FlexTable
	 * @param oddRowStyle style of odd rows
	 * @param evenRowStyle style of even rows
	 */
	public static void setFlexTableRowStyles(FlexTable t, String oddRowStyle, String evenRowStyle) {
		RowFormatter rf = t.getRowFormatter();
		for (int i=0; i<t.getRowCount(); i++) {
			if (i % 2 == 0) {
				rf.setStyleName(i, oddRowStyle);
			} else {
				rf.setStyleName(i, evenRowStyle);
			}
		}
	}

	/**
	 * Format FlexTable columns in order to make columns easy-readable  
	 * @param t the FlexTable
	 * @param style Odd column style
	 */
	public static void setColumnKeyValueStyle(FlexTable t, String style) {
		setColumnKeyValueStyle(t, style, false);
	}
	
	/**
	 * Format FlexTable columns in order to make columns easy-readable  
	 * @param t the FlexTable
	 * @param style Odd column style
	 * @param skipFirstRow if <code>true</code> leave the first row as is (for table header)
	 */
	public static void setColumnKeyValueStyle(FlexTable t, String style, boolean skipFirstRow) {
		FlexCellFormatter cf = t.getFlexCellFormatter();
		int i = skipFirstRow ? 1 : 0;
		for (i=0; i<t.getRowCount(); i++) {
			for (int j=0; j<t.getCellCount(i); j++) {
				if (j % 2 == 0) {
					cf.addStyleName(i, j, style);
				}
			}
		}
	}
	
	/**
	 * Format FlexTable header in order to make header easy-readable  
	 * @param t the FlexTable
	 * @param style Odd column style
	 */
	public static void setHeaderStyle(FlexTable t, String style) {
		FlexCellFormatter cf = t.getFlexCellFormatter();
		for (int i=0; i<t.getCellCount(0); i++) {
			cf.addStyleName(0, i, style);
		}
	}
	
	/**
	 * Simple tool to used to know if a Widget is visible, and if it is in foreground 
	 * @param object
	 * @return <code>true</code> if the parameter is visible and in foreground
	 */
	public static boolean isInForegroundVisible(UIObject object) {
		return object.isVisible() && object.getOffsetWidth() > 0 && object.getOffsetHeight() > 0;
	}
}