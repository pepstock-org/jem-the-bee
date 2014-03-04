/*******************************************************************************
 * Copyright (c) 2012-2014 pepstock.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andrea "Stock" Stocchero
 ******************************************************************************/
package org.pepstock.jem.plugin.util;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.pepstock.jem.plugin.views.jobs.inspector.InspectorLabelProvider;

/**
 * Utility to read and use images.
 * 
 * @author Andrea "Stock" Stocchero
 * @version 1.4
 */
public class Images {
	
	private static final String ICONS_FOLDER = "icons/";

	/**
	 * Image for FILE
	 */
	public static final Image FILE = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE); 

	/**
	 * Image for DIRECTORY
	 */
	public static final Image DIRECTORY = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER); 
	
	/**
	 * Image when client is not connected
	 */
	public static final Image OFFLINE_FAVICON = getImage("favicon_bw.png");
	
	/**
	 * Image when client is connected
	 */
	public static final Image ONLINE_FAVICON = getImage("favicon.png");
	
	/**
	 * Image for JOB general information link
	 */
	public static final Image GENERAL = getImage("information.gif");

	/**
	 * Image for JOB JCL link
	 */
	public static final Image JCL = getImage("ant_buildfile.gif");
	
	/**
	 * Image for JOB 
	 */
	public static final Image JOB = getImage("gears_64.png");
	
	/**
	 * Private constructor to avoid new instantiations 
	 */
	private Images() {
		
	}

	/**
	 * Reads a image from a file, using 'icons/' as icons folder.
	 * @param file file name of image
	 * @return image instance
	 */
	public static final Image getImage(String file) {
		Bundle bundle = FrameworkUtil.getBundle(InspectorLabelProvider.class);
		URL url = FileLocator.find(bundle, new Path(ICONS_FOLDER + file), null);
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();
	} 
	

}
