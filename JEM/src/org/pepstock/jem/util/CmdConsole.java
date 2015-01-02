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
package org.pepstock.jem.util;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.commons.lang3.StringUtils;

/**
 * Console uses to call JEM by HTTP, helpful to insert password or other commands from command line.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class CmdConsole {
	
	private PrintWriter writer = null;
	
	private BufferedReader reader = null;
	
	private Console console = System.console();

	/**
	 * Saves reader and writer depending on console instance.<br>
	 * In some environment (as Eclipse), you can instantiate any java Console.
	 */
	public CmdConsole() {
		if (console == null){
			reader = new BufferedReader(new InputStreamReader(System.in, CharSet.DEFAULT));
			writer = new PrintWriter(new OutputStreamWriter(System.out, CharSet.DEFAULT));
		} else {
			reader = new BufferedReader(console.reader());
			writer = console.writer();
		}
	}
	
	/**
	 * Reads the password
	 * 
	 * @param userid userid of user who want s to act in this console
	 * @return password value
	 * @throws IOException if any exception occurs
	 */
	public String readPassword(String userid) throws IOException{
		String prompt = userid+"'s password: ";
		if (console == null){
			writer.print(prompt);
			writer.flush();
			return reader.readLine();
		} else {
			char[] passwordChar = console.readPassword(prompt);
			return new String(passwordChar);
		}
	}

	/**
	 * Reads a command line
	 * 
	 * @param prompt prompt to show
	 * @return arrays of word which represent the command, otherwise <code>null</code>
	 * @throws IOException if any exception occurs
	 */
	public String[] readCommand(String prompt) throws IOException{
		String record;
		if (console == null){
			writer.print(prompt);
			writer.flush();
			record = reader.readLine();
			if (record == null){
				record = "";
			}
		} else {
			record = console.readLine(prompt, (Object[]) null);
		}
		if (record.trim().length() > 0){
			return StringUtils.split(record, " ");
		}
		return new String[0];
	}

	/**
	 * Prints a record
	 * @param record record to print in a buffer format
	 */
	public void print(StringBuilder record){
		print(record.toString());
	}

	/**
	 * Prints a record
	 * 
	 * @param record string to print
	 */
	public void print(String record){
		writer.println(record);
		writer.flush();
	}
	
	/**
	 * Returns the writer
	 * 
	 * @return the writer
	 */
	public PrintWriter getWriter() {
		return writer;
	}

	/**
	 * Returns the reader
	 * 
	 * @return the reader
	 */
	public BufferedReader getReader() {
		return reader;
	}

}