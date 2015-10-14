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

import java.io.File;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.BuildException;
import org.pepstock.jem.commands.util.ArgumentsParser;
import org.pepstock.jem.node.rmi.InternalUtilities;
import org.pepstock.jem.node.rmi.UtilsInitiatorManager;
import org.pepstock.jem.node.stats.Sample;
import org.pepstock.jem.node.stats.TransformAndLoader;
import org.pepstock.jem.util.CharSet;
import org.pepstock.jem.util.DateFormatter;
import org.pepstock.jem.util.Parser;

import com.thoughtworks.xstream.XStream;

/**
 * Is a utility (both a task ANT and a main program) that collects all statistics files.<br>
 * Needs some arguments with a date or a number of days, to calculate right date, and a Transformer and Loader object
 * commands.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public class StatsCollectTask extends AntUtilTask {
	
	/**
	 * Key for the Date argument
	 */
	private static String DATE = "date";
	
	/**
	 * Key for the days argument
	 */
	private static String DAYS = "days";
	
	/**
	 * Key for the class to load to transform and load data  
	 */
	private static String CLASS = "class";
	
	private static final XStream STREAMER = new XStream();
	
	/**
	 * Empty constructor
	 */
	public StatsCollectTask() {
	}

	/**
	 * Sets itself as main program and calls <code>execute</code> method of
	 * superclass (StepJava) to prepare the COMMAND data description.
	 * 
	 * @throws BuildException occurs if an error occurs
	 */
	@Override
	public void execute() throws BuildException {
		super.setClassname(StatsCollectTask.class.getName());
		super.execute();
	}

	/**
	 * Main program, called by StepJava class.
	 * 
	 * @param args uuid of data description which contains all data descriptions so commands are able to get GDG name
	 * @throws Exception if COMMAND data description doesn't exists, if an
	 *             error occurs dduring the command parsing, if data mount point
	 *             is null.
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
		// parses args
		// -jcl optional arg
		Option dateArg = OptionBuilder.withArgName(DATE).hasArg().withDescription("date of stats file").create(DATE);
		// -type optional arg
		Option daysArg = OptionBuilder.withArgName(DAYS).hasArg().withDescription("number of days to add to calculate the date of stats files").create(DAYS);
		// -class mandatory arg
		Option classArg = OptionBuilder.withArgName(CLASS).hasArg().withDescription("class of TL to invoke reading the objects").create(CLASS);
		classArg.setRequired(true);
		
		// parses all arguments
		ArgumentsParser parser = new ArgumentsParser(StatsCollectTask.class.getName());
		parser.getOptions().add(dateArg);
		parser.getOptions().add(daysArg);
		parser.getOptions().add(classArg);
		
		// saves all arguments in common variables
		Properties properties = parser.parseArg(args);
		String dateParam = properties.getProperty(DATE);
		String daysParam = properties.getProperty(DAYS);
		
		if (daysParam == null && dateParam == null){
			throw new Exception(AntUtilMessage.JEMZ027E.toMessage().getFormattedMessage());
		} else if (dateParam != null){
			DateFormatter.getDate(dateParam);
		} else if (daysParam != null){
			int days = Parser.parseInt(daysParam, -1);
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, days);
			dateParam = DateFormatter.getDate(calendar.getTime(), DateFormatter.DEFAULT_DATE_FORMAT);
		}

		String classParam = properties.getProperty(CLASS);
		
		Object objectTL = Class.forName(classParam).newInstance();
		if (!(objectTL instanceof TransformAndLoader)) {
			throw new Exception(AntUtilMessage.JEMZ028E.toMessage().getFormattedMessage(classParam, TransformAndLoader.class.getName(), objectTL.getClass().getName()));
		}
		TransformAndLoader tl = (TransformAndLoader)objectTL;
		
		System.out.println(AntUtilMessage.JEMZ029I.toMessage().getFormattedMessage(dateParam, tl.getClass().getName()));
		
		InternalUtilities util = UtilsInitiatorManager.getInternalUtilities();
		File statsFolder = util.getStatisticsFolder();
		int filesCount = 0;
		if (statsFolder == null){
			System.out.println(AntUtilMessage.JEMZ057W.toMessage().getFormattedMessage());
		} else if (statsFolder.exists()){
			Iterator<File> files = FileUtils.iterateFiles(statsFolder, new String[]{dateParam}, false);

			for (; files.hasNext();){
				int lineNumber = 0;
				File file = files.next();
				filesCount++;
				try {
					tl.fileStarted(file);
					Scanner sc = new Scanner(file, CharSet.DEFAULT_CHARSET_NAME);
					sc.useDelimiter("\n");
					while (sc.hasNext()) {
						String record = sc.next().toString();
						lineNumber++;
						try {
							Object obj = STREAMER.fromXML(record);
							if (obj instanceof Sample){
								Sample sample = (Sample)obj;
								try{
									tl.loadSuccess(sample);
								} catch (Exception ex){
									System.out.println(AntUtilMessage.JEMZ030W.toMessage().getFormattedMessage(tl.getClass().getName(), lineNumber, ex.getMessage()));
								}
							}
						} catch (Exception ex){
							tl.loadFailed(record, lineNumber, ex);
							System.out.println(AntUtilMessage.JEMZ031W.toMessage().getFormattedMessage(lineNumber, ex.getMessage()));
						}
					}
					sc.close();
					tl.fileEnded(file);
				} catch (Exception ex){
					System.out.println(AntUtilMessage.JEMZ032W.toMessage().getFormattedMessage(tl.getClass().getName(), file.getAbsolutePath()));
				}
			}
		} else {
			System.out.println(AntUtilMessage.JEMZ033E.toMessage().getFormattedMessage(statsFolder.getAbsolutePath()));
		} 
		System.out.println(AntUtilMessage.JEMZ034I.toMessage().getFormattedMessage(filesCount));
	}

}