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
package org.pepstock.jem.log;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.xml.DOMConfigurator;
import org.pepstock.jem.util.CharSet;

/**
 * Is a singleton wrapper of Log4j logger, to avoid to use freely Log4j but to
 * emit messages well-defined.
 * 
 * @see Message
 * @see Messages
 * @author Andrea "Stock" Stocchero
 * 
 */
public final class LogAppl {

	/**
	 * Is the variable name to use in command line to start JEM node.<br>
	 * The value of property is Log4j configuration file name.<br>
	 * Example:<br>
	 * <code>-Dlog4j.config=log4j.xml</code>
	 */
	public static final String LOG4J_CONFIGURATION_PROPERTY = "log4j.config";

	private static final String NAME = "jem";

	private static LogAppl LOGAPPL = null;

	private static final Logger LOGGER = Logger.getLogger(NAME);

	/**
	 * Private constructor which creates a static instance.
	 * If system properties doesn't exist, uses a basic configuration of Log4j
	 */
	private LogAppl() {
		// gets system property
		String fileName = System.getProperty(LOG4J_CONFIGURATION_PROPERTY);
		// if exists, load XML configuration
		if (fileName != null) {
			DOMConfigurator.configure(fileName);
		} else {
			// otherwise basic configuration
			ConsoleAppender consoleAppeder =new ConsoleAppender();
			consoleAppeder.setName("consoleAppender");
			OutputStreamWriter osw = new OutputStreamWriter(System.out, CharSet.DEFAULT);
			consoleAppeder.setWriter(new PrintWriter(osw));
			// sets INFo as level
			consoleAppeder.setThreshold(Level.INFO);
			// default layout
			PatternLayout layout = new PatternLayout();
			layout.setConversionPattern("%d{yyyy MM dd HH:mm:ss} %-6p [%t] %m%n");
			consoleAppeder.setLayout(layout);
			BasicConfigurator.resetConfiguration();
			BasicConfigurator.configure(consoleAppeder);
		}
	}
	
	/**
	 * Returns the log application instance. Is a static reference, so is
	 * available everywhere.<br>
	 * If instance is null, creates a new one, using a private constructor.
	 * 
	 * @return log application wrapper instance
	 */
	public static LogAppl getInstance() {
		if (LOGAPPL == null) {
			LOGAPPL = new LogAppl();
		}
		return LOGAPPL;
	}
	
	/**
	 * Emit a message, without any formatting actions.
	 * 
	 * @param message log record
	 */
	public void emit(MessageInterface message) {
		this.emit(message, (Object[]) null);
	}

	/**
	 * Emit a message and uses the passed objects to format the message itself,
	 * objects represent runtime info to display.
	 * 
	 * @param message log record
	 * @param objects string to format inside the message
	 */
	public void emit(MessageInterface message, Object... objects) {
		this.emit(message, null, objects);
	}

	/**
	 * Emit a message, without any formatting actions, but with an exception to
	 * print.
	 * 
	 * @param message log record
	 * @param exception exception to print
	 */
	public void emit(MessageInterface message, Throwable exception) {
		this.emit(message, exception, (Object[]) null);
	}

	/**
	 * Emit a message and uses the passed objects to format the message itself,
	 * objects represent runtime info to display.<br>
	 * Prints stack trace of exception.
	 * 
	 * @param message log record
	 * @param exception exception to print
	 * @param objects string to format inside the message
	 */
	public void emit(MessageInterface message, Throwable exception, Object... objects) {
		// if no parameters, do not format!
		String outputMessage = (objects == null) ? message.toMessage().getContent() : message.toMessage().getFormattedMessage(objects);

		// based on level and on exception, call Log4j logger to print the
		// message
		switch (message.toMessage().getLevel()) {
			case INFO:
				LOGGER.info(outputMessage);
				break;
			case WARNING:
				warning(outputMessage, exception);
				break;
			case ERROR:
				error(outputMessage, exception);
				break;
			default:
				LOGGER.info(outputMessage);
				break;
		}
	}
	
	/**
	 * It manages the error 
	 */
	private void error(String outputMessage, Throwable exception){
		if (exception == null){
			LOGGER.error(outputMessage);
		} else {
			LOGGER.error(outputMessage, exception);
		}
	}
	
	/**
	 * It manages the warning 
	 */
	private void warning(String outputMessage, Throwable exception){
		if (exception == null){
			LOGGER.warn(outputMessage);
		} else {
			LOGGER.warn(outputMessage, exception);
		}
	}
	
	/**
	 * Print a object string representation on defined appender in debug mode
	 * @param context context to print
	 */
	public void debug(String context){
		LOGGER.debug(context);
	}
	
	/**
	 * Print a exception on defined appender in debug mode
	 * @param context context to print
	 * @param exception exception to print
	 */
	public void debug(String context, Throwable exception){
		LOGGER.debug(context, exception);
	}
	
	/**
	 * Ignores the exception
	 * @param context context to ignore
	 * @param exception exception to ignore
	 */
	public void ignore(String context, Throwable exception){
		// redirect to debug for SONAR issue
		debug(context, exception);
	}
}