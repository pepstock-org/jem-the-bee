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
package org.pepstock.jem.jppf;

import java.io.Serializable;

/**
 * Contains the chunk information (start and end offset, and calculated length).
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class ChunkDefinition implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private long start = 0;
	
	private long end = 0;
	
	private int length = 0;
	
	/**
	 * Constructs a chunk with start and end offset
	 * @param start start offset
	 * @param end end offset
	 */
	public ChunkDefinition(long start, long end) {
		this.start = start;
		this.end = end;
		// calculate length, adding 1 because
		// bith start and end are included in chunk
		this.length = (int)(end - start + 1);
	}

	/**
	 * @return the start
	 */
	public long getStart() {
		return start;
	}

	/**
	 * @return the end
	 */
	public long getEnd() {
		return end;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChunkDefinition [start=" + start + ", end=" + end + ", length=" + length + "]";
	}


}
