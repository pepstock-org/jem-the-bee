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
package org.pepstock.jem.ant.tasks;

import java.io.Serializable;
import java.text.ParseException;

import org.apache.tools.ant.BuildException;
import org.pepstock.catalog.gdg.GDGUtil;
import org.pepstock.jem.ant.AntMessage;

/**
 * Represents the file that the task has to use during the execution. There are
 * 4 kinds of dataset.<br>
 * GDGs are versioned file which are addressable by a relative position starting
 * from last version.<br>
 * References are links to dataset previously defined in other tasks.<br>
 * Temporaries are files create in temp directory and deleted on exit of JVM.<br>
 * Inlines are data put inside JCL.<br>
 * Examples:<br>
 * <br>
 * <b>GDG</b>: <code>&lt;dataSet name="nas.rx.jemtest(-1)"/&gt;</code><br>
 * <b>Reference</b>:
 * <code>&lt;dataSet name="*.step1.step-java.FILEIN"/&gt;</code><br>
 * <b>Temporary</b>: <code>&lt;dataSet name="@@temp"/&gt;</code><br>
 * <b>Inline</b>: <code>&lt;dataSet&gt inline data ;&lt;dataSet&gt;</code><br>
 * <b>Resource</b>: <code>&lt;dataSet name="foo.txt" resource="ftp/resource"/&gt;</code><br>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0
 * 
 */
public class DataSet implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name = null;

	private int offset = Integer.MIN_VALUE;

	private StringBuilder textBuffer = new StringBuilder();

	private String datasource = null;
	
	private boolean replaceProperties = false;
	
	/**
	 * Empty constructor
	 */
	public DataSet() {
	}

	/**
	 * Returns the name of the file to use.
	 * 
	 * @return name of dataset
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the file to use.
	 * 
	 * @param name of dataset
	 */
	public void setName(String name) {
		// checks if the name is empty
		if (name.trim().length() == 0){
			throw new BuildException(AntMessage.JEMA041E.toMessage().getFormattedMessage());
		}

		try {
			// use GDG util to extract name and offset del gdg
			// if a exception occurs means that is not GDG
			// and then another kind of file
			Object[] objects = GDGUtil.isGDG(name);
			// is GDG! save the root name
			// array object at index 0 is name
			this.name = objects[0].toString();
			// array object at index 1 is offset
			// parse teh string into a number
			// BE AWARE that positive offset don't have to have the sign "+"
			// otherwise java don't consider it a number
			offset = Integer.parseInt(objects[1].toString());
		} catch (ParseException e) {
			// is not a GDG! save the name (as is) and
			// sets the offese to minimum value of a integer
			this.name = name;
			offset = Integer.MIN_VALUE;
		}
	}

	/**
	 * Returns the offset value. If dataset is a GDG this is the relative
	 * position to generation 0, otherwise returns Integer.MIN_VALUE
	 * 
	 * @return If dataset is a GDG this is the relative position to generation
	 *         0, otherwise returns Integer.MIN_VALUE
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Returns <code>true</code> if data description is defined for having a
	 * temporary file. Is a temporary file if name is starting with
	 * <code>@@</code>.
	 * 
	 * @return <code>true</code> is data description is defined for having a
	 *         temporary file
	 */
	public boolean isTemporary() {
		// checks name. is null, then is not a temporary
		if (name != null){
			// returns true if the first 2 chars are @@ and if is not Inline
			// then
			// none data text is included in data set
			return name.startsWith("@@") && !isInline();
		} else {
			return false;
		}
	}

	/**
	 * Returns <code>true</code> if data description is defined for having a
	 * referback to a dataset used and defined in a step previously configured.
	 * Is a reference file if name has the following format:<br>
	 * <br>
	 * <code>*.[target-name].[task-name].[data-description-name]</code>.
	 * 
	 * @return <code>true</code> is data description is defined for having a
	 *         temporary file
	 */
	public boolean isReference() {
		// checks name. is null, then is not a reference
		if (name != null){
			// returns true if the first 2 chars are "*." and if is not Inline
			// then
			// none data text is included in data set
			return name.startsWith("*.") && !isInline();
		} else {
			return false;
		}
	}

	
	
	/**
	 * @param textBuffer the textBuffer to set
	 */
	public void setTextBuffer(StringBuilder textBuffer) {
		this.textBuffer = textBuffer;
	}

	/**
	 * Calls by ANT engine during the JCL parsing. If ANT find a content text
	 * inside the <code>dataset</code>, adds the text appending in a string
	 * buffer. If it happens, the dataset is a Inline dataset, with data defined
	 * inside the JCL.
	 * 
	 * @param text content of dataset element
	 */
	public void addText(String text) {
		textBuffer.append(text);
	}

	/**
	 * Returns the text value.
	 * 
	 * @see DataSet#addText(String)
	 * @return the text value
	 */
	public StringBuilder getText() {
		return textBuffer;
	}

	/**
	 * Returns <code>true</code> if data description is defined for having data
	 * inside of dataset.
	 * 
	 * @see DataSet#addText(String)
	 * @return <code>true</code> if text has length greater than 0
	 */
	public boolean isInline() {
		return textBuffer.length() > 0;
	}

	/**
	 * Returns <code>true</code> if data description is defined for having a
	 * GDG. If offest attribute is not equals to Integer.MIN_VALUE, dataset is a
	 * GDGD.
	 * 
	 * @see DataSet#getOffset()
	 * @return <code>true</code> if data description is defined for having a GDG
	 */
	public boolean isGdg() {
		return offset != Integer.MIN_VALUE && !isDatasource();
	}

	/**
	 * Returns <code>true</code> if data set is defined to be linked to a common resource.
	 * 
	 * @return <code>true</code> if data set is defined to be linked to a common resource
	 */
	public boolean isDatasource() {
		return datasource != null;
	}

	/**
	 * Returns the common resources to use to have a IO stream 
	 * 
	 * @return the resource logical name
	 */
	public String getDatasource() {
		return datasource;
	}

	/**
	 * Sets the common resources to use to have a IO stream
	 * 
	 * @param resource the resource logical name to set
	 */
	public void setDatasource(String resource) {
		// checks if the resource is empty
		if (resource.trim().length() == 0){
			throw new BuildException(AntMessage.JEMA020E.toMessage().getFormattedMessage());
		}
		this.datasource = resource;
	}
	
	/**
	 * @return the replaceProperties
	 */
	public boolean isReplaceProperties() {
		return replaceProperties;
	}

	/**
	 * @param replaceProperties the replaceProperties to set
	 */
	public void setReplaceProperties(boolean replaceProperties) {
		this.replaceProperties = replaceProperties;
	}

	/**
	 * Returns the string representation of dataset.
	 * 
	 * @return text if INLINE, otherwise dataset name
	 */
	@Override
	public String toString() {
		// if inline, return the text value, otherwise the name
		if (isInline()) {
			return "content=" + textBuffer.toString();
		} else {
			return "dsn=" + name;
		}
	}
}