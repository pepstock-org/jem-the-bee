/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.commons;

import java.util.Collection;

/**
 * Utility class to handle CSV data
 * @author Marco "Fuzzo" Cuccato
 *
 */
public class CSVUtil {

	/**
	 * 
	 */
    public static final String COMMA_STRING = ",";
    /**
     * 
     */
    public static final String COMMA_SPACE_STRING = ", ";
    /**
     * 
     */
    public static final char COMMA_CHAR = ',';
    /**
     * 
     */
    public static final String NULL = "null";
    
    private CSVUtil() {
    }
    
    /**
     * Creates CSV phrase from objects
     * @param objects objects to be returned in phrase
     * @return a string, with each object separated by <code>,</code>
     */
    public static String getCSVPhrase(Collection<?> objects) {
        return getCSVPhrase(objects, COMMA_CHAR);
    }
    
    /**
     * Creates CSV phrase from objects
     * @param objects objects to be returned in phrase
     * @param separator the char to be used as separator
     * @return a string, with each object separated by separator 
     */
    public static String getCSVPhrase(Collection<?> objects, char separator) {
        return getCSVPhrase(objects, String.valueOf(separator));
    }
    
    /**
     * Creates CSV phrase from objects
     * @param objects objects to be returned in phrase
     * @param separator the string to be used as separator
     * @return a string, with each object separated by separator 
     */
    public static String getCSVPhrase(Collection<?> objects, String separator) {
        StringBuilder s = new StringBuilder();
        if (!objects.isEmpty()) {
            for (Object o: objects) {
                if (o != null) {
                    s.append(o.toString());
                } else {
                    s.append(NULL);
                }
                s.append(separator);
            }
            s.setLength(s.length()-separator.length());
        }
        return s.toString();
    }
    
    /**
     * Creates CSV phrase from objects
     * @param objects objects to be returned in phrase
     * @return a string, with each object separated by <code>,</code>
     */
    public static String getCSVPhrase(Object[] objects) {
        return getCSVPhrase(objects, COMMA_CHAR);
    }
    
    /**
     * Creates CSV phrase from objects
     * @param objects objects to be returned in phrase
     * @param separator the char to be used as separator
     * @return a string, with each object separated by separator 
     */
    public static String getCSVPhrase(Object[] objects, char separator) {
        return getCSVPhrase(objects, String.valueOf(separator));
    }
    
    /**
     * Creates CSV phrase from objects
     * @param objects objects to be returned in phrase
     * @param separator the string to be used as separator
     * @return a string, with each object separated by separator 
     */
    public static String getCSVPhrase(Object[] objects, String separator) {
        StringBuilder s = new StringBuilder();
        if (objects.length > 0) {
            for (Object o: objects) {
                if (o != null) {
                    s.append(o.toString());
                } else {
                    s.append(NULL);
                }
                s.append(separator);
            }
            s.setLength(s.length()-separator.length());
        }
        return s.toString();
    }
    
    /**
     * Split objects in csvPhrase into array
     * @param csvPhrase the csv string representing the objects, separated by comma
     * @return a String[]
     */
    public static String[] split(String csvPhrase) {
        return split(csvPhrase, COMMA_STRING);
    }
    
    /**
     * Split objects in csvPhrase into array
     * @param csvPhrase the csv string representing the objects
     * @param separator the separator char used in csvPhrase
     * @return a String[]
     */
    public static String[] split(String csvPhrase, char separator) {
        return split(csvPhrase, Character.toString(separator));
    }

    /**
     * Split objects in csvPhrase into array
     * @param csvPhrase the csv string representing the objects
     * @param separator the separator string used in csvPhrase
     * @return a String[]
     */
    public static String[] split(String csvPhrase, String separator) {
        return csvPhrase.split(separator);
    }

    /**
     * Split and trim objects in csvPhrase into array
     * @param csvPhrase the csv string representing the objects
     * @return a String[]
     */
    public static String[] splitAndTrim(String csvPhrase) {
        String[] toReturn = split(csvPhrase);
        trim(toReturn);
        return toReturn;
    }

    /**
     * Split and trim objects in csvPhrase into array
     * @param csvPhrase the csv string representing the objects
     * @param separator the separator used in phrase
     * @return a String[]
     */
    public static String[] splitAndTrim(String csvPhrase, char separator) {
        String[] toReturn = split(csvPhrase, separator);
        trim(toReturn);
        return toReturn;
    }

    /**
     * Split and trim objects in csvPhrase into array
     * @param csvPhrase the csv string representing the objects
     * @param separator the separator used in phrase
     * @return a String[]
     */
    public static String[] splitAndTrim(String csvPhrase, String separator) {
        String[] toReturn = split(csvPhrase, separator);
        trim(toReturn);
        return toReturn;
    }

    /**
     * Trim all strings contained in the array
     * @param strings a String[] containing String to be trimmed
     */
    public static void trim(String[] strings) {
        if (strings != null) {
            for (int i=0; i < strings.length; i++) {
                strings[i] = strings[i].trim();
            }
        }
    }
}
