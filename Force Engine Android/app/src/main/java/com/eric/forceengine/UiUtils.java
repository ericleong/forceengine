package com.eric.forceengine;

import android.content.res.Resources;
import android.graphics.Color;

import com.eric.forceengine.objects.ColoredForceCircle;

import forceengine.objects.ForceCircle;
import forceengine.physics.PhysicsEngine;

/**
 * UI-related utilities.
 *
 * Created by Eric on 8/24/2014.
 */
public class UiUtils {

	public static float getPxFromDp(float dp) {
		return dp * (Resources.getSystem().getDisplayMetrics().densityDpi / 160f);
	}

	/**
	 * @return a random color from the set of colors.
	 */
	public static int randomColor(PhysicsEngine engine) {
		int newColor, previousColor = Color.BLACK;

		if (engine != null && engine.getForceCircles() != null && engine.getForceCircles().size() > 1) {
			ForceCircle fc = engine.getForceCircle(engine.getForceCircles().size() - 1);
			if (fc instanceof ColoredForceCircle) {
				ColoredForceCircle c = (ColoredForceCircle) fc;
				previousColor = c.getColor();
			}
		}

		do {
			switch ((int) Math.round(Math.random() * 10) % 10) {
				case 0:
					newColor = Color.rgb(234, 253, 0);
					break;
				case 1:
					newColor = Color.rgb(76, 233, 0);
					break;
				case 2:
					newColor = Color.rgb(244, 0, 48);
					break;
				case 3:
					newColor = Color.rgb(152, 5, 200);
					break;
				case 4:
					newColor = Color.rgb(255, 113, 0);
					break;
				case 5:
					newColor = Color.rgb(255, 173, 0);
					break;
				case 6:
					newColor = Color.rgb(14, 64, 201);
					break;
				case 7:
					newColor = Color.rgb(20, 225, 160);
					break;
				case 8:
					newColor = Color.rgb(228, 0, 108);
					break;
				default:
					newColor = Color.rgb(0, 191, 255);
					break;
			}
		} while (newColor == previousColor);

		return newColor;
	}
}
