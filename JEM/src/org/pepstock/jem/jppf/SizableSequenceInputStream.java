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
package org.pepstock.jem.jppf;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Collections;
import java.util.List;

import org.pepstock.jem.log.LogAppl;

/**
 * Extends the SequenceInputStream using a ArrayLIst<InputStream> as arguments, instead of Enumeration.<br>
 * Furthermore it overrides the available methods, calculating the size of all inputstreams.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4	
 *
 */
public class SizableSequenceInputStream extends SequenceInputStream {
	
	private List<InputStream> inputstreams = null;
	
	private int totSize = -1;

	/**
	 * Creates a sequence input stream using a ist of streams.
	 * 
	 * @param streams collection of streams
	 */
	public SizableSequenceInputStream(List<InputStream> streams) {
		super(Collections.enumeration(streams));
		inputstreams = streams;
	}

	/* (non-Javadoc)
	 * @see java.io.InutStreamRunnable#available()
	 */
	@Override
	public int available() throws IOException {
		// if size is not already calculated,
		// then calculates!
		if (totSize == -1){
			// sets to zero, before calculation
			totSize = 0;
			// scans all inputstreams
			// asking for size and adding them
			for (InputStream is : inputstreams){
				try {
					totSize += is.available();
				} catch (IOException e) {
					// ignore
					LogAppl.getInstance().ignore(e.getMessage(), e);
					totSize = Math.max(0, totSize);
				}
			}
		}
		return totSize;
	}

}