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
package org.pepstock.jem.ant.tasks.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.pepstock.jem.ant.tasks.StepJava;
import org.pepstock.jem.util.CharSet;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public abstract class AntUtilTask extends StepJava {

	/**
	 * Reads a input stream putting all in a string buffer for further parsing.
	 * 
	 * @param is
	 *            input stream with all commands
	 * @return string buffer with all commands
	 * @throws IOException
	 *             if IO error occurs
	 */
	public static final StringBuilder read(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		Scanner sc = new Scanner(is, CharSet.DEFAULT_CHARSET_NAME);
		sc.useDelimiter("\n");
		while (sc.hasNext()) {
			String record = sc.next().toString();
			sb.append(record.trim()).append(' ');
		}
		sc.close();
		return sb;
	}

}