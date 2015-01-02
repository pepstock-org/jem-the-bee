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
package org.pepstock.jem.gwt.client.panels.jobs.commons.inspector;

import java.util.List;

import org.pepstock.jem.Job;
import org.pepstock.jem.OutputFileContent;
import org.pepstock.jem.OutputListItem;
import org.pepstock.jem.OutputTree;
import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.JemConstants;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.editor.SyntaxHighlighter;
import org.pepstock.jem.gwt.client.editor.viewers.TextViewer;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Component which shows output produced by job. Uses highlighter in PLAIn to show data.<br>
 * Every file is requested by RPC only when the user asks for. 
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.0	
 *
 */
public class Output extends SplitLayoutPanel implements SyntaxHighlighter, ResizeCapable{
	
	static {
		Styles.INSTANCE.common().ensureInjected();
		Styles.INSTANCE.administration().ensureInjected();
	}

	private final VerticalPanel log = new VerticalPanel();
	
	private final ScrollPanel scrollerFiles = new ScrollPanel();
	
	private TextViewer viewOutput = new TextViewer();
	
    private HorizontalPanel selected = null;
    
    private boolean resized = false;
    
	/**
	 * Constructs UI using the job that is in inspect mode, and the tree of produced output 
	 * 
	 * @param job job instance in inspect mode 
	 * @param outputTree the tree of produced output
	 * 
	 */
	public Output(Job job, OutputTree outputTree) {
		final Job thisJob = job;
		
		// Root of output tree
		DisclosurePanel root = new DisclosurePanel(job.getName());
		root.setAnimationEnabled(true);
		
		// this is the inside panel
		VerticalPanel firstLevelPanel = new VerticalPanel();
		firstLevelPanel.setSpacing(1);
		// scans first level
	    for (OutputListItem item : outputTree.getFirstLevelItems()){

			final HorizontalPanel options = new HorizontalPanel();
			options.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			options.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			options.setSpacing(8);
			options.setWidth(Sizes.HUNDRED_PERCENT);

	    	// create a item anchor for all elements of tree
			final ItemAnchor anchor = new ItemAnchor(item);

			anchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					// asks to load and show the output
					output(thisJob, anchor.getItem());
					selectPanel(options);
				}
			});
			options.add(anchor);
			options.setCellWidth(anchor, Sizes.HUNDRED_PERCENT);
			firstLevelPanel.add(options);
	    }
	    
	    root.setOpen(true);
	    
	    // subpanel
	    VerticalPanel secondAndFirstLevelPanel = new VerticalPanel();
	    secondAndFirstLevelPanel.add(firstLevelPanel);
	    
	    for (List<OutputListItem> items : outputTree.getSecondLevelItems()){
	    	if (!items.isEmpty()){
	    		String key = items.get(0).getParent();
	    		// for every step creates a disclosure panel
	    		DisclosurePanel secondPanel = new DisclosurePanel(key);
	    		secondPanel.setAnimationEnabled(true);

	    		// lists of anchor with produced output 
	    		VerticalPanel secondLevelPanel = new VerticalPanel();
	    		secondLevelPanel.setSpacing(1);
	    		// scans items
	    		for (OutputListItem item : items){
	    			final HorizontalPanel options = new HorizontalPanel();
	    			options.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
	    			options.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	    			options.setSpacing(8);
	    			options.setWidth(Sizes.HUNDRED_PERCENT);

	    			// create a item anchor for all elements of tree
	    			final ItemAnchor anchor = new ItemAnchor(item);
	    			anchor.addClickHandler(new ClickHandler() {
	    				@Override
	    				public void onClick(ClickEvent event) {
	    					// asks to load and show the output
	    					output(thisJob, anchor.getItem());
	    					selectPanel(options);
	    				}
	    			});

	    			options.add(anchor);
	    			options.setCellWidth(anchor, Sizes.HUNDRED_PERCENT);

	    			secondLevelPanel.add(options);
	    		}
	    		secondPanel.setContent(secondLevelPanel);
	    		secondAndFirstLevelPanel.add(secondPanel);
	    	}
	    }
	    root.setContent(secondAndFirstLevelPanel);
	    
	    // west panel with a disclosure
	    scrollerFiles.add(root);
	    scrollerFiles.setWidth(Sizes.toString(Sizes.SPLIT_PANEL_WEST_DEFAULT_SIZE));

		log.add(viewOutput);
		log.setVisible(false);
		
		addWest(scrollerFiles, Sizes.SPLIT_PANEL_WEST_DEFAULT_SIZE);
		add(log);
	}
	
	private void selectPanel(HorizontalPanel select){
		if (select.equals(selected)) {
			return;
		}
		if (selected != null){
			selected.setStyleName(Styles.INSTANCE.common().adminUnselectedTreeItem());
		}
		select.setStyleName(Styles.INSTANCE.common().adminSelectedTreeItem());
		selected = select;
	}


	/**
	 * Calls remotely a service to have the output data of a item.
	 * 
	 * @param job job instance in inspect
	 * @param item output item to show
	 * @see org.pepstock.jem.gwt.client.panels.jobs.commons.JobInspectListener#inspect(org.pepstock.jem.Job)
	 */
	public void output(final Job job, final OutputListItem item) {
		Loading.startProcessing();
		log.setVisible(true);
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.QUEUES_MANAGER.getOutputFileContent(job, item, new GetOutputFileContentAsyncCallback());
			}
	    });
	}

	private class GetOutputFileContentAsyncCallback extends ServiceAsyncCallback<OutputFileContent> {

		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Get OUTPUT error!").show();
		}

		@Override
		public void onJemSuccess(OutputFileContent result) {
			// creates the label of output
			String content = null;
			// uses highligther to show the data in PLAIN way
			if (result.getContent() ==  null){
				content = JemConstants.NONE_BRACKETS;
			} else if (result.getContent().trim().length() == 0){
				content = JemConstants.NONE_BRACKETS;
			} else {
				content = result.getContent();
			}
			
			viewOutput.setContent(content);
			viewOutput.startEditor();
		}

		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
        }
		
	}
	
    @Override
    public void onResize() {
    	super.onResize();
    	resized = getWidgetSize(scrollerFiles).intValue() != Sizes.SPLIT_PANEL_WEST_DEFAULT_SIZE;
        onResize(getOffsetWidth(), getOffsetHeight());
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	setHeight(Sizes.toString(availableHeight));
    	setWidth(Sizes.toString(availableWidth));
    	scrollerFiles.setHeight(Sizes.toString(availableHeight));
    	if (resized){
    		scrollerFiles.setWidth(Sizes.toString(getWidgetSize(scrollerFiles).intValue()));
    	}
    	int syntaxHighlighterWidth = availableWidth - 
    			( resized ? getWidgetSize(scrollerFiles).intValue() : Sizes.SPLIT_PANEL_WEST_DEFAULT_SIZE) -
    			Sizes.SPLIT_PANEL_SEPARATOR;
    	int syntaxHighlighterHeight = availableHeight - Sizes.MAIN_TAB_PANEL_PADDING_BOTTOM * 2;
    	viewOutput.onResize(syntaxHighlighterWidth, syntaxHighlighterHeight);
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.SyntaxHighlighter#startEditor()
	 */
    @Override
    public void startEditor() {
	    // do nothing
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.SyntaxHighlighter#destroyEditor()
	 */
    @Override
    public void destroyEditor() {
	   viewOutput.destroyEditor();
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.SyntaxHighlighter#getContent()
	 */
    @Override
    public String getContent() {
	    return null;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.SyntaxHighlighter#setContent(java.lang.String)
	 */
    @Override
    public void setContent(String content) {
	    // do nothing
    }
    
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.SyntaxHighlighter#isChanged()
	 */
    @Override
    public boolean isChanged() {
	    return false;
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.SyntaxHighlighter#setChanged(boolean)
	 */
    @Override
    public void setChanged(boolean changed) {
    	// do nothing
    }

	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.editor.SyntaxHighlighter#isEditorStarted()
	 */
    @Override
    public boolean isEditorStarted() {
	    return false;
    }

}
