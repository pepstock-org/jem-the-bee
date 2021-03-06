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
package org.pepstock.jem.ant;

import org.pepstock.jem.ant.tasks.utilities.scripts.PowerShellScriptTask;

/**
 * Is a JCL factory which enables to submit WINDOWS PowerShell script file as JCL (wrapped in an ANT file).
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public class PowerShellFactory extends ScriptFactory<PowerShellScriptTask> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * default JCL type for ANT power shell
	 */
	public static final String CMD_TYPE = "ps";
	
	/**
	 * default JCL description for ANT power shell
	 */
	public static final String CMD_TYPE_DESCRIPTION = "PowerShell script";
	
	private static final String CMD_COMMENT = "#";
	
	private static final String CMD_BEGIN_TAG = "<JEM-PS>"; 

	private static final String CMD_END_TAG = "</JEM-PS>";
	
	private static final String CMD_MODE = "batchfile";
	
	/**
	 * sets the default values of type and description
	 */
    public PowerShellFactory() {
	    super();
	    super.setType(CMD_TYPE);
	    super.setTypeDescription(CMD_TYPE_DESCRIPTION);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.ScriptFactory#getCommentCharSequence()
	 */
	@Override
	public String getCommentCharSequence() {
		return CMD_COMMENT;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.ScriptFactory#getAntTask()
	 */
	@Override
	public Class<PowerShellScriptTask> getAntTask() {
		return PowerShellScriptTask.class;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.ScriptFactory#getBeginElement()
	 */
	@Override
	public String getBeginElement() {
		return CMD_BEGIN_TAG;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.ScriptFactory#getEndElement()
	 */
	@Override
	public String getEndElement() {
		return CMD_END_TAG;
	}
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.ScriptFactory#getMode()
	 */
	@Override
	public String getMode() {
		return CMD_MODE;
	}

}
