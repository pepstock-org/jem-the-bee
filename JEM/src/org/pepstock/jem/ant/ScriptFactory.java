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
package org.pepstock.jem.ant;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.Jcl;
import org.pepstock.jem.factories.JclFactoryException;

/**
 * Is a JCL factory which enables to submit a script directly, withou having any ANT file.<br>
 * It creates a ANT file at runtime, adding the content of script inside ANT file.<br/>
 * The elements (begin and end ones) MUST alone on comment line.<br/>
 * <br/>
 * Example:<br/>
 * <pre>
 * # <JEM-xxx>
 * # key=value
 * # </JEM-xxx>
 * </pre>
 *  
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public abstract class ScriptFactory extends AntFactory {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Returns the mime type of script language
	 * @return the mime type of script language
	 */
	public abstract String getMode();
	
	/**
	 * TAG element to put on comments of script which defines the start point of JEM meta data.
	 * @return TAG element to put on comments of script which defines the start point of JEM meta data
	 */
	public abstract String getBeginElement();

	/**
	 * TAG element to put on comments of script which defines the end point of JEM meta data.
	 * @return TAG element to put on comments of script which defines the end point of JEM meta data
	 */
	public abstract String getEndElement();

	/**
	 * Char sequence defined in script language as comment
	 * @return Char sequence defined in script language as comment
	 */
	public abstract String getCommentCharSequence();
	
	/**
	 * Returns the ANT task to use to execute the script.
	 * @return the ANT task to use to execute the script
	 */
	public abstract Class<?> getAntTask();

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.AntFactory#createJcl(java.lang.String)
	 */
	@Override
	public final Jcl createJcl(String content) throws JclFactoryException {
		StringBuilder result = null;
		try {
			// reads script extracting the JEM properties
			Properties jemProperties = getProperties(content);
			// creates ANT file 
			result = getAntJcl(content, jemProperties);
		} catch (Exception e) {
			throw new JclFactoryException(e.getMessage(), e);
		}
		Jcl jcl;
		try {
			// used ANTJcl to create the JCL 
			jcl = super.createJcl(result.toString());
		} catch (JclFactoryException e) {
			// At this time type=ANT and it's not correct
			// so overrides teh new type
			e.getJcl().setType(getType());
			// sets JCL mode
			e.getJcl().setMode(getMode());
			throw e;
		}
		// overrides the content JCL, putting ANT language
		jcl.setContentToBeExecuted(result.toString());
		// sets the original script as content
		jcl.setContent(content);
		// sets JCL mode
		jcl.setMode(getMode());
		return jcl;
	}

	/**
	 * Reads the script extracting the meta data for JEM.
	 * 
	 * @param content script content
	 * @return a lit of properties with JEM properties
	 * @throws Exception if any error occurs
	 */
	private Properties getProperties(String content) throws JclFactoryException, IOException{
		Properties jemProperties = new Properties();
		// flag to check if it's inside of meta data reading
		boolean isInJemConfig = false;
		// if it's able to parse this script
		// It's difficult to validate a script. All is based on different 
		// tags used in the comments
		boolean isScript = false;
		
		StringBuilder propertiesStrings = new StringBuilder();
		// reads script
		StringReader contentReader = new StringReader(content);
		List<String> lines = IOUtils.readLines(contentReader);
		// scans lines
		for (String line : lines){
			// if is a comment
			if (StringUtils.startsWithIgnoreCase(line, getCommentCharSequence())){
				// removes the first part and trim spaces
				String postComment = StringUtils.stripStart(line, getCommentCharSequence()).trim();

				// checks if is the begin element
				if (getBeginElement().equalsIgnoreCase(postComment)){
					// if flag is true, means the begin element is written twice
					if (isInJemConfig){
						throw new JclFactoryException(AntMessage.JEMA070E.toMessage().getFormattedMessage(getBeginElement()));
					}
					// sets flags
					isInJemConfig = true;
					isScript = true;
				} else if (getEndElement().equalsIgnoreCase(postComment)){
					// checks if is the end element
					// if the flag is false, means that there isn't begin element
					if (!isInJemConfig){
						throw new JclFactoryException(AntMessage.JEMA071E.toMessage().getFormattedMessage(getBeginElement()));
					}
					// sets false and break
					isInJemConfig = false;
					break;
				} else if (isInJemConfig){
					//reads properties if flag is true
					propertiesStrings.append(postComment).append(System.getProperty("line.separator"));
				}
			} else if (isInJemConfig) {
				// if we are here. that means the comment line stops before closing the metadata
				throw new JclFactoryException(AntMessage.JEMA071E.toMessage().getFormattedMessage(getEndElement()));
			}
		}
		// if we are here, that means it gets the end before closing the metadata
		if (isInJemConfig) {
			throw new JclFactoryException(AntMessage.JEMA071E.toMessage().getFormattedMessage(getEndElement()));
		}
		// if we are here, there isn't the begin element and then the script is not of the type of the factory
		if (!isScript){
			throw new JclFactoryException(AntMessage.JEMA071E.toMessage().getFormattedMessage(getBeginElement()));
		}
		
		// loads properties
		StringReader reader = new StringReader(propertiesStrings.toString());

		jemProperties.load(reader);
		return jemProperties;
	}
	
	/**
	 * Creates a ANT file to execute teh script
	 * @param content script content
	 * @param jemProperties all JEM properties read from script comment
	 * @return a string with ANT file
	 */
	private StringBuilder getAntJcl(String content, Properties jemProperties){
		StringBuilder result = new StringBuilder();
		
	    result.append("<?xml version=\"1.0\"?>");
	    result.append("<project default=\"exec\" basedir=\".\">");
	    // loads all jem properties
	    for (Object key : jemProperties.keySet()){
	    	String value = jemProperties.getProperty(key.toString());
	    	result.append("<property name=\""+key.toString()+"\" value=\""+value+"\"/>");
	    }
	    // sets teh ANT task which will execute the script
	    result.append("<taskdef name=\"script\" classname=\""+getAntTask().getName()+"\" />");
	    result.append("<target name=\"exec\">");
	    // writes all script
	    result.append("<script><![CDATA[");
		result.append(content);
	    result.append("]]></script>");
	    result.append("</target>");
	    result.append("</project>");
		
	    return result;
	}

}
