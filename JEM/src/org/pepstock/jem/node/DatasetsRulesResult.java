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
package org.pepstock.jem.node;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.pepstock.jem.node.sgm.PathsContainer;

/**
 * Contains the list of warnings and rules, result of loading of datapaths and datasets rules.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
public class DatasetsRulesResult implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<String> warnings = null;
	
	private Map<Pattern, PathsContainer> rules = null;

	/**
	 * Empty constructor
	 */
	public DatasetsRulesResult() {
	}

	/**
	 * @return the warnings
	 */
	public List<String> getWarnings() {
		return warnings;
	}

	/**
	 * @param warnings the warnings to set
	 */
	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}

	/**
	 * @return the rules
	 */
	public Map<Pattern, PathsContainer> getRules() {
		return rules;
	}

	/**
	 * @param rules the rules to set
	 */
	public void setRules(Map<Pattern, PathsContainer> rules) {
		this.rules = rules;
	}

}
