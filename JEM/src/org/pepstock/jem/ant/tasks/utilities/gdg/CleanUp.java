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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.pepstock.catalog.gdg.Root;
import org.pepstock.jem.ant.tasks.utilities.AntUtilMessage;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.Parser;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public class CleanUp extends Command {

	@SuppressWarnings("javadoc")
	public static final String COMMAND_KEYWORD = "CLEANUP";

	// format of GDG command
	private static final String CLEANUP_GDG_FORMAT = "CLEANUP GDG {0} VERSIONS {1,number,integer}";

	private static final MessageFormat FORMAT = new MessageFormat(CLEANUP_GDG_FORMAT);

	// with 0 ignore any action
	private static final int NOVERSIONS = 0;

	private int versions = NOVERSIONS;

	/**
	 * Parses the DEFINE command.
	 * 
	 * @param commandLine
	 *            command line
	 * @throws ParseException
	 *             if command line has a syntax error
	 */
	public CleanUp(String commandLine) throws ParseException {
		super(commandLine);

		// parse command
		Object[] object = FORMAT.parse(commandLine);
		// we must have only 1 object
		if (object.length == 2) {
			// sets gdg path
			setDDName(object[0].toString());

			// gets versions
			int gdgVersions = Parser.parseInt(object[1].toString(), NOVERSIONS);
			// versions could less than zero but minimum value must be 0
			versions = Math.max(gdgVersions, NOVERSIONS);
			setVersions(versions);
		} else {
			throw new ParseException(AntUtilMessage.JEMZ004E.toMessage().getFormattedMessage(COMMAND_KEYWORD, commandLine), 0);
		}
	}

	/**
	 * Returns the relative versions to clean
	 * 
	 * @return the versions
	 */
	public int getVersions() {
		return versions;
	}

	/**
	 * Sets the relative versions to clean
	 * 
	 * @param versions
	 *            the versions to set
	 */
	public void setVersions(int versions) {
		this.versions = versions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.ant.tasks.utilities.gdg.Command#execute()
	 */
	@Override
	public void perform(File file) throws IOException {
		// if NOVERSIONS, do not anything
		if (getVersions() == 0) {
			LogAppl.getInstance().emit(AntUtilMessage.JEMZ001I, COMMAND_KEYWORD, file, file.getAbsolutePath());
			return;
		}
		// logs for creation
		Root root = new Root(file);

		// gets all properties of root, sorts keys by a list
		Properties properties = root.getProperties();
		// if properties elements count (-1 because there is the key for last
		// generation to ignore)
		// is less than versions return, nothing to clean
		if ((properties.size() - 1) <= getVersions()) {
			LogAppl.getInstance().emit(AntUtilMessage.JEMZ001I, COMMAND_KEYWORD, file, file.getAbsolutePath());
			dumpRoot(file);
			return;
		}
		@SuppressWarnings("unchecked")
		List<String> keysList = (ArrayList<String>) Collections.list(properties.propertyNames());
		Collections.sort(keysList);

		// calculates the amount of files to remove (-1 because there is the key
		// for last generation to ignore)
		int rowsCountToremove = keysList.size() - 1 - getVersions();

		// scans all keys
		for (String key : keysList) {
			// ignores key for last generation
			if (!key.equalsIgnoreCase(Root.LAST_GENERATION_PROPERTY) &&
					// if has still some rows to delete
					rowsCountToremove > 0) {
				// gest filename and creates a file object for key
				String fileName = properties.getProperty(key);
				File genFile = new File(file, fileName);
				// delete the file!
				if (!genFile.delete()) {
					LogAppl.getInstance().emit(AntUtilMessage.JEMZ002W, genFile);
				} else {
					LogAppl.getInstance().emit(AntUtilMessage.JEMZ003I, genFile);
					root.getProperties().remove(key);
				}
				rowsCountToremove--;
			}
		}
		// stores the root properties
		root.commit();
		
		dumpRoot(file);
		// logs for creation
		LogAppl.getInstance().emit(AntUtilMessage.JEMZ001I, COMMAND_KEYWORD, file, file.getAbsolutePath());
	}
	
	private void dumpRoot(File file) throws IOException{
		// Because of some errors reading GDG in a NAS, read again the root
		Root reroot = new Root(file);
		LogAppl.getInstance().emit(AntUtilMessage.JEMZ041I, reroot.getProperties());
	}

}