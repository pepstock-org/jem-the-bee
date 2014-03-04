/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013   Andrea "Stock" Stocchero
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
package org.pepstock.jem.jppf;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tools.ant.BuildException;
import org.jppf.utils.JPPFConfiguration;
import org.jppf.utils.TypedProperties;
import org.pepstock.catalog.Disposition;
import org.pepstock.jem.log.LogAppl;
import org.pepstock.jem.node.tasks.jndi.ContextUtils;
import org.pepstock.jem.springbatch.tasks.DataDescription;
import org.pepstock.jem.springbatch.tasks.DataSet;
import org.pepstock.jem.springbatch.tasks.JemTasklet;
import org.pepstock.jem.springbatch.tasks.TaskletException;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.thoughtworks.xstream.XStream;

/**
 * Springbatch tasklet which enables the integration with JPPF.
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class JPPFTasklet extends JemTasklet {
	
	private JPPFBean bean = null;

	/**
	 * @return the bean
	 */
	public JPPFBean getBean() {
		return bean;
	}

	/**
	 * @param bean the bean to set
	 */
	public void setBean(JPPFBean bean) {
		this.bean = bean;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.springbatch.tasks.JemTasklet#run(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus run(StepContribution stepContribution, ChunkContext chunkContext) throws TaskletException {
		if (bean == null){
			throw new TaskletException(JPPFMessage.JEMJ011E.toMessage().getMessage());
		}
		if (bean.getRunnable() == null){
			throw new TaskletException(JPPFMessage.JEMJ001E.toMessage().getMessage());
		}
		LogAppl.getInstance();
		try {
			// new initial context to access by JNDI to DataDescriptions
			InitialContext ic = ContextUtils.getContext();
		
			// try to load configuration from data description
			// if data description is allocated
			TypedProperties propsFromDataDescritpion = JPPFUtil.loadTypedPropertiesFromDataDescription(ic);

			// loads properties
			TypedProperties propsFromBean = loadProperties(bean);
			// extract stepContext because the step name is necessary
			StepContext stepContext = chunkContext.getStepContext();
			propsFromBean.setProperty(Keys.JEM_JOB_NAME, stepContext.getJobName());
			propsFromBean.setProperty(Keys.JEM_TASK_NAME, stepContext.getStepName());

			// checks if we're using the resources
			if (propsFromBean.getProperty(Keys.JEM_DATASOURCE) != null){
				// if yes, gets 
				String datasource = propsFromBean.getProperty(Keys.JEM_DATASOURCE);
				Object configResource = (Object) ic.lookup(datasource);
				if (configResource instanceof TypedProperties){
					TypedProperties props = (TypedProperties)configResource;

					// properties on Resource definition go on override on
					// properties on ANT task and on 
					// properties read from data description
					JPPFConfiguration.getProperties().putAll(propsFromDataDescritpion);
					JPPFConfiguration.getProperties().putAll(propsFromBean);
					JPPFConfiguration.getProperties().putAll(props);

				} else {
					throw new TaskletException(JPPFMessage.JEMJ010E.toMessage().getFormattedMessage(Keys.JEM_DATASOURCE, configResource.getClass().getName(), TypedProperties.class.getName()));
				}
			} else {
				// properties on ANT task go on override on 
				// properties read from data description
				JPPFConfiguration.getProperties().putAll(propsFromDataDescritpion);
				JPPFConfiguration.getProperties().putAll(propsFromBean);
			}

			// creates XML streamer
			XStream xstream = new XStream();
			// serializes the initial context with all resources
			// because they are necessary on grid to runnable
			String icXml = xstream.toXML(ic);
			ExecuteManager.submit(icXml);
		} catch (NamingException e) {
			throw new TaskletException(JPPFMessage.JEMJ032E.toMessage().getMessage(), e);
		} catch (JPPFMessageException e) {
			throw new TaskletException(e.getMessage(), e);
		}
		
		return RepeatStatus.FINISHED;
	}
	
	private TypedProperties loadProperties(JPPFBean bean) throws JPPFMessageException{
		TypedProperties props = new TypedProperties();
		
		/*----------------------+
		 | Load JPPF properties | 
		 +----------------------*/
		props.setProperty(Keys.JEM_JPPF_DISCOVERY_ENABLED, Boolean.FALSE.toString());
		
		if (bean.getAddress() != null){
			JPPFUtil.loadTypedProperties(props, bean.getAddress());
		}

		/*----------------------+
		 | Load JEM properties  | 
		 +----------------------*/
		if (bean.getDatasource() != null){
			props.setProperty(Keys.JEM_DATASOURCE, bean.getDatasource());
		}
		props.setProperty(Keys.JEM_RUNNABLE, bean.getRunnable());
		props.setProperty(Keys.JEM_TASK_NUMBER, String.valueOf(Math.max(1, bean.getParallelTaskNumber())));
		
		// both delimiter and delimiterString are not allowed
		if ((bean.getDelimiter() != null) && (bean.getDelimiterString() != null)){
			throw new JPPFMessageException(JPPFMessage.JEMJ027E);
		}

		
		// delimiter is not null but 
		// chunkable data description is nul throws an exception. Not Allowed.
		if ((bean.getDelimiter() != null || bean.getDelimiterString() != null) && bean.getChunkableDataDescription() == null){
			throw new JPPFMessageException(JPPFMessage.JEMJ013E);
		}
		// if there is data description for chunk
		// sets all properties 
		if (bean.getChunkableDataDescription() != null){
			containsDataDescription(bean.getChunkableDataDescription(), false);
			props.setProperty(Keys.JEM_CHUNKABLE_DATA_DESCRIPTION, bean.getChunkableDataDescription());
			if (bean.getDelimiter() != null){
				props.setProperty(Keys.JEM_DELIMITER, bean.getDelimiter());
			} else if (bean.getDelimiterString() != null){
				props.setProperty(Keys.JEM_DELIMITER_STRING, bean.getDelimiterString());
			}
		}
		// if there is data description for merging
		// sets all properties 
		if (bean.getMergedDataDescription() != null){
			containsDataDescription(bean.getMergedDataDescription(), true);
			props.setProperty(Keys.JEM_MERGED_DATA_DESCRIPTION, bean.getMergedDataDescription());
		}

		return props;
	}

	/**
	 * Checks if there is a data description with the name of chunk or merging data description 
	 * @param name name of data description to find
	 * @param write if must be check for an output or input
	 * @throws BuildException is not all controls end correctly 
	 */
	private void containsDataDescription(String name, boolean write) throws JPPFMessageException{
		boolean found = false;
		// scans data description
		for (DataDescription ddescription : getDataDescriptionList()){
			// is the same datadescritpion
			if (ddescription.getName().equalsIgnoreCase(name)){
				if (write){
					// mustn't be in shr
					if (ddescription.getDisposition().equalsIgnoreCase(Disposition.SHR)){
						throw new JPPFMessageException(JPPFMessage.JEMJ014E, name);
					}
				} else {
					// must be in shr
					if (!ddescription.getDisposition().equalsIgnoreCase(Disposition.SHR)){
						throw new JPPFMessageException(JPPFMessage.JEMJ015E, name);
					}
					// chunk works only with a single dataset 
					if (ddescription.isMultiDataset()){
						throw new JPPFMessageException(JPPFMessage.JEMJ016E, name);
					}
					// chunk works only with files
					DataSet ddataset = ddescription.getDatasets().iterator().next();
					if (ddataset.getDatasource()!= null){
						throw new JPPFMessageException(JPPFMessage.JEMJ017E, name);
					} else if (ddataset.isInline()) {
						throw new JPPFMessageException(JPPFMessage.JEMJ018E, name);
					}
				}
				found = true;
				break;
			}
		}
		if (!found){
			// not found
			throw new JPPFMessageException(JPPFMessage.JEMJ019E, name);
		}
	}
}
