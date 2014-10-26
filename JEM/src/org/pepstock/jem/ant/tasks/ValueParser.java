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
 * DSN=dsn1,DSIP=dis,DATASOURCE=dataource
 * DSN=(dsn1;dns2),DSIP=dis,DATASOURCE=dataource
 * 
 * 
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
	 * Parses the value of variable in JBPM and load datasets into data description
	 * @param dd
	 * @param valueParm
	 * @throws AntException
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
				if (dsn != null && dsn.length > 1 && dataSource != null){
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
	/**
	 * 
	 * @param dsn
	 * @param content
	 * @return
	 * @throws AntException
	 */
	private static DataSet createDataSet(DataDescription dd, String dsn, String content) throws AntException{
		if (dsn == null && content == null){
			throw new AntException(AntMessage.JEMA075E, dd.getName());
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
	
	/**
	 * Loads all lock object from a comma separated string
	 * @param valueParm value to parse
	 * @return list of locks
	 */
	public static final List<Lock> loadLocks(String valueParm){
		List<Lock> locks = new ArrayList<Lock>();
		String toParse = valueParm;
		if (toParse != null && toParse.trim().length() > 0){
			String[] parsed = toParse.split(COMMAND_SEPARATOR);
			for (String name : parsed){
				Lock lock = new Lock();
				lock.setName(name.trim());
				locks.add(lock);
			}
		}
		return locks;
	}

}
