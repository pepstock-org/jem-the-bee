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
package org.pepstock.jem.ant.tasks;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.catalog.Disposition;
import org.pepstock.jem.ant.AntException;
import org.pepstock.jem.ant.AntMessage;


/**
 * Parser of all information, extracted from metadata of SCRIPTS JCL, necessary to creates data description and locks.<br>
 * This is the syntax to use for data description (all commands are not positionals):<br>
 * <ul>
 * <li> <code>DSN=<i>dsn</i>,DISP=[SHR|OLD|MOD|NEW]</code>
 * <li> <code>DSN=(<i>dsn1</i>;<i>dsn2</i>),DISP=SHR</code>
 * <li> <code>DSN=<i>dsn</i>,DISP=[SHR|OLD|MOD|NEW],DATASOURCE=<i>datasource</i></code>
 * </ul>
 * To set locks, it's enough to set the property with all locks names comma separated.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 * 
 */
public final class ValueParser {

	private static final String SYSOUT = "SYSOUT";
	
	private static final String DSN_PREFIX = "DSN";

	private static final String DISP_PREFIX = "DISP";
	
	private static final String DATASOURCE_PREFIX = "DATASOURCE";
	
	private static final String COMMAND_SEPARATOR = ",";

	private static final String VALUE_SEPARATOR = ";";
	
	/**
	 * To avoid any instantiation
	 */
	private ValueParser() {
		
	}
	
	/**
	 * Parses the value of variables into SCRIPT JCL and loads datasets into data descriptions
	 * @param dd data description to load
	 * @param valueParm value of property in metadata
	 * @throws AntException if any error occurs
	 */
	public static final void loadDataDescription(DataDescription dd, String valueParm) throws AntException{
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
						// checks if the content is inside of brackets
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
	        	throw new AntException(AntMessage.JEMA072E, dd.getName());
			}
			dd.setDisposition(disposition);
			
			// if sets SYSOUT but also DSN=, this is not allowed
			if (isSysout && dsn != null){
	        	throw new AntException(AntMessage.JEMA073E, dd.getName());
			}
			// sets sysout
			dd.setSysout(isSysout);
			// if not sysout, loads datasets
			if (!isSysout){
				// datasource can be set ONLY with 1 dataset
				if (dsn != null){
					if (dsn.length > 1 && dataSource != null){
						throw new AntException(AntMessage.JEMA074E, dd.getName());
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
	}
	/**
	 * Creates all data set using the dsn or content values.
	 * @param dd data description object to be loaded of data sets. Passed only for the exception
	 * @param dsn data set name or null if is a INLINE data set
	 * @param content content of INLINE data set or null
	 * @return a data set instance
	 * @throws AntException if any error occurs
	 */
	private static DataSet createDataSet(DataDescription dd, String dsn, String content) throws AntException{
		// if DSN = null and content is empty, exception
		if (dsn == null && content == null){
			throw new AntException(AntMessage.JEMA075E, dd.getName());
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
	
	/**
	 * Loads all lock object from a comma separated string
	 * @param valueParm value to parse
	 * @return list of locks
	 */
	public static final List<Lock> loadLocks(String valueParm){
		// list of locks to return
		List<Lock> locks = new ArrayList<Lock>();
		// list of locks are comma separated
		String toParse = valueParm;
		// checks if is a valid string
		if (toParse != null && toParse.trim().length() > 0){
			// parses using comma
			String[] parsed = toParse.split(COMMAND_SEPARATOR);
			// scans of all names creating all locks
			for (String name : parsed){
				Lock lock = new Lock();
				lock.setName(name.trim());
				locks.add(lock);
			}
		}
		return locks;
	}

}
