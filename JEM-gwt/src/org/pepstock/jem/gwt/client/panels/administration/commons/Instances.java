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
package org.pepstock.jem.gwt.client.panels.administration.commons;

import java.util.Collection;

import org.pepstock.jem.node.stats.LightSample;

/**
 * @author Andrea "Stock" Stocchero
 *
 */
public class Instances {
	
	/**
	 * Collection instance of samples
	 */
	private static Collection<LightSample> SAMPLES = null;

	/**
	 * Last sample
	 */
	private static LightSample LAST_SAMPLE = null;

	/**
	 * current sample
	 */
	private static LightSample CURRENT_SAMPLE = null;

	/**
	 * To avoid any instantiation
	 */
	private Instances(){
		
	}

	/**
	 * @param samples the sAMPLES to set
	 */
	public static void setSamples(Collection<LightSample> samples) {
		SAMPLES = samples;
	}

	/**
	 * @param lastSample the lAST_SAMPLE to set
	 */
	public static void setLastSample(LightSample lastSample) {
		LAST_SAMPLE = lastSample;
	}

	/**
	 * @param currentSample the cURRENT_SAMPLE to set
	 */
	public static void setCurrentSample(LightSample currentSample) {
		CURRENT_SAMPLE = currentSample;
	}

	/**
	 * @return the sAMPLES
	 */
	public static Collection<LightSample> getSamples() {
		return SAMPLES;
	}

	/**
	 * @return the lAST_SAMPLE
	 */
	public static LightSample getLastSample() {
		return LAST_SAMPLE;
	}

	/**
	 * @return the cURRENT_SAMPLE
	 */
	public static LightSample getCurrentSample() {
		return CURRENT_SAMPLE;
	}
	
	

}