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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pepstock.jem.ant.tasks.Procedure;
import org.pepstock.jem.ant.tasks.ProcedureDefinition;


/**
 * Singleton that contains all PROCEDURE defined inside of ANT JCL.<br>
 * It contains both definition and instances.<br>
 * PROCEDURE is new task which can define a task, container of others.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class ProceduresContainer {
	
	private static final ProceduresContainer INSTANCE = new ProceduresContainer();
	
	private final Map<String, ProcedureDefinition> proceduresDefinitions = new HashMap<String, ProcedureDefinition>();
	
	private final List<Procedure> procedures = new ArrayList<Procedure>();

	/**
	 * Singleton, empty constructor
	 */
	private ProceduresContainer() {
	}
	
	/**
	 * Returns the singleton instance
	 * @return singleton instance
	 */
	public static synchronized ProceduresContainer getInstance(){
		return INSTANCE;
	}

	/**
	 * Returns all procedure definitions 
	 * @return the proceduresDefinitions
	 */
	public Map<String, ProcedureDefinition> getProceduresDefinitions() {
		return proceduresDefinitions;
	}

	/**
	 *  Returns all procedures instances 
	 * @return the procedures
	 */
	public List<Procedure> getProcedures() {
		return procedures;
	}

}