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
package org.pepstock.jem.ant.tasks.utilities.gdg;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;

import org.pepstock.catalog.gdg.Root;
import org.pepstock.jem.ant.tasks.utilities.AntUtilMessage;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.Numbers;
import org.pepstock.jem.util.Parser;

/**
 * GDG command to rename a GDG generation.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3	
 *
 */
public class Rename extends Command {
	
	@SuppressWarnings("javadoc")
	public static final String COMMAND_KEYWORD = "RENAME";

	// format of GDG command
	private static final String RENAME_GDG_FORMAT = "RENAME GDG {0} {1} TO {2}";

	private static final MessageFormat FORMAT = new MessageFormat(RENAME_GDG_FORMAT);
	
	private String generation = null;
	
	private String fileTo = null;

	/**
	 * Parses the DELETE command.
	 * 
	 * @param commandLine command line
	 * @throws ParseException if command line has a syntax error
	 */
	public Rename(String commandLine) throws ParseException {
		super(commandLine);
		
		// parse command
		Object[] object =  FORMAT.parse(commandLine);
		// we must have only 1 object
		if (object.length == Numbers.N_3) {
			// sets gdg path
			setDDName(object[ELEMENT_1].toString());
			generation = object[ELEMENT_2].toString();
			fileTo = object[ELEMENT_3].toString();
		} else {
			throw new ParseException(AntUtilMessage.JEMZ004E.toMessage().getFormattedMessage(COMMAND_KEYWORD, commandLine), ELEMENT_1);
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.tasks.utilities.gdg.Command#execute()
	 */
	@Override
	public void perform(File file) throws IOException {
		int genFrom = Parser.parseInt(generation, Integer.MIN_VALUE);
		if (genFrom == Integer.MIN_VALUE){
			throw new IOException(AntUtilMessage.JEMZ049E.toMessage().getFormattedMessage(generation));
		}
		
		// creates root checking if to create generation 0
		Root root = new Root(file);
		
		String fileNameFrom = root.getVersion(generation);
		if (fileNameFrom == null){
			throw new IOException(AntUtilMessage.JEMZ050E.toMessage().getFormattedMessage(generation));		
		}
		
		File generationFile = new File(file, fileNameFrom);
		File generationFileTo = new File(file, fileTo);
		if (generationFile.exists()){
			if (generationFile.renameTo(generationFileTo)){
				root.getProperties().put(generation, fileTo);
				root.commit();
				LogAppl.getInstance().emit(AntUtilMessage.JEMZ051I, generationFile.getAbsolutePath(), generationFileTo.getAbsolutePath());
			} else {
				throw new IOException(AntUtilMessage.JEMZ052E.toMessage().getFormattedMessage(generationFile.getAbsolutePath()));	
			}
		} else {
			throw new IOException(AntUtilMessage.JEMZ050E.toMessage().getFormattedMessage(generation));		
		}
		// logs for creation
		LogAppl.getInstance().emit(AntUtilMessage.JEMZ001I, COMMAND_KEYWORD, file, generationFileTo.getAbsolutePath());
	}
}