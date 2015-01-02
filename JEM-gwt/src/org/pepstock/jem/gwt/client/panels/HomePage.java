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
package org.pepstock.jem.gwt.client.panels;

import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.Images;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Home entry point
 * 
 * @author Andrea "Stock" Stocchero
 * 
 */
public class HomePage extends ScrollPanel implements ResizeCapable {
	/**
	 * Empty constructor
	 */

	private static final String BATCH_HTML = AbstractImagePrototype.create(Images.INSTANCE.batch()).getHTML().replaceAll(">", " align='left'>");

	private static final String DOS_BATCH_HTML = AbstractImagePrototype.create(Images.INSTANCE.dosbatch()).getHTML().replaceAll(">", " align='left'>");

	private static final String XML_HTML = AbstractImagePrototype.create(Images.INSTANCE.xml()).getHTML().replaceAll(">", " align='left'>");

	private static final String JEM_HTML = AbstractImagePrototype.create(Images.INSTANCE.jemhome()).getHTML().replaceAll(">", " align='right'>");

	private static final String WHATSBEE = "<h2>What's a BEE (batch execution environment)?</h2>" +

	BATCH_HTML + "<p valign='top'>Core applications are usually performed through batch processing, which involves executing one or more batch jobs in a "
	        + "sequential flow.  The job entry manager (<b>JEM</b>) helps receive jobs, schedule them for processing, and determine how " 
	        + "job output is processed."
	        + "<p>Many batch jobs are run in parallel and JCL is used to control the operation of each job. Correct use of JCL parameters "
	        + "allows parallel, asynchronous execution of jobs that may need access to the same data sets. One goal of an JEM is to "
	        + "process work while making the best use of system resources. To achieve this goal, resource management is needed during " 
	        + "key phases to do the following: <br>"
	        + "<ul><li>Before job processing, reserve input and output resources for jobs.</li>" + "<li>During job processing, control step execution and standardize output</li>"
	        + "<li>After job processing, free all resources used by the completed jobs, making the resources available to other jobs</li></ul></p>";

	private static final String WHATSJOB = "<h2>What's a JOB?</h2>" +

	DOS_BATCH_HTML + "<p align='top'>Batch processing is execution of a series of programs (</i>jobs</i>) on a computer without manual intervention.<p>" +

	"<p>Jobs are set up so they can be run to completion without manual intervention, so all input data are preselected through "
	        + "scripts or command-line parameters. This is in contrast to <i>online</i> or interactive programs which prompt the user for "
	        + "such input. A program takes a set of data files as input, processes the data, and produces a set of output data files. "
	        + " This operating environment is termed as <b>batch processing</b> because the input data are collected into batches of files " 
	        + "and are processed in batches by the program.</p>";

	private static final String WHATSJCL = "<h2>What's a JCL? </h2>" +

	XML_HTML + "<p align='top'><b>Job Control Language (JCL)</b> is a scripting language to instruct the system on how to run a batch job. "
	        + "In JCL the unit of work is the job. A job consists of one or several steps, each of which is a request to run one specific program.</p>";

	private static final String WHATSJEM = "<h2>What's JEM, the BEE?</h2>" +

	JEM_HTML + "<p valign='top'><b>JEM, the BEE</b> is java and cloud application which implements a batch execution environment " 
			+ "which is able to manage the execution of job, described by a jcl, as following:</p>" 
			+ "<p><ul>"
	        + "<li> collecting all outputs produced by job</li>"
	        + "<li> providing a all necessary commands to control the job</li>" + "<li> providing a global resource systems</li>" 
	        + "<li> providing GDG implementation</li>"
	        + "<li> managing different JCLs</li>" 
	        + "<li> providing a cross platform execution</li>" 
	        + "<li> providing a cross programming languages</li>" 
	        + "<li> providing a intelligent dispatching by affinities</li></ul></p>";

	/**
	 * Constructs the panel with image
	 */
	public HomePage() {
		HorizontalPanel subcomponent = new HorizontalPanel();
		subcomponent.setSpacing(10);

		VerticalPanel pp1 = new VerticalPanel();
		pp1.add(new HTML(WHATSBEE));
		pp1.add(new HTML(WHATSJCL));

		subcomponent.add(pp1);

		VerticalPanel pp2 = new VerticalPanel();
		pp2.add(new HTML(WHATSJOB));
		pp2.add(new HTML(WHATSJEM));

		subcomponent.add(pp2);

		add(subcomponent);
	}

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
		setSize(Sizes.toString(availableWidth), Sizes.toString(availableHeight));
    }
}