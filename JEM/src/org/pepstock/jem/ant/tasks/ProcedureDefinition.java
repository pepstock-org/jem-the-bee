/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013  Andrea "Stock" Stocchero
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
package org.pepstock.jem.ant.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.taskdefs.PreSetDef;
import org.pepstock.jem.ant.AntMessage;

/**
 * The procedure definition task generates a new definition
 * based on a current definition with some attributes or
 * elements preset.
 */
public class ProcedureDefinition extends PreSetDef{
	
	private UnknownElement nestedTask;
    
    private String name;
    
    private int order = 0;

    /**
     * Set the name of this definition.
     * @param name the name of the definition.
     */
     public void setName(String name) {
        this.name = name;
        super.setName(name);
    }

    /**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
     * Add a nested task to predefine attributes and elements on.
     * @param nestedTask  Nested task/type to extend.
     */
	@Override
    public void addTask(Task nestedTask) {
        if (this.nestedTask != null) {
            throw new BuildException(AntMessage.JEMA025E.toMessage().getFormattedMessage());
        }
        if (!(nestedTask instanceof UnknownElement)) {
            throw new BuildException(AntMessage.JEMA026E.toMessage().getFormattedMessage(nestedTask.getClass().getName()));
        }
        this.nestedTask = (UnknownElement) nestedTask;
        super.addTask(getNestedTask());
    }


    /**
	 * @return the nestedTask
	 */
	public UnknownElement getNestedTask() {
		return nestedTask;
	}

}