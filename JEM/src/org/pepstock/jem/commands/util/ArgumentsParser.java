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
package org.pepstock.jem.commands.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.NodeMessage;

/**
 * Utility class to parse the arguments of command line
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class ArgumentsParser {

	private String commandLine = null;

	private final List<Option> options = new ArrayList<Option>();

	/**
	 * Constructor with command line (used only on help message) and all
	 * options.
	 * 
	 * @param commandLine command line
	 * @param options options to check
	 * 
	 */
	public ArgumentsParser(String commandLine) {
		this.commandLine = commandLine;
	}

	/**
	 * Returns the commnd line
	 * 
	 * @return the commandLine
	 */
	public String getCommandLine() {
		return commandLine;
	}

	/**
	 * Returns all options
	 * 
	 * @return the options
	 */
	public List<Option> getOptions() {
		return options;
	}

	/**
	 * Parses all arguments passed by command line.<br>
	 * Creates all possible arguments with explanation to show in case of error.
	 * 
	 * @param args arguments of main method
	 * @return properties with all arguments
	 * @throws ParseException if same args are missing or wrong
	 */
	public Properties parseArg(String[] args) throws ParseException {

		// creates all possible commands options

		// -help options
		Option help = new Option("help", "print this message");

		// loads all created options
		Options allOptions = new Options();
		allOptions.addOption(help);

		for (Option opt : options){
			allOptions.addOption(opt);
		}
		// creates command line parser
		CommandLineParser parser = new PosixParser();
		CommandLine line;
		try {
			Properties properties = new Properties();
			// parses line
			line = parser.parse(allOptions, args);
			// checks if asked for help. If yes, prints help
			if ((line.getOptions().length == 1) && line.hasOption("help")) {
				StringWriter writer = new StringWriter();
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(new PrintWriter(writer), HelpFormatter.DEFAULT_WIDTH, getCommandLine(), "", allOptions, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, "");
				LogAppl.getInstance().emit(NodeMessage.JEMC056I, writer.getBuffer());
			} else {
				Option[] optionsLine = line.getOptions();
				for (int i = 0; i < optionsLine.length; i++) {
					String key = optionsLine[i].getArgName();
					String value;
					// to avoid null pointer exception in case of option line
					// with no arg
					if (optionsLine[i].hasArg()) {
						value = optionsLine[i].getValue();
					} else {
						value = "";
					}

					properties.setProperty(key, value);
				}
			}
			return properties;
		} catch (ParseException e) {
			// if exception, returns -help command
			// because if is here has found an error on arguments
			StringWriter writer = new StringWriter();
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(new PrintWriter(writer), HelpFormatter.DEFAULT_WIDTH, getCommandLine(), "", allOptions, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, "");
			LogAppl.getInstance().emit(NodeMessage.JEMC056I, writer.getBuffer());
			throw e;
		}

	}

}