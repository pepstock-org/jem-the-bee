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
package org.pepstock.jem.ant.tasks.utilities.sort;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.ant.AntMessage;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.tasks.jndi.ContextUtils;
import org.pepstock.jem.util.CharSet;

/**
 * Default comparator used by SORT ANT utilities. It parses the command, extracting columns and type to sort the file.<br>
 * The SORT utility can accept class parameter to indicate a Comparator<String> implementation to use to sort the data. 
 * If class misses, it uses the default comparator which can parse a list of order-by statements, found inside a data description called 
 * COMMAND (be careful because is case sensitive). The format is:<br>
 * <br>
 * <code>( start [, length] [, ASC|DESC] ),...</code><br>
 * <br>
 * It's mandatory to indicate the start point in the string. Length is optional (default is 0) and, if missing or 0 or less than 0, 
 * the length is all string starting from start character. The type of sort is optional as well (default is ASC) and if indicated must 
 * both ASC (for ascending order) or DESC (for descending one). You can have many order-by statements (comma separated), 
 * used in case of the previous statements compares equals values.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.2	
 *
 */
public class DefaultComparator implements Comparator<String>, Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final String DATA_DESCRIPTION_NAME = "COMMAND";
	
	private static final MessageFormat MESSAGE_FORMAT_1 = new MessageFormat("({0,number,integer})");
	
	private static final MessageFormat MESSAGE_FORMAT_2 = new MessageFormat("({0,number,integer},{1,number,integer})");
	
	private static final MessageFormat MESSAGE_FORMAT_1_STRING = new MessageFormat("({0,number,integer},{1})");
	
	private static final MessageFormat MESSAGE_FORMAT_2_STRING = new MessageFormat("({0,number,integer},{1,number,integer},{2})");
	
	private List<SingleComparator> comparators = new LinkedList<DefaultComparator.SingleComparator>();
	

	/**
	 * Checks if COMMAND data description is allocated. If not, uses the default string 
	 * comparator. Otherwise loads all comparators.
	 * 
	 * @throws Exception if any IO of parsing commands exception occurs
	 * 
	 */
	public DefaultComparator(){
		// new initial context to access by JNDI to COMMAND DataDescription
		InitialContext ic;
		try {
			ic = ContextUtils.getContext();
			// gets inputstream
			Object filein = (Object) ic.lookup(DATA_DESCRIPTION_NAME);
			// reads content of inout stream
			StringBuilder recordsSB = read((InputStream) filein);
			// trims result to see if is empty
			String records = recordsSB.toString().trim();
			// loads commands
			loadCommands(records);
		} catch (NamingException e) {
			LogAppl.getInstance().emit(AntMessage.JEMA044W, e, e.getMessage());
		} catch (ParseException e) {
			LogAppl.getInstance().emit(AntMessage.JEMA058W, e, e.getMessage());
		} catch (IOException e) {
			LogAppl.getInstance().emit(AntMessage.JEMA058W, e, e.getMessage());
		} 
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(String o1, String o2) {
		// in case of empty strings
		if(o1.length() == 0) {
			return o2.length();
		}
		if(o2.length() == 0) {
			return -1;
		}
		// checks if comparators collection is empty
		// if yes uses the default string comparator 
		if (!comparators.isEmpty()){
			//Scans all comparator using the comparators in sequence  
			for (SingleComparator c : comparators){
				// FIRST STRING
				// if the comparator has length, use length 
				// gets the string needed to compare following what command wants
				String partO1 = getRowPart(o1, c);
				// SECOND STRING
				// if the comparator has length, use length 
				// gets the string needed to compare following what command wants
				String partO2 = getRowPart(o2, c);
				// compares the strings creates using command 
				int compareResult = partO1.compareToIgnoreCase(partO2);
				// 2 strings are equals (comparator returns 0)
				// go to next comparator
				if (compareResult != 0){
					// strings not equal, so changes the result basing
					// what the command wants.
					// if wants ascending, return result of comparing, otherwise
					// it changes to opposite result
					compareResult =  (c.getMode().equalsIgnoreCase(SingleComparator.ASC)) ? compareResult * 1 : compareResult * -1;
					return compareResult;
				}
			}
			return 0;
		} else {
			return o1.compareToIgnoreCase(o2);
		}
	}
	
	/**
	 * Extract the string part of row to compare, using comparator
	 * @param row row of file to compare
	 * @param c comparator
	 * @return part of row
	 */
	private String getRowPart(String row, SingleComparator c){
		int start = c.getStart();
		int length = c.getLength();
		int maxOffset = start + length;
		
		if (row.length() < start){
			return "";
		} else if (row.length() <= maxOffset){
			return row.substring(start);
		}
		return length > 0 ? row.substring(start, maxOffset-1) : row.substring(start) ;
	}

	/**
	 * Loads commands from COMMAND data description
	 * 
	 * @param records lines of data set
	 * @throws ParseException if there is a mistake on command
	 */
	private void loadCommands(String records) throws ParseException{
		// splits all words and rejoins removing useless spaces
		String[] s = StringUtils.split(records, " ");
		String commandLine = StringUtils.join(s);
		LogAppl.getInstance().emit(AntMessage.JEMA045I, commandLine);
		
		// splits command with bracket and comma
		String[] s1 = StringUtils.splitByWholeSeparator(commandLine, "),");
		for (int i=0; i<s1.length; i++){
			Object[] result = null;
			// if ends with bracket, uses asis
			// otherwise adds a bracket
			String command = s1[i].endsWith(")") ? s1[i] : s1[i]+")";
			// parses in cascade mode all message formats
			// first that matches creates result
			// otherwise there is a parse exception
			try {
				result = MESSAGE_FORMAT_1.parse(command);
            } catch (ParseException e) {
            	try {
            		result =MESSAGE_FORMAT_2.parse(command);
                } catch (ParseException e1) {
	                 try {
	                	 result = MESSAGE_FORMAT_2_STRING.parse(command);
                    } catch (ParseException e2) {
                    	 try {
                    		 result = MESSAGE_FORMAT_1_STRING.parse(command);
                        } catch (ParseException e3) {
                        	LogAppl.getInstance().emit(AntMessage.JEMA046W, e3, command, e3.getMessage());
                        	throw e3;
                        }
                    }
                }
            }
			// if has result, creates a comparator
			// and adds it to a collection
			if (result != null){
				SingleComparator c;
				try {
					c = createSingleComparator(result);
					comparators.add(c);
				} catch (ParseException e) {
					LogAppl.getInstance().emit(AntMessage.JEMA046W, e, command, e.getMessage());
					throw e;
				}
				
			}
		}

	}
	
	/**
	 * Creates a SingleComparator using the data extracted by message format
	 * 
	 * @param result result of message format
	 * @return a single comparator to use in cascade
	 * @throws ParseException occurs if there is any prase excption
	 */
	private SingleComparator createSingleComparator(Object[] result) throws ParseException{
		SingleComparator c = new SingleComparator();
		// the first element of command MUST be a number
		// it is the FIRST char to use to compare 
		if (result[0] instanceof Number){
			Number n = (Number) result[0];
			c.setStart(Math.max(n.intValue(), SingleComparator.ZERO));
		} else {
			throw new ParseException("First parameter is not a Number", 0);
		}
		
		// if result has 2 elements
		// means could have a second element both a number or a label.
		if (result.length == 2){
			// if Number, is the length of string to compare
			if (result[1] instanceof Number){
				Number n = (Number) result[1];
				c.setLength(Math.max(n.intValue(), SingleComparator.ZERO));
			} else {
				// if is a string, checks if is ASC or DESC
				// otherwise exception
				String value = result[1].toString();
				if (!value.equalsIgnoreCase(SingleComparator.ASC) && !value.equalsIgnoreCase(SingleComparator.DESC)){
					throw new ParseException("Invalid format for sorting mode definition: must be ASC or DESC", 1);
				}
				c.setMode(value);
			}
		} else if (result.length == 3){
			// if result has 3 elements
			// it has element 2 as number (length)
			if (result[1] instanceof Number){
				Number n = (Number) result[1];
				c.setLength(Math.max(n.intValue(), SingleComparator.ZERO));
			} else {
				throw new ParseException("First parameter is not a Number", 1);
			}
			// and element 3 a string (mode) checks if is ASC or DESC
			// otherwise exception			
			String value = result[2].toString();
			if (!value.equalsIgnoreCase(SingleComparator.ASC) && !value.equalsIgnoreCase(SingleComparator.DESC)){
				throw new ParseException("Invalid format for sorting mode definition: must be ASC or DESC", 2);
			}
			c.setMode(value);
		} else {
			throw new ParseException("Invalid format for sorting definition: max length is 3 but is "+result.length, 0);
		}
		return c;
	}
	
	
	/**
	 * Reads a input stream putting all in a string buffer for further parsing.
	 * Removes <code>/n</code> chars. 
	 * 
	 * @param is
	 *            input stream with all commands
	 * @return string buffer with all commands
	 * @throws IOException
	 *             if IO error occurs
	 */
	private StringBuilder read(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		Scanner sc = new Scanner(is, CharSet.DEFAULT_CHARSET_NAME);
		sc.useDelimiter("\n");
		while (sc.hasNext()) {
			String record = sc.next().toString();
			sb.append(record.trim()).append(' ');
		}
		sc.close();
		return sb;
	}
	
	/**
	 * A bean which contains all information of command to perform the right sort.
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 1.0	
	 *
	 */
	static class SingleComparator implements Serializable{
		
		private static final long serialVersionUID = 1L;

		public static final int ZERO = 0;
		
		public static final String ASC = "ASC";
		
		public static final String DESC = "DESC";
		
		private int start =  ZERO;
		
		private int length =  ZERO;
		
		private String mode =  ASC;

		/**
		 * @return the start
		 */
		public int getStart() {
			return start;
		}

		/**
		 * @param start the start to set
		 */
		public void setStart(int start) {
			this.start = start;
		}

		/**
		 * @return the length
		 */
		public int getLength() {
			return length;
		}

		/**
		 * @param length the length to set
		 */
		public void setLength(int length) {
			this.length = length;
		}

		/**
		 * @return the mode
		 */
		public String getMode() {
			return mode;
		}

		/**
		 * @param mode the mode to set
		 */
		public void setMode(String mode) {
			this.mode = mode;
		}
		
		
	}
}