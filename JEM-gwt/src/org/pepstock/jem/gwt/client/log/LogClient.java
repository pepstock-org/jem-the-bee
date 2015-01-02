/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015 Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.log;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Is a singleton wrapper of GWT client side logger.
 * @author Marco "Fuzzo" Cuccato
 */
public final class LogClient {

	private static final String NAME = "jem-gwt-client";
	
	private static final LogClient LOGCLIENT = new LogClient();

	private static final Logger LOGGER = Logger.getLogger(NAME);

	/**
	 * to avoind any instantiation
	 */
	private LogClient() {

	}

	/**
	 * Returns the log application instance. Is a static reference, so is
	 * available everywhere.<br>
	 * If instance is null, creates a new one, using the private constructor.
	 * 
	 * @return log application wrapper instance
	 */
	public static final LogClient getInstance() {
		return LOGCLIENT;
	}


	/**
	 * Log a CONFIG message
	 * @param message
	 */
	public void config(String message) {
		log(Level.CONFIG, message);
	}
	
	/**
	 * Log a FINE message
	 * @param message
	 */
	public void fine(String message) {
		LOGGER.fine(message);
	}
	
	/**
	 * Log a FINER message
	 * @param message
	 */
	public void finer(String message) {
		LOGGER.finer(message);
	}

	/**
	 * Log a FINEST message
	 * @param message
	 */
	public void finest(String message) {
		LOGGER.finest(message);
	}

	/**
	 * Log an INFO message
	 * @param message
	 */
	public void info(String message) {
		LOGGER.info(message);
	}
	
	/**
	 * Log a WARNING message
	 * @param message
	 */
	public void warning(String message) {
		LOGGER.warning(message);
	}
	
	/**
	 * Log a SEVERE message
	 * @param message
	 */
	public void severe(String message) {
		LOGGER.severe(message);
	}
	
	/**
	 * Log a WARNING message with a {@link Throwable}
	 * @param message
	 * @param t the {@link Throwable}
	 */
	public void warning(String message, Throwable t) {
		log(Level.WARNING, message, t);
	}
	
	/**
	 * Log a SEVERE message with a {@link Throwable}
	 * @param message
	 * @param t the {@link Throwable}
	 */
	public void severe(String message, Throwable t) {
		log(Level.SEVERE, message, t);
	}
	
	/**
	 * Log a message to specific {@link Level}
	 * @param level the log {@link Level}
	 * @param message the message
	 */
	public void log(Level level, String message) {
		LOGGER.log(level, message);
	}
	
	/**
	 * Log just an exception at WARNING level, with it's message as text 
	 * @param t
	 */
	public void warning(Throwable t) {
		warning(t.getMessage(), t);
	}
	
	/**
	 * Log just an exception at SEVERE level, with it's message as text 
	 * @param t
	 */
	public void severe(Throwable t) {
		severe(t.getMessage(), t);
	}
	
	/**
	 * Log a message and a {@link Throwable} to specific {@link Level}
	 * @param level the log {@link Level}
	 * @param message the message
	 * @param t the associated {@link Throwable}
	 */
	protected void log(Level level, String message, Throwable t) {
		LOGGER.log(level, message, t);
	}
}
