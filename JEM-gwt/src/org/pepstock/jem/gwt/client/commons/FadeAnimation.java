package org.pepstock.jem.gwt.client.commons;

import java.math.BigDecimal;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.Element;

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