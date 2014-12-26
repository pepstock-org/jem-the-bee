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
package org.pepstock.jem.node.affinity;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.node.Queues;

import com.hazelcast.core.ILock;

/**
 * Is a loader of affinity and uses a groovy code to load simply all affinities for the node.<br>
 * The policy file in GROOVY must be passed in the properties in <code>init</code> method.<br>
 * Prepares a global constant for the JS, named <code>SYSINFO</code>. Is a instance of SystemInfo java class.<br>
 * Prepares a global variable, to use inside of JS, named <code>RESULT</code>. Is a instance of Result java class.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class GroovyPolicyAffinityLoader extends PolicyAffinityLoader {
	
	/**
	 * Script type
	 */
	public static final String TYPE = "groovy";
	
	private static final String RESULT_VARIABLE = "RESULT";
	
	private static final String SYSINFO_VARIABLE = "SYSINFO";
	
	private static final String DOMAIN_VARIABLE = "DOMAIN";
	
	private static final String ENVIRONMENT_VARIABLE = "ENVIRONMENT";

	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.affinity.ScriptAffinityLoader#runScript(java.io.File, org.pepstock.jem.node.affinity.SystemInfo)
	 */
	@Override
	public Result runScript(File script, SystemInfo info) throws IOException {
		Result result = new Result();
		// sets the info of environment
		result.setMemory(Main.EXECUTION_ENVIRONMENT.getMemory());
		result.setParallelJobs(Main.EXECUTION_ENVIRONMENT.getParallelJobs());

		// call groovy expressions from Java code
		Binding binding = new Binding();
		// adds all instances as groovy constants and variable
		binding.setVariable(RESULT_VARIABLE, result);
		binding.setVariable(SYSINFO_VARIABLE, info);
		binding.setVariable(DOMAIN_VARIABLE, Main.EXECUTION_ENVIRONMENT.getDomain());
		binding.setVariable(ENVIRONMENT_VARIABLE, Main.EXECUTION_ENVIRONMENT.getEnvironment());
		GroovyShell shell = new GroovyShell(getClass().getClassLoader(), binding);

		// synchronizes the access to file
		ILock writeSynch = Main.getHazelcast().getLock(Queues.AFFINITY_LOADER_LOCK);
		// in this way, the execution of affinity script
		// can run synchronized inside of JEM environment
		// even because the script can be modify by user interface
		writeSynch.lock();
		try {
			// run the SCRIPT!!
			shell.run(script, new String[]{});
			return result;
		} finally {
			// unlock always
			writeSynch.unlock();
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.affinity.ScriptAffinityLoader#getScriptType()
	 */
	@Override
	public String getScriptType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.affinity.ScriptAffinityLoader#testScript(java.lang.String, org.pepstock.jem.node.affinity.SystemInfo)
	 */
	@Override
	public Result testScript(String script, SystemInfo info) throws IOException {
		// this method is called by the user interface
		// to test if the script works correctly after
		// an update
		File tmp = File.createTempFile("jem", "."+TYPE);
		FileUtils.writeStringToFile(tmp, script);
		// gets result for user interface
		Result result = runScript(tmp, info);
		if (!tmp.delete()){
			LogAppl.getInstance().debug("Unable to delete temporary file "+tmp+" for "+GroovyPolicyAffinityLoader.class);
		}
		return result;
	}
}
