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
package org.pepstock.jem.protocol;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Random;

import org.pepstock.jem.util.JobIdGenerator;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class TestR {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		Random rnd = new Random();
//		for (int i=0; i<100; i++){
//
////			System.err.println(Math.abs(rnd.nextLong()));
//			System.out.println(JobIdGenerator.createJobId(rnd.nextInt(Integer.MAX_VALUE), rnd.nextInt(Integer.MAX_VALUE)));
//			
//			
//		}

		
		// testif a job id has been inserted
		MessageFormat jobIdFormat = new MessageFormat(JobIdGenerator.JOBID_FORMAT);
		// checks if is by job id
		try {
			// try to parse the job id
			Object[] obj = jobIdFormat.parse(JobIdGenerator.createJobId(rnd.nextInt(Integer.MAX_VALUE), rnd.nextInt(Integer.MAX_VALUE)));
			// checks if the ID is the same
			System.err.println(obj.length);
			System.err.println(obj[0].getClass().getName());
			System.err.println(obj[1].getClass().getName());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

}
