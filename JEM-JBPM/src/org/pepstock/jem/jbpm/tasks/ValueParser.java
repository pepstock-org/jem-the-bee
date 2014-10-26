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
 * DSN=dsn1,DSIP=dis,DATASOURCE=dataource
 * DSN=(dsn1;dns2),DSIP=dis,DATASOURCE=dataource
 * 
 * 
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
	 * Parses the value of variable in JBPM and load datasets into data description
	 * @param dd
	 * @param valueParm
	 * @throws JBpmException
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
							subValue = StringUtils.substringBetween(subToken, "(", ")");
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
			if (!isSysout){
				// datasource can be set ONLY with 1 dataset
				if (dsn != null && dsn.length > 1 && dataSource != null){
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
	 * 
	 * @param dsn
	 * @param content
	 * @return
	 * @throws JBpmException
	 */
	private static DataSet createDataSet(DataDescription dd, String dsn, String content) throws JBpmException{
		if (dsn == null && content == null){
			throw new JBpmException(JBpmMessage.JEMM053E, dd.getName());
		}
		
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
	 * @param ds
	 * @param valueParm
	 * @throws JBpmException
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
					String subValue = StringUtils.substringBetween(subToken, "(", ")");
					if (subValue != null){
						subValue = subValue.replace(VALUE_SEPARATOR, System.getProperty("line.separator")).concat(System.getProperty("line.separator"));
						StringReader reader = new StringReader(subValue);
						Properties properties = new Properties();
						try {
							properties.load(reader);
							for (Entry<Object, Object> entry : properties.entrySet()){
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
	 * @param token
	 * @param prefix
	 * @return
	 */
	private static String getValue(String token, String prefix){
		String subToken = StringUtils.substringAfter(token, prefix).trim();
		if (subToken.startsWith("=")){
			return StringUtils.substringAfter(subToken, "=").trim();
		} else {
			return null;
		}
	}

}
