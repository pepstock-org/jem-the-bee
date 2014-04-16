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

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.pepstock.jem.junit.test.antutils.java.ConnectToDataSource;
import org.pepstock.jem.springbatch.tasks.JemTasklet;
import org.pepstock.jem.springbatch.tasks.TaskletException;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.w3c.dom.Document;

/**
 * This class is a spring batch tasklet thath will delete all the dataset create
 * during the junit test relative to spring batch.
 * 
 * @author Simone "busy" Businaro
 * 
 */
public class DataSourceConnTasklet extends JemTasklet {

	static Logger log = Logger.getLogger(ConnectToDataSource.class.getName());
	
	private JdbcTemplate jdbcTemplate = null;

	/**
	 * Empty constructor
	 */
	public DataSourceConnTasklet() {
	}
	
	/**
	 * @return the jdbcTemplate
	 */
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	/**
	 * @param jdbcTemplate the jdbcTemplate to set
	 */
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.springbatch.tasks.JemTasklet#run(org.springframework
	 * .batch.core.StepContribution,
	 * org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus run(StepContribution stepContribution,
			ChunkContext chuckContext) throws TaskletException {
		
		log.info("Number of records on ROLES_MAP: "+getJdbcTemplate().queryForInt("SELECT COUNT(*) FROM ROLES_MAP"));
		
		try {
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"org.pepstock.jem.node.tasks.jndi.JemContextFactory");

			InitialContext context = new InitialContext(env);
			// configure log4j reading dataset set in JCL
			FileInputStream fis = (FileInputStream) context.lookup("log4j");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fis);
			DOMConfigurator.configure(doc.getDocumentElement());

			// get data source, note that jem-db is the name of the dataSource
			// present in the JCL
			DataSource dataSource = (DataSource) context.lookup("jem-db");
			log.info("Connecting to database ...");
			Connection conn = dataSource.getConnection();
			log.info("Connected to database ...");
			Statement statement = conn.createStatement();
			String sql = "SELECT * FROM ROLES_MAP";
			ResultSet rs = statement.executeQuery(sql);
			log.info(sql + ":");
			while (rs.next()) {
				log.info(rs.getObject(1));
			}
			rs.close();
			statement.close();
			conn.close();
		} catch (Exception e) {
			throw new TaskletException(e);
		}
		return RepeatStatus.FINISHED;
	}

}
