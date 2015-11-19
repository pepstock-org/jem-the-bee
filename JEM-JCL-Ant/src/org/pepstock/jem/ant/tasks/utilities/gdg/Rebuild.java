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

import org.apache.commons.lang3.StringUtils;
import org.pepstock.catalog.gdg.GDGUtil;
import org.pepstock.catalog.gdg.Root;
import org.pepstock.jem.ant.tasks.utilities.AntUtilMessage;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.util.Numbers;
import org.pepstock.jem.util.Parser;

/**
 * GDG command to rebuild a GDG generations. By command decides to use root properties files as master or the file system using the generations files.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3	
 *
 */
public class Rebuild extends Command {
	
	@SuppressWarnings("javadoc")
	public static final String COMMAND_KEYWORD = "REBUILD";

	// format of GDG command
	private static final String REBUILD_GDG_FORMAT = "REBUILD GDG {0}";

	private static final String REBUILD_GDG_BY_ROOT_FORMAT = "REBUILD GDG {0} MASTER(ROOT)";

	private static final String REBUILD_GDG_BY_GENERATIONS_FORMAT = "REBUILD GDG {0} MASTER(GENERATIONS)";
	
	private static final MessageFormat FORMAT = new MessageFormat(REBUILD_GDG_FORMAT);
	
	private static final MessageFormat BYROOT_FORMAT = new MessageFormat(REBUILD_GDG_BY_ROOT_FORMAT);
	
	private static final MessageFormat BYGENERATIONS_FORMAT = new MessageFormat(REBUILD_GDG_BY_GENERATIONS_FORMAT);
	
	private boolean isByRoot = true;

	/**
	 * Parses the REBUILD command.
	 * 
	 * @param commandLine command line
	 * @throws ParseException if command line has a syntax error
	 */
	public Rebuild(String commandLine) throws ParseException {
		super(commandLine);
		
		// parse command, MASTER ROOT
		Object[] object = null;
		try {
			// checks if is MASTER ROOT
			object = BYROOT_FORMAT.parse(commandLine);
			setByRoot(true);
		} catch (ParseException e0) {
			// if has a Parse Exception, try MASTER GENERATIONS
			try{
				object = BYGENERATIONS_FORMAT.parse(commandLine);
				setByRoot(false);
			} catch (ParseException e1) {
				// if has a Parse Exception, try default
				object = FORMAT.parse(commandLine);
				setByRoot(true);
			}
				
		}
			
		// we must have only 1 object
		if (object.length == Numbers.N_1) {
			// sets gdg path
			setDDName(object[ELEMENT_1].toString());
		} else {
			throw new ParseException(AntUtilMessage.JEMZ004E.toMessage().getFormattedMessage(COMMAND_KEYWORD, commandLine), ELEMENT_1);
		}
	}



	/**
	 * Returns if it uses root properties file as master
	 * 
	 * @return the isByRoot
	 */
	public boolean isByRoot() {
		return isByRoot;
	}



	/**
	 * Sets if it uses root properties file as master
	 * 
	 * @param isByRoot the isByRoot to set
	 */
	public void setByRoot(boolean isByRoot) {
		this.isByRoot = isByRoot;
	}



	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.tasks.utilities.gdg.Command#execute()
	 */
	@Override
	public void perform(File file) throws IOException {
		// gets root
		Root root = (isByRoot()) ? new Root(file) : GDGUtil.createGDGEmptyRoot(file);
		// gets all properties of root, sorts keys by a list
		Properties properties = root.getProperties();
		@SuppressWarnings("unchecked")
		List<String> keysList = (ArrayList<String>) Collections.list(properties.propertyNames());
		Collections.sort(keysList);
		
		// scans all keys
		for (String key : keysList){
			// ignores key for last generation
			if (!key.equalsIgnoreCase(Root.LAST_GENERATION_PROPERTY)){
				// gets filename and creates a file object for key
				String fileName = properties.getProperty(key);
				File genFile = new File(file, fileName);
				// if file doesn't exists
				if (!genFile.exists()){
					// if master is root, creates the missing file, empty!
					// So that the root properties is already OK!
					if (isByRoot()){
						boolean isCreated = genFile.createNewFile();
						if (isCreated){
							LogAppl.getInstance().emit(AntUtilMessage.JEMZ005I, genFile.getAbsolutePath());
						} else {
							LogAppl.getInstance().emit(AntUtilMessage.JEMZ040E, genFile.getAbsolutePath());
							throw new IOException(AntUtilMessage.JEMZ040E.toMessage().getFormattedMessage(genFile.getAbsolutePath()));
						}
					} else {
						// if master are generations, remove the key (and value) from properties
						// because the file doesn't exist
						root.getProperties().remove(key);
						LogAppl.getInstance().emit(AntUtilMessage.JEMZ006I, key);
					}
				}
			}
		}
		// now scans all files of GDG (the generations)
		File[] files = file.listFiles();
		for (int i=0; i<files.length; i++){
			// gets the file name and if is not root properties checks the consistency
			String fileName = files[i].getName();
			if (!fileName.equalsIgnoreCase(Root.ROOT_FILE_NAME)){
				
				// the filenames are always 5 chars equals to generation (a number as #####)
				String key = StringUtils.left(fileName, GDGUtil.GDG_VERSION_DIGITS);
				if (isByRoot()){
					// if master is root ...
					if (root.getProperties().containsKey(key)){
						// and key is inside of properties
						// it could do nothing but it replaces the key with filename (probably is the same)
						root.getProperties().setProperty(key, fileName);
						LogAppl.getInstance().emit(AntUtilMessage.JEMZ007I, fileName, key);
					} else {
						// and key is not in properties
						// means that file system is wrong and than delete it
						if (!files[i].delete()){
							LogAppl.getInstance().emit(AntUtilMessage.JEMZ002W, fileName);
						} else {
							LogAppl.getInstance().emit(AntUtilMessage.JEMZ003I, fileName);
						}
					}
				} else {
					// if master is file system, set properties, overriding possible key and value already in properties
					root.getProperties().setProperty(key, fileName);
					LogAppl.getInstance().emit(AntUtilMessage.JEMZ007I, fileName, key);
				}
			}
		}
		
		// now properties is OK and equals to file system
		// calculate the last version
		int generation0 = 0;
		// scans properties keys
		for (Object keyObject : root.getProperties().keySet()){
			String key = keyObject.toString();
			// ignores last generation property
			if (!key.equalsIgnoreCase(Root.LAST_GENERATION_PROPERTY)){
				// calculate the max value
				generation0 = Math.max(generation0, Parser.parseInt(key));
			}
		}
		// sets the last generation
		root.getProperties().setProperty(Root.LAST_GENERATION_PROPERTY, String.valueOf(generation0));
		LogAppl.getInstance().emit(AntUtilMessage.JEMZ008I, COMMAND_KEYWORD, file, generation0);
		// stores root properties
		root.commit();
		// logs for creation
		LogAppl.getInstance().emit(AntUtilMessage.JEMZ001I, COMMAND_KEYWORD, file, file.getAbsolutePath());
	}


}