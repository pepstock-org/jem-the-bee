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
    
 Linking JEM, the BEE statically or dynamically with other modules is making a combined work based on JEM, the BEE. 
 Thus, the terms and conditions of the GNU General Public License cover the whole combination.

 As a special exception, the copyright holders of JEM, the BEE give you permission to combine JEM, the BEE program with 
 free software programs or libraries that are released under the GNU LGPL and with independent modules 
 that communicate with JEM, the BEE solely through the org.pepstock.jem.node.executor.DefaultExecutor interface. 
 You may copy and distribute such a system following the terms of the GNU GPL for JEM, the BEE and the licenses 
 of the other code concerned, provided that you include the source code of that other code when and as 
 the GNU GPL requires distribution of source code and provided that you do not modify the 
 org.pepstock.jem.node.executor.DefaultExecutor interface.

 Note that people who make modified versions of JEM, the BEE are not obligated to grant this special exception
 for their modified versions; it is their choice whether to do so. The GNU General Public License
 gives permission to release a modified version without this exception; this exception also makes it
 possible to release a modified version which carries forward this exception. If you modify the 
 org.pepstock.jem.node.executor.DefaultExecutor interface, this exception does not apply to your modified version of 
 JEM, the BEE, and you must remove this exception when you distribute your modified version.

 This exception is an additional permission under section 7 of the GNU General Public License, version 3
 (GPLv3)      
*/
package org.pepstock.jem.node.executors;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.concurrent.Callable;

import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.NodeMessage;

/**
 * Default of executors with commons methods.
 * 
 * @author Andrea "Stock" Stocchero
 * @param <V>
 * 
 */
public abstract class DefaultExecutor<V> implements Callable<V>, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Callable implementation method. It mustn't be called 
	 * because the extensions must implement execute method.<br>
	 * This is done to avoid serialization errors which happen
	 * when the exception is not serializable and it must (for Hazelcast)   
	 * 
	 * @return object to return
	 * @throws if any error occurs. It creates always a new Exception to be sure that it's serializable
	 */
	
	public final V call() throws SerializableException{
		// checks if is shutting down
		try {
			checkShutDown();
			return execute();
		} catch (ExecutorException e) {
			// debug
			LogAppl.getInstance().debug(e.getMessage(), e);

			// saves complete stacktrace in string format
			// to serialize
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			// catches this exception because 
			// it couldn't be serializable, creating
			// Hazelcast exception
			SerializableException se = new SerializableException(e.getMessage(), sw.toString());
			pw.close();
			try {
				sw.close();
			} catch (IOException e1) {
				LogAppl.getInstance().ignore(e1.getMessage(), e1);
			}
			throw se;
		}
	}

	/**
	 * Logic must be implemented here
	 * @return object to return 
	 * @throws ExecutorException if any error occurs
	 */
	public abstract V execute() throws ExecutorException;
	
	/**
	 * Checks if node is shutting down. If yes, throws an exception.
	 * @throws ExecutorException if node is shutting down
	 */
	public void checkShutDown() throws ExecutorException{
		// if shutting down, throw an exception
		if (Main.IS_SHUTTING_DOWN.get()){
			throw new ExecutorException(NodeMessage.JEMC115E, Main.getNode().toString());
		}
	}
}