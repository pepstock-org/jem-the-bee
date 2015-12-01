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

import org.apache.tools.ant.BuildException;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.ant.tasks.DataDescription;
import org.pepstock.jem.ant.tasks.StepJava;

/**
 * Is an abstract ANT task class that checks INPUT and OUTPUT data descriptions.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public abstract class AbstractIOTask extends StepJava {

	/**
	 * Data description name for files in INPUT
	 */
	public static final String INPUT_DATA_DESCRIPTION_NAME = "INPUT";
	
	/**
	 * Data description name for files in OUTPUT
	 */
	public static final String OUTPUT_DATA_DESCRIPTION_NAME = "OUTPUT";

	/**
	 * Empty constructor
	 */
	public AbstractIOTask() {
	}

	/**
	 * Sets itself as main program and calls <code>execute</code> method of
	 * superclass (StepJava).<br>
	 * Checks the necessary data descriptions are defined otherwise an exception will occur
	 * 
	 * @throws BuildException occurs if an error occurs
	 */
	@Override
	public void execute() throws BuildException {
		// checks before execution if has INPUT and OUTPUT
		// data description
		boolean input = false;
		boolean output = false;
		for (DataDescription dd : super.getDataDescriptions()){
			if (dd.getName().equalsIgnoreCase(INPUT_DATA_DESCRIPTION_NAME)){
				input = true;
			}
			if (dd.getName().equalsIgnoreCase(OUTPUT_DATA_DESCRIPTION_NAME)){
				output = true;
			}
		}
		
		if (!input){
			throw new BuildException(AntMessage.JEMA018E.toMessage().getFormattedMessage(INPUT_DATA_DESCRIPTION_NAME));
		}
		if (!output){
			throw new BuildException(AntMessage.JEMA018E.toMessage().getFormattedMessage(OUTPUT_DATA_DESCRIPTION_NAME));
		}
		
		super.setClassname(getClass().getName());
		super.execute();
	}
}