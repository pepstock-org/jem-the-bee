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
package org.pepstock.jem.junit.test.springbatch.java;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class NullMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.err.println("DONE!");
		try {
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"org.pepstock.jem.node.tasks.jndi.JemContextFactory");

			InitialContext context = new InitialContext(env);

			// get data source, note that jem-db is the name of the dataSource
			// present in the JCL
			DataSource dataSource = (DataSource) context.lookup("jem-db");
			System.err.println("Connecting to database ...");
			Connection conn = dataSource.getConnection();
			System.err.println("Connected to database ...");
			Statement statement = conn.createStatement();
			String sql = "SELECT * FROM ROLES_MAP";
			ResultSet rs = statement.executeQuery(sql);
			System.err.println(sql + ":");
			while (rs.next()) {
				System.err.println(rs.getObject(1));
			}
			rs.close();
			statement.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
