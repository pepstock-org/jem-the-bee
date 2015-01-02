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
package org.pepstock.jem.junit.test.springbatch.java;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.pepstock.jem.annotations.AssignDataSource;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.2
 */
public class DataSourceConnMain {
	
	@AssignDataSource("jem-db")
	private static DataSource dataSource = null;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
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
	}

}
