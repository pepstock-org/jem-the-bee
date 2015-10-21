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
import java.text.ParseException;

import org.pepstock.catalog.DataDescriptionImpl;
import org.pepstock.catalog.DataSetImpl;
import org.pepstock.jem.ant.tasks.utilities.SubCommand;
import org.pepstock.jem.log.JemException;

/**
 * Utility class to use to save command line during the syntax checking and execute the command
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 * 
 */
public abstract class Command implements SubCommand{
	
	static final int ELEMENT_1 = 0;

	static final int ELEMENT_2 = 1;

	static final int ELEMENT_3 = 2;
	
	private String commandLine = null;
	
	private String ddname = null;
	
	private DataDescriptionImpl dataDescriptionImpl = null;
	
	/**
	 * Stores command line
	 * 
	 * @param commandLine command line 
	 * @throws ParseException  if command line has a syntax error
	 */
	public Command(String commandLine) throws ParseException {
		this.setCommandLine(commandLine);
	}

	
	/**
	 * @return the commandLine
	 */
	public String getCommandLine() {
		return commandLine;
	}


	/**
	 * @param commandLine the commandLine to set
	 */
	public void setCommandLine(String commandLine) {
		this.commandLine = commandLine;
	}


	/**
	 * @return the path
	 */
	public String getDDName() {
		return ddname;
	}


	/**
	 * @param path the path to set
	 */
	public void setDDName(String path) {
		this.ddname = path;
	}


	/**
	 * @return the dataDescriptionImpl
	 */
	public DataDescriptionImpl getDataDescriptionImpl() {
		return dataDescriptionImpl;
	}


	/**
	 * @param dataDescriptionImpl the dataDescriptionImpl to set
	 */
	public void setDataDescriptionImpl(DataDescriptionImpl dataDescriptionImpl) {
		this.dataDescriptionImpl = dataDescriptionImpl;
	}

	/**
	 * Execute the command
	 * 
	 * @throws IOException if an error occurs
	 */
	@Override
	public void execute() throws JemException{
		try {
			for (DataSetImpl ds : dataDescriptionImpl.getDatasets()){
				perform(ds.getRealFile());
			}
		} catch (IOException e) {
			throw new JemException(e);
		}
	}
	
	/**
	 * Perform action on specific file
	 * @param file file to use
	 * @throws IOException if any exception occurs
	 */
	public abstract void perform(File file)  throws IOException;

}