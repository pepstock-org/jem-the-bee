/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012, 2013  Marco "Fuzzo" Cuccato
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
package org.pepstock.jem.gwt.client.commons;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;

/**
 * Style interface for {@link DefaultTablePager}
 * @author Marco "Fuzzo" Cuccato
 *
 */
public interface DefaultTablePagerResources extends ClientBundle {
	
	/**
	 * The instance
	 */
	DefaultTablePagerResources INSTANCE = GWT.create(DefaultTablePagerResources.class); 
	
	/**
	 * Custom styles
	 * @return return a custom style
	 */
	@Source("../resources/css/DefaultTablePager.css")
	CustomStyles styles();

	/**
	 * Style interface for background 
	 * @author Marco "Fuzzo" Cuccato
	 *
	 */
	interface CustomStyles extends CssResource {
		String background();
	}
	
	/**
	 * Custom resources needed by original GWT SimplePager
	 */
	interface Resources extends com.google.gwt.user.cellview.client.SimplePager.Resources {

		@ImageOptions(flipRtl = true)
		@Source("../resources/images/defaultpager/fastForward_enabled.png")
		ImageResource simplePagerFastForward();

		@Override
		@ImageOptions(flipRtl = true)
		@Source("../resources/images/defaultpager/fastForward_disabled.png")
		ImageResource simplePagerFastForwardDisabled();

		@Override
		@ImageOptions(flipRtl = true)
		@Source("../resources/images/defaultpager/firstPage_enabled.png")
		ImageResource simplePagerFirstPage();

		@Override
		@ImageOptions(flipRtl = true)
		@Source("../resources/images/defaultpager/firstPage_disabled.png")
		ImageResource simplePagerFirstPageDisabled();

		@Override
		@ImageOptions(flipRtl = true)
		@Source("../resources/images/defaultpager/lastPage_enabled.png")
		ImageResource simplePagerLastPage();

		@Override
		@ImageOptions(flipRtl = true)
		@Source("../resources/images/defaultpager/lastPage_disabled.png")
		ImageResource simplePagerLastPageDisabled();

		@Override
		@ImageOptions(flipRtl = true)
		@Source("../resources/images/defaultpager/nextPage_enabled.png")
		ImageResource simplePagerNextPage();

		@Override
		@ImageOptions(flipRtl = true)
		@Source("../resources/images/defaultpager/nextPage_disabled.png")
		ImageResource simplePagerNextPageDisabled();

		@Override
		@ImageOptions(flipRtl = true)
		@Source("../resources/images/defaultpager/previousPage_enabled.png")
		ImageResource simplePagerPreviousPage();

		@Override
		@ImageOptions(flipRtl = true)
		@Source("../resources/images/defaultpager/previousPage_disabled.png")
		ImageResource simplePagerPreviousPageDisabled();

		@Override
		@Source("../resources/css/Pager.css")
		Style simplePagerStyle();
		
	}
	
	/**
	 * Style interface for buttons and page details 
	 */
	interface Style extends com.google.gwt.user.cellview.client.SimplePager.Style {

		@Override
		String button();

		@Override
		String disabledButton();
		
		@Override
		String pageDetails();
	}
	
}