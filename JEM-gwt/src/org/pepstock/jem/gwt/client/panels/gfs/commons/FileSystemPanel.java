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
package org.pepstock.jem.gwt.client.panels.gfs.commons;



import java.util.Collection;
import java.util.List;

import org.pepstock.jem.GfsFile;
import org.pepstock.jem.gwt.client.ResizeCapable;
import org.pepstock.jem.gwt.client.Sizes;
import org.pepstock.jem.gwt.client.commons.InspectListener;
import org.pepstock.jem.gwt.client.commons.Loading;
import org.pepstock.jem.gwt.client.commons.SearchListener;
import org.pepstock.jem.gwt.client.commons.ServiceAsyncCallback;
import org.pepstock.jem.gwt.client.commons.Styles;
import org.pepstock.jem.gwt.client.commons.Toast;
import org.pepstock.jem.gwt.client.panels.gfs.inspector.FileInspector;
import org.pepstock.jem.gwt.client.security.CurrentUser;
import org.pepstock.jem.gwt.client.security.PreferencesKeys;
import org.pepstock.jem.gwt.client.services.Services;
import org.pepstock.jem.log.MessageLevel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Nodes table container for nodes
 * 
 * @author Andrea "Stock" Stocchero
 *
 */
public abstract class FileSystemPanel extends GfsPanel implements SearchListener, InspectListener<GfsFile>, ResizeCapable {
	
	static {
		Styles.INSTANCE.common().ensureInjected();
	}
	
	private PathsSearcher searcher;
	
	private PathPanel pathPanel = new PathPanel();
	
	private FilesTableContainer files = null;
	
	private ScrollPanel scroller = null;
	
	private String preferenceKey = null;
	/**
	 * Creates panel with all views on files
	 * 
	 * @param preferencesKey key to extract preferences on files searching 
	 */
	public FileSystemPanel(String preferencesKey) {
		super();
		this.preferenceKey = preferencesKey;
		
		searcher = new PathsSearcher(preferencesKey);
		
		files = new FilesTableContainer(getFilesTable());
		scroller = new ScrollPanel(files);
		// add the always visible searcher
		add(searcher);
		searcher.setSearchListener(this);
		
		pathPanel.setWidth(Sizes.HUNDRED_PERCENT);
		add(pathPanel);
		pathPanel.setListener(this);

		// add the second row to the main panel
		add(scroller);
		files.getFilesTable().setInspectListener(this);
	}

	/**
	 * @return the preferenceKey
	 */
	public String getPreferenceKey() {
		return preferenceKey;
	}

	/**
	 * @param preferenceKey the preferenceKey to set
	 */
	public void setPreferenceKey(String preferenceKey) {
		this.preferenceKey = preferenceKey;
	}

	/**
	 * 
	 */
	public void load(){
		if (getPreferenceKey() != null){
			List<String> list = CurrentUser.getInstance().getListPreference(getPreferenceKey());
			if (!list.isEmpty()){
				search(list.get(0));
				return;
			}
		} 
		// do the first search
		search("*");		
	}
    
	/**
	 * @return
	 */
	public abstract FilesTable getFilesTable();
	
	/**
	 * @return
	 */
	public abstract int getFilesType();

	/**
	 * @return
	 */
	public abstract boolean isOverviewOnly();
	
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.InspectListener#inspect(java.lang.Object)
	 */
    @Override
    public void inspect(GfsFile file) {
    	if (file.isDirectory()){
    		search(file);
    		savePreference(file);
    	} else {
    		if (!isOverviewOnly()){
    			loadFile(file);
    		}
    	}
    }
    
	/**
	 * Saves user preference
	 */
	private void savePreference(GfsFile file){
		if (getPreferenceKey() != null){
			List<String> list = CurrentUser.getInstance().getListPreference(getPreferenceKey());
			if (list.isEmpty()){
				CurrentUser.getInstance().setListPreference(getPreferenceKey(), list);
			}
			if (!list.contains(file.getLongName())){
				list.add(0, file.getLongName());
				if (list.size() > PreferencesKeys.DEFAULT_MAXIMUM_PREFERENCES){
					list.remove(list.size()-1);
				}
				CurrentUser.getInstance().setLastUpdateTime();
			} else {
				list.remove(file.getLongName());
				list.add(0, file.getLongName());
				CurrentUser.getInstance().setLastUpdateTime();
			}
		}
	}
    
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.commons.SearchListener#search(java.lang.String)
	 */
    @Override
    public void search(String filter) {
    	GfsFile file = new GfsFile();
    	file.setLongName(filter);
    	file.setName(filter);
    	file.setDirectory(true);
    	inspect(file);
    }
    
    /**
     * @param file
     */
    public void search(final GfsFile file) {
    	final String filter = file.getLongName();
		searcher.setEnabled(false);
    	Loading.startProcessing();
   	
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.GFS_MANAGER.getFilesList(getFilesType(), "*".equalsIgnoreCase(filter) ? "." : filter, new GetFilesListAsyncCallback(file));
			}
	    });
    }

    private class GetFilesListAsyncCallback extends ServiceAsyncCallback<Collection<GfsFile>> {
    	
    	private final GfsFile file;
    	
    	public GetFilesListAsyncCallback(final GfsFile file) {
    		this.file = file;
    	}
    	
    	@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Get File system error!").show();
		}

		@Override
		public void onJemSuccess(Collection<GfsFile> result) {
			// sets data to table to show it
			files.getFilesTable().setRowData(result);
			pathPanel.set(file, result.size());
		}

		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
			searcher.setEnabled(true);
        }
    }
    
    /**
     * @param file
     */
    public void loadFile(final GfsFile file) {
    	
		searcher.setEnabled(false);
    	Loading.startProcessing();
    	
	    Scheduler scheduler = Scheduler.get();
	    scheduler.scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				Services.GFS_MANAGER.getFile(getFilesType(), file.getLongName(), new GetFileAsyncCallback(file));
			}
	    });
    }
    
    private class GetFileAsyncCallback extends ServiceAsyncCallback<String> {
    	
    	private final GfsFile file;
    	
    	public GetFileAsyncCallback(final GfsFile file) {
    		this.file = file;
    	}
    	
		@Override
		public void onJemFailure(Throwable caught) {
			new Toast(MessageLevel.ERROR, caught.getMessage(), "Get File system error!").show();
		}

		@Override
		public void onJemSuccess(String result) {
			// sets data to table to show it
			FileInspector inspector = new FileInspector(file.getLongName(), result);
			inspector.setModal(true);
			inspector.setTitle(file.getLongName());
			inspector.center();
		}

		@Override
        public void onJemExecuted() {
			Loading.stopProcessing();
			searcher.setEnabled(true);
        }
    }
    
	/* (non-Javadoc)
	 * @see org.pepstock.jem.gwt.client.ResizeCapable#onResize(int, int)
	 */
    @Override
    public void onResize(int availableWidth, int availableHeight) {
    	super.onResize(availableWidth, availableHeight);
    	
    	// removes 2 spacing of secondRow
    	int height = getHeight() - 
    			Sizes.SEARCHER_WIDGET_HEIGHT - 
    			Sizes.SEARCHER_WIDGET_HEIGHT;
    	
    	scroller.setSize(Sizes.toString(getWidth()), Sizes.toString(height));
    }

}