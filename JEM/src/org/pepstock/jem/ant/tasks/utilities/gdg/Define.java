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

import org.pepstock.catalog.gdg.GDGUtil;
import org.pepstock.catalog.gdg.Root;
import org.pepstock.jem.ant.tasks.utilities.AntUtilMessage;
import org.pepstock.jem.log.LogAppl;

/**
 * GDG command to define a new GDG. Creates a new directory for GDG (passed by command) and creates a empty generation 0 if asked.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3	
 *
 */
public class Define extends Command {
	
	@SuppressWarnings("javadoc")
	public static final String COMMAND_KEYWORD = "DEFINE";

	// format of GDG command
	private static final String DEFINE_GDG_FORMAT = "DEFINE GDG {0}";

	private static final String DEFINE_GDG_NOEMPTY_FORMAT = "DEFINE GDG {0} NOEMPTY";
	
	private static final MessageFormat FORMAT = new MessageFormat(DEFINE_GDG_FORMAT);
	
	private static final MessageFormat NOEMPTY_FORMAT = new MessageFormat(DEFINE_GDG_NOEMPTY_FORMAT);
	
	private boolean noEmpty = false;

	/**
	 * Parses the DEFINE command.
	 * 
	 * @param commandLine command line
	 * @throws ParseException if command line has a syntax error
	 */
	public Define(String commandLine) throws ParseException {
		super(commandLine);
		
		// parse command, without NOEMPTY
		Object[] object = null;
		try {
			// checks if is NOEMPTY
			object = NOEMPTY_FORMAT.parse(commandLine);
			setNoEmpty(true);
		} catch (ParseException e) {
			// if has a Parse Exception, try without NOEMPTY
			object = FORMAT.parse(commandLine);
			setNoEmpty(false);
		}
		// we must have only 1 object
		if (object.length == 1) {
			// sets gdg path
			setDDName(object[0].toString());
		} else {
			throw new ParseException(AntUtilMessage.JEMZ004E.toMessage().getFormattedMessage(COMMAND_KEYWORD, commandLine), 0);
		}
	}

	/**
	 * Returns if a generation 0 must be created
	 * 
	 * @return the noEmpty
	 */
	public boolean isNoEmpty() {
		return noEmpty;
	}

	/**
	 * Sets if a generation 0 must be created
	 * 
	 * @param noEmpty the noEmpty to set
	 */
	public void setNoEmpty(boolean noEmpty) {
		this.noEmpty = noEmpty;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.tasks.utilities.gdg.Command#execute()
	 */
	@Override
	public void perform(File file) throws IOException {
		// creates root checking if to create generation 0
		Root root = GDGUtil.createGDG(file, isNoEmpty());
		// logs for creation
		LogAppl.getInstance().emit(AntUtilMessage.JEMZ001I, COMMAND_KEYWORD, file, file.getAbsolutePath());
		if (isNoEmpty()){
			LogAppl.getInstance().emit(AntUtilMessage.JEMZ005I, GDGUtil.getGenerationIndex(root, 0));
		}
	}


}