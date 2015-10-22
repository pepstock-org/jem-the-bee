/**
    JEM, the BEE - Job Entry Manager, the Batch Execution Environment
    Copyright (C) 2012-2015   Marco "Fuzzo" Cuccato
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

import java.math.BigDecimal;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;

/**
 * Animation that provide fade in/out effect 
 * @author Marco "Fuzzo" Cuccato
 */
public class FadeAnimation extends Animation {

	private Element element;
	private double opacityIncrement;
	private double targetOpacity;
	private double baseOpacity;

	/**
	 * Build the animation and attach it to en element 
	 * @param element the element want to fade
	 */
	public FadeAnimation(Element element) {
		this.element = element;
	}

	@Override
	protected void onUpdate(double progress) {
		element.getStyle()
				.setOpacity(baseOpacity + progress * opacityIncrement);
	}

	@Override
	protected void onComplete() {
		super.onComplete();
		element.getStyle().setOpacity(targetOpacity);
	}

	/**
	 * Provide the animation
	 * @param duration the fade duration
	 * @param targetOpacityParm the target opacity
	 */
	public void fade(int duration, double targetOpacityParm) {
		double newTargetOpacity = targetOpacityParm;
		if (newTargetOpacity > 1.0) {
			newTargetOpacity = 1.0;
		}
		if (newTargetOpacity < 0.0) {
			newTargetOpacity = 0.0;
		}
		this.targetOpacity = newTargetOpacity;
		String opacityStr = element.getStyle().getOpacity();
		try {
			baseOpacity = new BigDecimal(opacityStr).doubleValue();
			opacityIncrement = targetOpacity - baseOpacity;
			run(duration);
		} catch (NumberFormatException e) {
			// set opacity directly
			onComplete();
		}
	}

}