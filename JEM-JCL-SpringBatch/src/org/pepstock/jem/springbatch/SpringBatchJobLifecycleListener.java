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
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.pepstock.jem.Job;
import org.pepstock.jem.PropertiesWrapper;
import org.pepstock.jem.Result;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.events.JobLifecycleListener;
import org.pepstock.jem.springbatch.DataSourceFactory;
import org.pepstock.jem.springbatch.SpringBatchMessage;
import org.pepstock.jem.springbatch.SpringBatchRuntimeException;
import org.springframework.batch.core.ExitStatus;

/**
 * @author Andrea "Stock" Stocchero
 * @version 2.3
 */
public class SpringBatchJobLifecycleListener implements JobLifecycleListener {
	
	private static final int FIELD_1 = 1;
	
	private static final int FILED_2 = 2;
	
	private static final int FIELD_3 = 3;
	
	private static final int FIELD_4 = 4;
	
	/**
	 * Internal property to save the job instance ID of Springbatch
	 */
	public static final String JOB_INSTANCE_ID = "job.instance.id";
	
	/**
	 * Internal property to save the job execution ID of Springbatch
	 */
	public static final String JOB_EXECUTION_ID = "job.execution.id";
	
	private static final String DELETE_FROM_STEP_EXECUTION_CONTEXT = "DELETE FROM batch_step_execution_context WHERE STEP_EXECUTION_ID IN (SELECT STEP_EXECUTION_ID FROM springbatch.batch_step_execution WHERE JOB_EXECUTION_ID = ?)";

	private static final String DELETE_FROM_STEP_EXECUTION = "DELETE FROM batch_step_execution WHERE JOB_EXECUTION_ID = ?";

	private static final String DELETE_FROM_JOB_EXECUTION_CONTEXT = "DELETE FROM batch_job_execution_context WHERE JOB_EXECUTION_ID = ?"; 

	private static final String DELETE_FROM_JOB_EXECUTION_PARAMS = "DELETE FROM batch_job_execution_params WHERE JOB_EXECUTION_ID = ?";

	private static final String DELETE_FROM_JOB_EXECUTION = "DELETE FROM batch_job_execution WHERE JOB_EXECUTION_ID = ?";

	private static final String DELETE_FROM_JOB_INSTANCE = "DELETE FROM batch_job_instance WHERE JOB_INSTANCE_ID = ?";
	
	private static final String GET_JOB_EXECUTION_IDS_BY_INSTANCE = "SELECT JOB_EXECUTION_ID FROM batch_job_execution WHERE JOB_INSTANCE_ID = ?";
	
	private static final String UPDATE_STEP_EXECUTION = "UPDATE batch_step_execution SET END_TIME = ?, STATUS = ? WHERE STEP_NAME= ? AND JOB_EXECUTION_ID = ?";

	private static final String UPDATE_JOB_EXECUTION = "UPDATE batch_job_execution SET END_TIME = ?, STATUS = ?, EXIT_CODE = ? WHERE JOB_EXECUTION_ID = ?";

	
	private static final String SCHEMA_RESOURCE_FORMAT = "org/springframework/batch/core/schema-{0}.sql";

	private static final String TABLES_JOB_INSTANCE = "batch_job_instance";

	private BasicDataSource datasource = null;
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.node.events.JobLifecycleListener#init(java.util.Properties)
	 */
	@Override
	public void init(Properties properties) {
		StringWriter ws = new StringWriter();
		String dataBaseType = DataSourceFactory.getDataSourceType(properties);
		if (dataBaseType == null) {
			throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS057E);
		} else {
			InputStream is = null;
			try {
				is = this.getClass().getClassLoader().getResourceAsStream(MessageFormat.format(SCHEMA_RESOURCE_FORMAT, dataBaseType));
				if (is == null) {
					throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS058E, dataBaseType);
				}
				IOUtils.copy(is, ws);
				LogAppl.getInstance().emit(SpringBatchMessage.JEMS059I, dataBaseType);
			} catch (IOException e) {
				throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS058E, e, dataBaseType);
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
			if (tablesToBeCreate){
				LogAppl.getInstance().emit(SpringBatchMessage.JEMS060I);
				stmt = conn.createStatement();
				for (String sql : StringUtils.split(ws.toString(), ';')) {
					if (sql.trim().length() > 0) {
						stmt.execute(sql + ";");
					}
				}
				conn.commit();
				LogAppl.getInstance().emit(SpringBatchMessage.JEMS061I);
			}
		} catch (SQLException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					LogAppl.getInstance().ignore(e1.getMessage(), e1);
				}
			}
			throw new SpringBatchRuntimeException(SpringBatchMessage.JEMS062E, e);
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
		PropertiesWrapper props = job.getJcl().getProperties();
		if (props != null && props.containsKey(JOB_INSTANCE_ID) && props.containsKey(JOB_EXECUTION_ID)){
			Long jobInstanceId = Long.parseLong(props.get(JOB_INSTANCE_ID));  
			Long jobExecutionId = Long.parseLong(props.get(JOB_EXECUTION_ID));
			if (job.getResult().getReturnCode() == Result.SUCCESS){
				clean(jobInstanceId);
			} else if (job.getResult().getReturnCode() == Result.CANCELED || job.getResult().getReturnCode() == Result.FATAL){
				LogAppl.getInstance().emit(SpringBatchMessage.JEMS063I, job);
				String stepName = job.getCurrentStep().getName();
				repair(jobExecutionId, stepName);
			}
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
			LogAppl.getInstance().ignore(e.getMessage(), e);
			LogAppl.getInstance().emit(SpringBatchMessage.JEMS065E, e, jobInstanceId);
			
			if (conn != null){
				try {
					conn.rollback();
				} catch (SQLException e1) {
					LogAppl.getInstance().ignore(e1.getMessage(), e1);
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
	 * @param jobInstanceId
	 * @param jobExecutionId
	 */
	void repair(Long jobExecutionId, String stepName){
		Connection conn = null;
		try {
			conn = datasource.getConnection();
			conn.setAutoCommit(false);
			update(conn, UPDATE_STEP_EXECUTION, jobExecutionId, stepName);
			update(conn, UPDATE_JOB_EXECUTION, jobExecutionId, null);
			conn.commit();
		} catch (SQLException e) {
			LogAppl.getInstance().ignore(e.getMessage(), e);
			LogAppl.getInstance().emit(SpringBatchMessage.JEMS064E, e, jobExecutionId);
			if (conn != null){
				try {
					conn.rollback();
				} catch (SQLException e1) {
					LogAppl.getInstance().ignore(e1.getMessage(), e1);
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
			selectStmt.setLong(FIELD_1, id);
			// executes the statement
			rs = selectStmt.executeQuery();
			while(rs.next()){
				result.add(rs.getLong(FIELD_1));
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
			updateStmt.setLong(FIELD_1, id);
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

	/**
	 * 
	 * @param connection
	 * @param sql
	 * @param id
	 * @throws SQLException
	 */
	private static void update(Connection connection, String sql, Long id, String stepName) throws SQLException{
		PreparedStatement updateStmt = null;
		try {
			updateStmt = connection.prepareStatement(sql);
			// set resource name in prepared statement
			Timestamp endedTime = new Timestamp(System.currentTimeMillis());
			String status = ExitStatus.FAILED.getExitCode();
			updateStmt.setTimestamp(FIELD_1, endedTime);
			updateStmt.setString(FILED_2, status);
			if (stepName != null){
				updateStmt.setString(FIELD_3, stepName);
			} else {
				updateStmt.setString(FIELD_3, status);
			}
			updateStmt.setLong(FIELD_4, id);
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
