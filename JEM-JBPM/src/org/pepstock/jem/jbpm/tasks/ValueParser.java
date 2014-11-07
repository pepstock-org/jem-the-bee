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
package org.pepstock.jem.jbpm.tasks;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.pepstock.catalog.Disposition;
import org.pepstock.jem.jbpm.JBpmException;
import org.pepstock.jem.jbpm.JBpmMessage;
import org.pepstock.jem.log.LogAppl;

/**
 * Parser of all information, extracted from JBPM JCL, necessary to creates data description and data sources.<br>
 * This is the syntax to use for data description (all commands are not positionals):<br>
 * <ul>
 * <li> <code>DSN=<i>dsn</i>,DISP=[SHR|OLD|MOD|NEW]</code>
 * <li> <code>DSN=(<i>dsn1</i>;<i>dsn2</i>),DISP=SHR</code>
 * <li> <code>DSN=<i>dsn</i>,DISP=[SHR|OLD|MOD|NEW],DATASOURCE=<i>datasource</i></code>
 * </ul>
 * This is the syntax to use for data source (all commands are not positionals):<br>
 * <ul>
 * <li> <code>RESOURCE=<i>resourceName</i>,PROPERTIES=(<i>key1=value1</i>;<i>key2=value2</i>)</code>
 * </ul>
 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public final class ValueParser {
	
	private static final String SYSOUT = "SYSOUT";
	
	private static final String DSN_PREFIX = "DSN";

	private static final String DISP_PREFIX = "DISP";
	
	private static final String DATASOURCE_PREFIX = "DATASOURCE";
	
	private static final String RESOURCE_PREFIX = "RESOURCE";

	private static final String PROPERTIES_PREFIX = "PROPERTIES";
	
	private static final String COMMAND_SEPARATOR = ",";

	private static final String VALUE_SEPARATOR = ";";
	
	/**
	 * To avoid any instantiation
	 */
	private ValueParser() {
		
	}
	
	/**
	 * Parses the value of variables in JBPM and loads datasets into data descriptions
	 * @param dd data description to load
	 * @param valueParm value of assignment in BPMN2 language
	 * @throws JBpmException if any error occurs
	 */
	public static final void loadDataDescription(DataDescription dd, String valueParm) throws JBpmException{
		// trim value
		String value = valueParm.trim();
		
		// init of variables to load dd
		String[] dsn = null;
		String disposition = null;
		String dataSource = null;
		boolean isSysout = false; 
		// this boolean is set to true 
		// because if it's not able to parse the content,
		// the wjole string will be the content of dataset
		boolean isText = true;
		
		// parses using comma
		String[] stmtTokens = StringUtils.split(value, COMMAND_SEPARATOR);
		if (stmtTokens != null && stmtTokens.length > 0){
			// scans all tokes
			for (int i=0; i<stmtTokens.length; i++){
				String token = stmtTokens[i].trim();
				// is datasetname?
				if (StringUtils.startsWithIgnoreCase(token, DSN_PREFIX)){
					// gets all string after DSN and =
					String subToken = getValue(token, DSN_PREFIX);
					if (subToken != null){
						// sets that is not a text
						isText = false;
						
						String subValue = null;
						// checks if teh content is inside of brackets
						if (subToken.startsWith("(") && subToken.endsWith(")")){
							// gets content inside brackets
							subValue = StringUtils.removeEnd(StringUtils.removeStart(subToken, "("), ")");
						} else {
							// only 1 datasets
							subValue= subToken;
						}
						// parses values
						dsn = StringUtils.split(subValue, VALUE_SEPARATOR);
					} 
				} else if (StringUtils.startsWithIgnoreCase(token, DISP_PREFIX)){
					// sets that is not a text
					isText = false;
					// saves disposition
					disposition = getValue(token, DISP_PREFIX);
				} else if (StringUtils.startsWithIgnoreCase(token, DATASOURCE_PREFIX)){
					// sets that is not a text
					isText = false;
					// saves data sourceinfo
					dataSource = getValue(token, DATASOURCE_PREFIX);
				} else if (StringUtils.startsWithIgnoreCase(token, SYSOUT)){
					// sets that is not a text
					isText = false;
					// sets is a sysout
					isSysout = true;
				} 
			}
		}
		// content of file
		if (isText){
        	dd.setDisposition(Disposition.SHR);
        	DataSet ds = createDataSet(dd, null, valueParm);
        	dd.addDataSet(ds);
		} else {
			// disposition DISP= is mandatory, always
			if (disposition == null){
	        	throw new JBpmException(JBpmMessage.JEMM050E, dd.getName());
			}
			dd.setDisposition(disposition);
			
			// if sets SYSOUT but also DSN=, this is not allowed
			if (isSysout && dsn != null){
	        	throw new JBpmException(JBpmMessage.JEMM051E, dd.getName());
			}
			// sets sysout
			dd.setSysout(isSysout);
			// if not sysout, loads datasets
			// datasource can be set ONLY with 1 dataset
			if (!isSysout && dsn != null){
				if (dsn.length > 1 && dataSource != null){
					throw new JBpmException(JBpmMessage.JEMM052E, dd.getName());
				}
				// loads all datasets and set datasource
				for(int k=0; k<dsn.length; k++){
					DataSet ds = createDataSet(dd, dsn[k], null);
					dd.addDataSet(ds);
					// doesn't check anything because
					// already done before
					if (dataSource != null){
						ds.setDatasource(dataSource);
					}
				}
			}
		}
	}

	/**
	 * Creates all data set using the dsn or content values.
	 * @param dd data description object to be loaded of data sets. Passed only for the exception
	 * @param dsn data set name or null if is a INLINE data set
	 * @param content content of INLINE data set or null
	 * @return a data set instance
	 * @throws JBpmException if dsn and content are null occurs
	 */
	private static DataSet createDataSet(DataDescription dd, String dsn, String content) throws JBpmException{
		// if DSN = null and content is empty, exception
		if (dsn == null && content == null){
			throw new JBpmException(JBpmMessage.JEMM053E, dd.getName());
		}
		// creates dataset and loads it
		DataSet ds = new DataSet();
		if (content != null){
			ds.addText(content);
		} else {
			ds.setName(dsn);
		}
		return ds;
	}	
	
	/**
	 * Parses the value of variable in JBPM and load all data source properties if there are
	 * @param ds data source to be loaded
	 * @param valueParm value specified on JBPM xml assignment
	 * @throws JBpmException if any error occurs
	 */
	public static final void loadDataSource(DataSource ds, String valueParm) throws JBpmException{
		// trim value
		String value = valueParm.trim();
	
		// parses using comma
		String[] stmtTokens = StringUtils.split(value, COMMAND_SEPARATOR);
		if (stmtTokens != null && stmtTokens.length > 0){
			// scans all tokes
			for (int i=0; i<stmtTokens.length; i++){
				String token = stmtTokens[i].trim();
				// is datasetname?
				if (StringUtils.startsWithIgnoreCase(token, PROPERTIES_PREFIX)){
					// gets all string after DSN=
					String subToken = getValue(token, PROPERTIES_PREFIX);
					if (subToken.startsWith("(") && subToken.endsWith(")")){
						// gets content inside brackets
						String subValue = StringUtils.removeEnd(StringUtils.removeStart(subToken, "("), ")");
						// if subvalue is null, exception
						if (subValue != null){
							// changes all ; in line separator because by areader it can load a properties object
							subValue = subValue.replace(VALUE_SEPARATOR, System.getProperty("line.separator")).concat(System.getProperty("line.separator"));
							// loads all properties from a reader
							StringReader reader = new StringReader(subValue);
							Properties properties = new Properties();
							try {
								// loads it
								properties.load(reader);
								// scans all properties to creates property object
								for (Entry<Object, Object> entry : properties.entrySet()){
									// creates properties
									Property prop = new Property();
									prop.setName(entry.getKey().toString());
									prop.addText(entry.getValue().toString());
									ds.addProperty(prop);
								}
							} catch (IOException e) {
								LogAppl.getInstance().ignore(e.getMessage(), e);
							}
						} else {
							throw new JBpmException(JBpmMessage.JEMM054E, ds.getName());
						}
					} else {
						throw new JBpmException(JBpmMessage.JEMM054E, ds.getName());
					}
				} else if (StringUtils.startsWithIgnoreCase(token, RESOURCE_PREFIX)){
					String subToken = getValue(token, RESOURCE_PREFIX);
					// sets that is not a text
					ds.setResource(subToken);
				} 
			}
		}
	}
	
	/**
	 * Extract the sub token (value) considering blanks between = and prefix
	 * @param token token to check inside the string
	 * @param prefix prefix to check
	 * @return the rest of the token string
	 */
	private static String getValue(String token, String prefix){
		String subToken = StringUtils.substringAfter(token, prefix).trim();
		// checks if there TOKEN=VALUE
		if (subToken.startsWith("=")){
			return StringUtils.substringAfter(subToken, "=").trim();
		} else {
			return null;
		}
	}

}
