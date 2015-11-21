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
package org.pepstock.jem.ant;

import java.util.Properties;

import org.pepstock.jem.ant.tasks.utilities.scripts.GenericShellScriptTask;
import org.pepstock.jem.log.JemException;

/** 
 * It's generic JCL factory which is able to manage whatever JCL, configured by properties in the JEM env configuration file.<br>
 * <br>
 * If the usage of specific chars is mandatory for command to be execute, this is how to define them following the XML rules:<br>
 * <br>
 * <code>
 * ampersand (&) is escaped to &amp;
 * double quotes (") are escaped to &quot;
 * single quotes (') are escaped to &apos;
 * less than (<) is escaped to . &lt;
 * greater than (>) is escaped to . &gt;
 * </code>
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public class GenericScriptFactory extends ScriptFactory<GenericShellScriptTask> {

	private static final long serialVersionUID = 1L;

	/**
	 * public key which indicates the JCL type 
	 */
	public static final String GENERIC_JCL_TYPE_PROPERTY = "jem.generic.jcl.type";

	/**
	 * public key which indicates the JCL type description
	 */
	public static final String GENERIC_JCL_TYPE_DESCRIPTION_PROPERTY = "jem.generic.jcl.type.description";

	/**
	 * public key which indicates the JCL type comment chars to use during parsing
	 */
	public static final String GENERIC_JCL_COMMENT_PROPERTY = "jem.generic.jcl.comment";
	
	/**
	 * public key which indicates the JCL tag to begin and end of comment area to parse
	 */
	public static final String GENERIC_JCL_TAG_PROPERTY = "jem.generic.jcl.tag";
	
	/**
	 * public key which indicates the JCL mode, the mime type of script to render correctly on UI
	 */
	public static final String GENERIC_JCL_MODE_PROPERTY = "jem.generic.jcl.mode";
	
	/**
	 * public key which indicates the command line to be executed
	 */
	public static final String GENERIC_COMMAND_PROPERTY = "jem.generic.command";
	
	// default mime type of script
	private static final String DEFAULT_MODE = "batchfile";
	
	private String comment = null;
	
	private String beginTag = null;
	
	private String endTag = null;
	
	private String mode = DEFAULT_MODE;

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.AntFactory#init(java.util.Properties)
	 */
	@Override
	public void init(Properties properties) throws JemException {
		// reads all properties
		super.init(properties);
		
		// checks if type is present. It's mandatory only if is not already set
		if (super.getType() == null){
			if (!getProperties().containsKey(GENERIC_JCL_TYPE_PROPERTY) || "".equalsIgnoreCase(getProperties().getProperty(GENERIC_JCL_TYPE_PROPERTY))){
				throw new JemException(GENERIC_JCL_TYPE_PROPERTY+" is missing!");
			} else {
				super.setType(getProperties().getProperty(GENERIC_JCL_TYPE_PROPERTY));
			}
		}
		// checks if type description is present. It's mandatory only if not set by config
		if (super.getTypeDescription() == null){
			if (!getProperties().containsKey(GENERIC_JCL_TYPE_DESCRIPTION_PROPERTY) || "".equalsIgnoreCase(getProperties().getProperty(GENERIC_JCL_TYPE_DESCRIPTION_PROPERTY))){
				throw new JemException(GENERIC_JCL_TYPE_DESCRIPTION_PROPERTY+" is missing!");
			} else {
				super.setTypeDescription(getProperties().getProperty(GENERIC_JCL_TYPE_DESCRIPTION_PROPERTY));
			}
		}
		// checks if comment chars are present. they are mandatory
		if (!getProperties().containsKey(GENERIC_JCL_COMMENT_PROPERTY) || "".equalsIgnoreCase(getProperties().getProperty(GENERIC_JCL_COMMENT_PROPERTY))){
			throw new JemException(GENERIC_JCL_COMMENT_PROPERTY+" is missing!");
		} else {
			comment = getProperties().getProperty(GENERIC_JCL_COMMENT_PROPERTY);
		}
		// checks if full command is present. It's mandatory
		if (!getProperties().containsKey(GENERIC_COMMAND_PROPERTY)|| "".equalsIgnoreCase(getProperties().getProperty(GENERIC_COMMAND_PROPERTY))){
			throw new JemException(GENERIC_COMMAND_PROPERTY+" is missing!");
		} 

		// checks if the tag property is set
		if (getProperties().containsKey(GENERIC_JCL_TAG_PROPERTY)){
			// get the tag for begin and end to read inside the comments
			String tag = getProperties().getProperty(GENERIC_JCL_TAG_PROPERTY);
			beginTag = "<"+tag.toUpperCase()+">";
			endTag = "</"+tag.toUpperCase()+">";
		} else {
			// default is JEM-[JCL type]
			beginTag = "<JEM-"+getType().toUpperCase()+">";
			endTag = "</JEM-"+getType().toUpperCase()+">";
		}
	
		// checks if mode is present otherwise it uses the default
		if (getProperties().containsKey(GENERIC_JCL_MODE_PROPERTY)){
			mode = getProperties().getProperty(GENERIC_JCL_MODE_PROPERTY);
		}
	}

	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.ScriptFactory#getMode()
	 */
	@Override
	public String getMode() {
		return mode;
	}


	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.ScriptFactory#getBeginElement()
	 */
	@Override
	public String getBeginElement() {
		return beginTag;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.ScriptFactory#getEndElement()
	 */
	@Override
	public String getEndElement() {
		return endTag;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.ScriptFactory#getCommentCharSequence()
	 */
	@Override
	public String getCommentCharSequence() {
		return comment;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.ant.ScriptFactory#getAntTask()
	 */
	@Override
	public Class<GenericShellScriptTask> getAntTask() {
		return GenericShellScriptTask.class;
	}

}
