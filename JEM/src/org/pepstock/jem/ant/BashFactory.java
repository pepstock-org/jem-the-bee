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

import org.pepstock.jem.ant.tasks.utilities.ShellScriptTask;
import org.pepstock.jem.ant.tasks.utilities.scripts.BashScriptTask;

/**
 * Is a JCL factory which enables to submit BASH script file as JCL (wrapped in an ANT file).
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class BashFactory extends ScriptFactory {

	private static final long serialVersionUID = 1L;
	
	/**
	 * public key which indicates the JCL type for ANT
	 */
	public static final String BASH_TYPE = "bash";
	
	private static final String BASH_TYPE_DESCRIPTION = "Bash script";
	
	private static final String BASH_COMMENT = "#";
	
	private static final String BASH_BEGIN_TAG = "<JEM-BASH>"; 

	private static final String BASH_END_TAG = "</JEM-BASH>";
	
	private static final String BASH_MODE = "sh";

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.ScriptFactory#getCommentCharSequence()
	 */
	@Override
	public String getCommentCharSequence() {
		return BASH_COMMENT;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.ScriptFactory#getAntTask()
	 */
	@Override
	public Class<? extends ShellScriptTask> getAntTask() {
		return BashScriptTask.class;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.AntFactory#getType()
	 */
	@Override
	public String getType() {
		return BASH_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.AntFactory#getTypeDescription()
	 */
	@Override
	public String getTypeDescription() {
		return BASH_TYPE_DESCRIPTION;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.ScriptFactory#getBeginElement()
	 */
	@Override
	public String getBeginElement() {
		return BASH_BEGIN_TAG;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.ScriptFactory#getEndElement()
	 */
	@Override
	public String getEndElement() {
		return BASH_END_TAG;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.ScriptFactory#getMode()
	 */
	@Override
	public String getMode() {
		return BASH_MODE;
	}

}
