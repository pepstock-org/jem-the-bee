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
package org.pepstock.jem.node.resources.definition;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.pepstock.jem.log.LogAppl;

/**
 * Is a Javascript engine wrapper necessary to test the regular expression, defined in XML template.
 * Inside that template, you must use JAVASCRIPT REGEX   
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class JSRegExEvaluator {
	
	private static final JSRegExEvaluator INSTANCE = new JSRegExEvaluator();
	
	// JS function to use
	private static final String JS_REGEX_TEST = "function testRegEx(regex, text) {var re = new RegExp(regex); return re.exec(text);}";
	
	private Invocable invocableEngine = null;
	
	private boolean ready = true;

	/**
	 * Creates the javascript engine
	 */
	private JSRegExEvaluator() {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		invocableEngine = (Invocable) engine;
		try {
			// initialize function to call
			engine.eval(JS_REGEX_TEST);
		} catch (ScriptException e) {
			// if we have an exception, is not ready 
			LogAppl.getInstance().ignore(e.getMessage(), e);
			ready = false;
		}
	}
	
	/**
	 * Singleton method to get instance
	 * @return JS evaluator instance 
	 */
	public static JSRegExEvaluator getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Tests regular expression and a value, using JS
	 * @param regEx regular expression
	 * @param value value string to check
	 * @return <code>true</code> if match otherwise <code>false</code>
	 */
	public synchronized boolean test(String regEx, String value){
		// checks if ready
		// otherwise is always FALSE
		if (!ready){
			return false;
		}
		try {
			// calls JS function 
			Object object = invocableEngine.invokeFunction("testRegEx", regEx, value);
			// if have got a result, match
			return object != null;
		} catch (Exception e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			// if there is an exception, returns false
			return false;
		}
	}

}
