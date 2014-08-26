package com.eric.forceengine.objects;

import forceengine.objects.ForceCircle;

/**
 * A {@link forceengine.objects.ForceCircle} with {@link android.graphics.Color}.
 * <p/>
 * Created by Eric on 8/24/2014.
 */
public class ColoredForceCircle extends ForceCircle {

	private int mColor;

	public ColoredForceCircle(double x, double y, double radius, double mass) {
		super(x, y, radius, mass);
	}

	public ColoredForceCircle(double x, double y, double vx, double vy, double radius, double mass) {
		super(x, y, vx, vy, radius, mass);
	}

	public ColoredForceCircle(double x, double y, double vx, double vy, double radius, double mass, double restitution, int color) {
		super(x, y, vx, vy, radius, mass, restitution);

		mColor = color;
	}

	public int getColor() {
		return mColor;
	}
}
