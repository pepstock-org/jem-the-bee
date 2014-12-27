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
package org.pepstock.jem.rest.entities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import org.pepstock.jem.node.About;
import org.pepstock.jem.node.persistence.RedoStatement;
import org.pepstock.jem.node.stats.LightSample;

/**
 * POJO container of samples list.
 * <br>
 * It contains also list of redo statements, if there are, and "about" information on JEM.
 * <br>
 * Uses the annotation XmlRootElement to be serialized.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 *
 */
@XmlRootElement
public class Stats extends ReturnedObject implements Serializable{

    private static final long serialVersionUID = 1L;

	private Collection<LightSample> samples = null;
	
	private LightSample currentSample = null;
	
	private Collection<RedoStatement> redoStatements = null;
	
	private About about = null;
	
	private String[] infos = null;

	/**
	 * Empty constructor
	 */
	public Stats() {
	}

	/**
	 * @return the samples
	 */
	public Collection<LightSample> getSamples() {
		return samples;
	}

	/**
	 * @param samples the samples to set
	 */
	public void setSamples(Collection<LightSample> samples) {
		this.samples = samples;
	}

	/**
	 * @return the currentSample
	 */
	public LightSample getCurrentSample() {
		return currentSample;
	}

	/**
	 * @param currentSample the currentSample to set
	 */
	public void setCurrentSample(LightSample currentSample) {
		this.currentSample = currentSample;
	}

	/**
	 * @return the redoStatements
	 */
	public Collection<RedoStatement> getRedoStatements() {
		return redoStatements;
	}

	/**
	 * @param redoStatements the redoStatements to set
	 */
	public void setRedoStatements(Collection<RedoStatement> redoStatements) {
		this.redoStatements = redoStatements;
	}

	/**
	 * @return the about
	 */
	public About getAbout() {
		return about;
	}

	/**
	 * @param about the about to set
	 */
	public void setAbout(About about) {
		this.about = about;
	}

	/**
	 * @return the infos
	 */
	public String[] getInfos() {
		return infos;
	}

	/**
	 * @param infos the infos to set
	 */
	public void setInfos(String[] infos) {
		System.arraycopy(infos, 0, this.infos, 0, infos.length);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Stats [samples=" + samples + ", currentSample=" + currentSample + ", redoStatements=" + redoStatements + ", about=" + about + ", infos=" + Arrays.toString(infos) + "]";
	}
}