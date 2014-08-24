package com.eric.forceengine;

import android.content.res.Resources;

/**
 * UI-related utilities.
 *
 * Created by Eric on 8/24/2014.
 */
public class UiUtils {

	public static final float getPxFromDp(float dp) {
		return dp * (Resources.getSystem().getDisplayMetrics().densityDpi / 160f);
	}
}
