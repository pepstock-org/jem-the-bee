/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.pepstock.jem.node.Main;
import org.pepstock.jem.util.CharSet;

/**
 * Is a loader of affinity and uses a javascript code to load simply all affinities for the node.<br>
 * The policy file in JS must be passed in the properties in <code>init</code> method.<br>
 * Prepares a global constant for the JS, named <code>SYSINFO</code>. Is a instance of SystemInfo java class.<br>
 * Prepares a global variable, to use inside of JS, named <code>RESULT</code>. Is a instance of Result java class.<br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class JSPolicyAffinityLoader extends PolicyAffinityLoader {
	
	/**
	 * Script type
	 */
	public static final String TYPE = "javascript";
	
	private static final String JS_RESULT_VARIABLE = "RESULT";
	
	private static final String JS_SYSINFO_VARIABLE = "SYSINFO";
	
	private static final String JS_DOMAIN_VARIABLE = "DOMAIN";
	
	private static final String JS_ENVIRONMENT_VARIABLE = "ENVIRONMENT";

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.affinity.ScriptAffinityLoader#runScript(java.io.File, org.pepstock.jem.node.affinity.SystemInfo)
	 */
	@Override
	public Result runScript(File script, SystemInfo info) throws IOException {
		// reader of JS file
		return runScript(new InputStreamReader(new FileInputStream(script), CharSet.DEFAULT), info);
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
	public Result testScript(String script, SystemInfo info) throws IOException  {
		return runScript(new StringReader(script), info);
	}
	
	/**
	 * 
	 * @param reader
	 * @param info
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	private Result runScript(Reader reader, SystemInfo info) throws IOException {		
		// creates JS context
		Result result = new Result();
		result.setMemory(Main.EXECUTION_ENVIRONMENT.getMemory());
		result.setParallelJobs(Main.EXECUTION_ENVIRONMENT.getParallelJobs());
		Context jsContext = Context.enter();

		try {
			//inits JS context
            ScriptableObject scope = jsContext.initStandardObjects();

            // Collect the arguments into a single string.
            String s = "";

            // Set up "SYSINFO" in the global scope to contain Sysinfo java object
            jsContext.newObject(scope);
            scope.defineProperty(JS_SYSINFO_VARIABLE, info, ScriptableObject.READONLY);
            scope.defineProperty(JS_DOMAIN_VARIABLE, Main.EXECUTION_ENVIRONMENT.getDomain(), ScriptableObject.READONLY);
            scope.defineProperty(JS_ENVIRONMENT_VARIABLE, Main.EXECUTION_ENVIRONMENT.getEnvironment(), ScriptableObject.READONLY);
            scope.defineProperty(JS_RESULT_VARIABLE, result, ScriptableObject.PERMANENT);
            
            // Now evaluate the string we've collected. We'll ignore the result.
            jsContext.evaluateReader(scope, reader, s, 1, null);
            
        } finally {
        	// cleanup of JS context
            Context.exit();
        }
		return result;
	}

}