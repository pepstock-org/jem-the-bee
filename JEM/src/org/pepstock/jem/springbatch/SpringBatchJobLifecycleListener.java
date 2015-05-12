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
package org.pepstock.jem.springbatch;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.Result;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.events.JobLifecycleListener;

/**
 * @author Andrea "Stock" Stocchero
 * @version 3.0
 */
public class SpringBatchJobLifecycleListener implements JobLifecycleListener {
	
	public static final String JOB_INSTANCE_ID = "job.instance.id";
	
	public static final String JOB_EXECUTION_ID = "job.execution.id";
	
	private static final String DELETE_FROM_STEP_EXECUTION_CONTEXT = "DELETE FROM batch_step_execution_context WHERE STEP_EXECUTION_ID IN (SELECT STEP_EXECUTION_ID FROM springbatch.batch_step_execution WHERE JOB_EXECUTION_ID = ?)";

	private static final String DELETE_FROM_STEP_EXECUTION = "DELETE FROM batch_step_execution WHERE JOB_EXECUTION_ID = ?";

	private static final String DELETE_FROM_JOB_EXECUTION_CONTEXT = "DELETE FROM batch_job_execution_context WHERE JOB_EXECUTION_ID = ?"; 

	private static final String DELETE_FROM_JOB_EXECUTION_PARAMS = "DELETE FROM batch_job_execution_params WHERE JOB_EXECUTION_ID = ?";

	private static final String DELETE_FROM_JOB_EXECUTION = "DELETE FROM batch_job_execution WHERE JOB_EXECUTION_ID = ?";

	private static final String DELETE_FROM_JOB_INSTANCE = "DELETE FROM batch_job_instance WHERE JOB_INSTANCE_ID = ?";
	
	private static final String GET_JOB_EXECUTION_IDS_BY_INSTANCE = "SELECT JOB_EXECUTION_ID FROM batch_job_execution WHERE JOB_INSTANCE_ID = ?";
	
	private static final String SCHEMA_RESOURCE_FORMAT = "org/springframework/batch/core/schema-{0}.sql";

	private static final String TABLES_JOB_INSTANCE = "batch_job_instance";

	private BasicDataSource datasource = null;
	
	/**
	 * 
	 */
	public SpringBatchJobLifecycleListener() {
		
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.events.JobLifecycleListener#init(java.util.Properties)
	 */
	@Override
	public void init(Properties properties) {
	
		StringWriter ws = new StringWriter();
		
		String dataBaseType = DataSourceFactory.getDataSourceType(properties);
		if (dataBaseType == null) {
			// FIXME
			throw new RuntimeException("Database type is null");
		} else {
			InputStream is = null;
			try {
				is = this.getClass().getClassLoader().getResourceAsStream(MessageFormat.format(SCHEMA_RESOURCE_FORMAT, dataBaseType));
				if (is == null) {
					throw new IOException("Unale to find the schema for '" + dataBaseType + "'");
				}
				IOUtils.copy(is, ws);
				// FIXME log
			} catch (IOException e) {
				// FIXME
				throw new RuntimeException(e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						LogAppl.getInstance().ignore(e.getMessage(), e);
					}
				}
			}
		}
		Connection conn = null;
		Statement stmt = null;
		try {
			
			datasource = (BasicDataSource)DataSourceFactory.createDataSource(properties);
			conn = datasource.getConnection();
			conn.setAutoCommit(false);

			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getTables(null, null, TABLES_JOB_INSTANCE, new String[] { "TABLE", "ALIAS" });
			boolean tablesToBeCreate = !rs.next();
			rs.close();
			// FIXME log
			if (tablesToBeCreate){
				stmt = conn.createStatement();
				for (String sql : StringUtils.split(ws.toString(), ';')) {
					if (sql.trim().length() > 0) {
						stmt.execute(sql + ";");
					}
				}
				conn.commit();
				// FIXME log
			}
		} catch (SQLException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
			// FIXME
			throw new RuntimeException(e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * 
	 */
	void close(){
		if (datasource != null){
			try {
				datasource.close();
			} catch (SQLException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.events.JobLifecycleListener#queued(org.pepstock.jem.Job)
	 */
	@Override
	public void queued(Job job) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.events.JobLifecycleListener#running(org.pepstock.jem.Job)
	 */
	@Override
	public void running(Job job) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.events.JobLifecycleListener#ended(org.pepstock.jem.Job)
	 */
	@Override
	public void ended(Job job) {
		if (job.getResult().getReturnCode() == Result.SUCCESS){
			Map<String, Object> props = job.getJcl().getProperties();
			if (props != null && props.containsKey(JOB_INSTANCE_ID) && props.containsKey(JOB_EXECUTION_ID)){
				Long jobInstanceId = (Long)props.get(JOB_INSTANCE_ID);  
//				Long jobExecutionId = (Long)props.get(JOB_EXECUTION_ID);
				clean(jobInstanceId);
			}
		} else if (job.getResult().getReturnCode() == Result.CANCELED){
			// FIXME
		}

	}
	
	/**
	 * 
	 * @param jobInstanceId
	 * @param jobExecutionId
	 */
	void clean(Long jobInstanceId){
		Connection conn = null;
		try {
			conn = datasource.getConnection();
			conn.setAutoCommit(false);
			List<Long> jobExecutionIds = get(conn, GET_JOB_EXECUTION_IDS_BY_INSTANCE, jobInstanceId);

			for (Long jobExecutionId : jobExecutionIds){
				delete(conn, DELETE_FROM_STEP_EXECUTION_CONTEXT, jobExecutionId);
				delete(conn, DELETE_FROM_STEP_EXECUTION, jobExecutionId);
				delete(conn, DELETE_FROM_JOB_EXECUTION_CONTEXT, jobExecutionId);
				delete(conn, DELETE_FROM_JOB_EXECUTION_PARAMS, jobExecutionId);
				delete(conn, DELETE_FROM_JOB_EXECUTION, jobExecutionId);
			}
			delete(conn, DELETE_FROM_JOB_INSTANCE, jobInstanceId);
			conn.commit();
		} catch (SQLException e) {
			// FIXME
			e.printStackTrace();
			if (conn != null){
				try {
					conn.rollback();
				} catch (SQLException e1) {
					LogAppl.getInstance().ignore(e.getMessage(), e);
				}
			}
		} finally {
			try {
				if (conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
		}
	}

	/**
	 * 
	 * @param connection
	 * @param sql
	 * @param id
	 * @throws SQLException
	 */
	private static List<Long> get(Connection connection, String sql, Long id) throws SQLException{
		List<Long> result = new ArrayList<Long>();
		
		PreparedStatement selectStmt = null;
		ResultSet rs = null;
		try {
			selectStmt = connection.prepareStatement(sql);
			// set resource name in prepared statement
			selectStmt.setLong(1, id);
			// executes the statement
			rs = selectStmt.executeQuery();
			while(rs.next()){
				result.add(rs.getLong(1));
			}
		} finally{
			// closes statement
			try {
				if (rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}

			// closes statement
			try {
				if (selectStmt != null){
					selectStmt.close();
				}
			} catch (SQLException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}
		}
		return result;
	}

	
	/**
	 * 
	 * @param connection
	 * @param sql
	 * @param id
	 * @throws SQLException
	 */
	private static void delete(Connection connection, String sql, Long id) throws SQLException{
		PreparedStatement updateStmt = null;
		try {
			updateStmt = connection.prepareStatement(sql);
			// set resource name in prepared statement
			updateStmt.setLong(1, id);
			// executes the statement
			updateStmt.executeUpdate();
		} finally{
			// closes statement
			try {
				if (updateStmt != null){
					updateStmt.close();
				}
			} catch (SQLException e) {
				LogAppl.getInstance().ignore(e.getMessage(), e);
			}

		}
	}


}
