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
package org.pepstock.jem.log;

import java.io.StringWriter;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Custom LOG4J appender to save a configurable numbers of log records in a list because they could be showed by user interface.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class InMemoryAppender extends WriterAppender {
	
	private static StringWriter buffer = new StringWriter();
	
	/**
	 * Constructs the object, adding the following pattern: <code>%d{yyyy MM dd HH:mm:ss} %-6p [%t] %m%n</code>
	 */
	public InMemoryAppender() {
        super(new PatternLayout(), buffer);
        // flushes always
        setImmediateFlush(true);
        // new pattern
		PatternLayout layout = new PatternLayout();
		layout.setConversionPattern("%d{yyyy MM dd HH:mm:ss} %-6p [%t] %m%n");
		setLayout(layout);
	}

	@Override
    public void append(LoggingEvent loggingEvent) {
		// it uses always the same stringbuffer
		// for performances
    	StringBuffer sb = buffer.getBuffer();
    	sb.delete(0, sb.length());
        super.append(loggingEvent);
        LogBuffer.getInstance().addLog(sb);
    }
    
	/**
	 * @return the maxRows
	 */
	public long getMaxRows() {
		return LogBuffer.getInstance().getMaxRows();
	}

	/**
	 * @param maxRows the maxRows to set
	 */
	public void setMaxRows(long maxRows) {
		LogBuffer.getInstance().setMaxRows(maxRows);
	}
}