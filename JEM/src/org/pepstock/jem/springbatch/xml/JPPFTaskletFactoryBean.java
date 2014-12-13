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
package org.pepstock.jem.springbatch.xml;

import org.pepstock.jem.jppf.JPPFBean;
import org.pepstock.jem.jppf.JPPFTasklet;

/**
 * Factory bean for complex XML element for JPPF bean to confugure the JPPF connection<br>
 * it uses the <jppfConfiguration> tag to add this bean by extended XML authoring of SpringBatch
 * 
 * @author Andrea "Stock" Stocchero
 * @version 2.1
 */
public class JPPFTaskletFactoryBean extends TaskletFactoryBean {
	
	static final String JPPF_CONFIGURATION = "jppfConfiguration";

	private JPPFBean jppfConfiguration = null;

	/**
	 * @return the jppfConfiguration
	 */
	public JPPFBean getJppfConfiguration() {
		return jppfConfiguration;
	}

	/**
	 * @param jppfConfiguration the jppfConfiguration to set
	 */
	public void setJppfConfiguration(JPPFBean jppfConfiguration) {
		this.jppfConfiguration = jppfConfiguration;
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.springbatch.xml.TaskletFactoryBean#getObject()
	 */
	@Override
	public Object getObject() throws Exception {
		JPPFTasklet tasklet = (JPPFTasklet)super.getObject();
		if (jppfConfiguration != null){
			tasklet.setBean(jppfConfiguration);
		}
		return tasklet;
	}
}