/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2014   Andrea "Stock" Stocchero
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
package org.pepstock.jem.ant.tasks.managers;

import java.util.LinkedList;
import java.util.List;

import org.pepstock.jem.ant.DataDescriptionStep;


/**
 * Singleton that contains all steps definitions of ANT JCL.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class StepsContainer {
	
	private static final StepsContainer INSTANCE = new StepsContainer();
	
	private List<DataDescriptionStep> dataDescriptionSteps = new LinkedList<DataDescriptionStep>();

	/**
	 * Singleton, emtpy constructor
	 */
	private StepsContainer() {
		
	}
	
	/**
	 * Returns the singleton instance 
	 * @return singleton instance
	 */
	public static synchronized StepsContainer getInstance(){
		return INSTANCE;
	}

	/**
	 * @return the dataDescriptions
	 */
	public List<DataDescriptionStep> getDataDescriptionSteps() {
		return dataDescriptionSteps;
	}
}