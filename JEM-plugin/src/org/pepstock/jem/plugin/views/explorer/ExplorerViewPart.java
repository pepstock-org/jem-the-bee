/*******************************************************************************
 * Copyright (C) 2012-2014 pepstock.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Enrico - initial API and implementation
 ******************************************************************************/
package org.pepstock.jem.plugin.views.explorer;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.pepstock.jem.gfs.GfsFile;
import org.pepstock.jem.gfs.GfsFileType;
import org.pepstock.jem.log.JemException;
import org.pepstock.jem.log.MessageLevel;
import org.pepstock.jem.node.security.Permissions;
import org.pepstock.jem.plugin.Client;
import org.pepstock.jem.plugin.util.Loading;
import org.pepstock.jem.plugin.util.Notifier;
import org.pepstock.jem.plugin.views.LoginViewPart;

/**
 * Is a viewPart, activated to show GFS explorer. It has got the environment
 * header to login and logoff to JEM environment.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class ExplorerViewPart extends LoginViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = ExplorerViewPart.class.getName();

	private TabFolder tabFolder;

	private List<ExplorerTableContainer> tabs = new LinkedList<ExplorerTableContainer>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.pepstock.jem.plugin.views.LoginViewPart#loadViewPart(org.eclipse.
	 * swt.widgets.TabFolder)
	 */
	@Override
	public void loadViewPart(TabFolder tabFolder) {
		// clear tabs
		tabs.clear();
		this.tabFolder = tabFolder;
		tabFolder.setToolTipText("Explorer of Global File System");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.plugin.views.JemViewPart#getId()
	 */
	@Override
	public String getId() {
		return ID;
	}

	/**
	 * Due to user can have just a subset of all tab item (related to data type
	 * of GFS), here tab panel is created adding the authorized tab item.
	 * 
	 * @param container
	 *            container of table
	 * @param permission
	 *            permission to check
	 */
	private void createTabItem(ExplorerTableContainer container, String permission) {
		// checks permission ONLY if is logged.
		// permissions are stored in LoggedUser
		if (Client.getInstance().isLogged() && Client.getInstance().isAuthorized(Permissions.GFS, permission)) {
			// creates columns for viewer
			container.createViewer();
			getSite().setSelectionProvider(container.getViewer());

			createContextMenu(container.getViewer());

			// creates tab item
			TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
			tabItem.setText(container.getName());
			tabItem.setToolTipText(container.getName() + " folder");
			tabItem.setControl(container.getComposite());
			tabs.add(container);
		}
	}

	/**
	 * Create the context menu for this view.
	 */
	protected void createContextMenu(TableViewer viewer) {
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager arg0) {
				fillContextMenu(menuMgr);
			}
		});
		// adds popup menu
		Control control = viewer.getControl();
		Menu menu = menuMgr.createContextMenu(control);
		control.setMenu(menu);
		getSite().registerContextMenu(menuMgr, getSite().getSelectionProvider());
	}

	/**
	 * Fill context menu depending on selected item. Invoked when the menu is
	 * about to be shown.
	 * 
	 * @param menu
	 *            Menu manager
	 */
	protected void fillContextMenu(IMenuManager menu) {
		// gets container
		ExplorerTableContainer container = tabs.get(tabFolder.getSelectionIndex());
		if (container.getType() == GfsFileType.DATA) {
			return;
		}

		// only for files is possible REMOVE
		IStructuredSelection selection = (IStructuredSelection) container.getViewer().getSelection();
		GfsFile file = (GfsFile) selection.getFirstElement();

		if (file.isDirectory()) {
			return;
		}
		menu.add(new DeleteAction(file, container));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pepstock.jem.plugin.views.LoginViewPart#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		// if is enabling, it must create the tab panel, checking authorization
		if (enabled) {
			// only if is logged
			if (Client.getInstance().isLogged()) {
				if (Client.getInstance().isAuthorized(Permissions.VIEW, Permissions.VIEW_GFS_EXPLORER)) {
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							// creates here the tabs based on authorization of
							// client
							createTabItem(new ExplorerTableContainer(tabFolder, SWT.SINGLE, GfsFileType.DATA), Permissions.GFS_DATA);
							createTabItem(new ExplorerTableContainer(tabFolder, SWT.SINGLE, GfsFileType.LIBRARY), Permissions.GFS_LIBRARY);
							createTabItem(new ExplorerTableContainer(tabFolder, SWT.SINGLE, GfsFileType.CLASS), Permissions.GFS_CLASS);
							createTabItem(new ExplorerTableContainer(tabFolder, SWT.SINGLE, GfsFileType.SOURCE), Permissions.GFS_SOURCES);
							createTabItem(new ExplorerTableContainer(tabFolder, SWT.SINGLE, GfsFileType.BINARY), Permissions.GFS_BINARY);
						}
					});
				} else {
					Notifier.showMessage(this, "Not auhtorized!", "You are not authorized to see any data on global file system.", MessageLevel.ERROR);
				}
			}
		} else {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					// clears and disposes everything
					tabs.clear();
					for (TabItem item : tabFolder.getItems()) {
						item.dispose();
					}
				}
			});
		}
	}

	/**
	 * Delete file from GFS action.
	 * 
	 * @author Andrea "Stock" Stocchero
	 * @version 2.1
	 */
	class DeleteAction extends Action {

		private GfsFile file = null;

		private ExplorerTableContainer container = null;

		/**
		 * Constructs the action using the file to remove and the container
		 * @param file file to remove
		 * @param container container GFS
		 */
		public DeleteAction(GfsFile file, ExplorerTableContainer container) {
			if (file == null) {
				setEnabled(false);
			} else {
				this.file = file;
			}
			this.container = container;
			setText("Remove " + file.getName());
		}

		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
        @Override
        public void run() {
        	//Uses a loading panel to delete file
			Loading loading = new Loading() {
				
				/* (non-Javadoc)
				 * @see org.pepstock.jem.plugin.util.Loading#getDisplay()
				 */
                @Override
                public Display getDisplay() {
					Control ctrl = container.getViewer().getControl();
					if (ctrl == null || ctrl.isDisposed()) {
						return null;
					}
					return ctrl.getDisplay();
                }

				/* (non-Javadoc)
				 * @see org.pepstock.jem.plugin.util.Loading#execute()
				 */
                @Override
                protected void execute() throws JemException {
                	// removes the file by REST
					try {
						Client.getInstance().delete(container.getType(), file.getLongName(), file.getDataPathName());
					} catch (JemException e) {
						Notifier.showMessage(getDisplay().getActiveShell(), "Unable to delete " + file.getName(), e.getMessage(), MessageLevel.ERROR);
					}

					// refreshes teh list of GFS files
					Control ctrl = container.getViewer().getControl();
					if (ctrl == null || ctrl.isDisposed()) {
						return;
					}
					ctrl.getDisplay().syncExec(new Runnable() {
						@Override
						public void run() {
							container.refresh();
						}
					});
				}
			};
			// performs the execute panels
			loading.run();
		}
	}

}
