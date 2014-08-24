package forceengine.physics;

import forceengine.objects.Force;
import forceengine.objects.Point;
import forceengine.objects.RectVector;
import forceengine.objects.Vector;

public class PhysicsMath {
	
	/**
	 * Calculates the gravitational attraction between two forces.
	 * 
	 * @param g
	 *            The gravitational constant (G), the value depends on the units
	 *            used.
	 * @param a
	 *            A force.
	 * @param b
	 *            The force <b>a</b> is attracted to.
	 * @return A vector with the gravitational attraction of <b>a</b> to
	 *         <b>b</b>. <b>b</b> to <b>a</b> is simply the equalibriant or the
	 *         negative vector.
	 */
	public static final Vector gravitationalAcceleration(double g, Force a, Force b) {
		return inverseSquaredAttraction(g * b.getMass(), a, b);
	}
	
	/**
	 * Calculates attraction between two forces.
	 * 
	 * @param multiplier
	 *            The gravitational constant (G), the value depends on the units
	 *            used.
	 * @param a
	 *            A force.
	 * @param b
	 *            The force <b>a</b> is attracted to.
	 * @return A vector with the attraction of <b>a</b> to
	 *         <b>b</b>. <b>b</b> to <b>a</b> is simply the equlibriant or the
	 *         negative vector.
	 */
	public static final Vector inverseSquaredAttraction(double multiplier, Point a, Point b) {
		if (a.getX() == b.getX() && a.getY() == b.getY())
			return new RectVector(0, 0);
		double distancesq = a.distanceSq(b);

		double g = multiplier / distancesq;
		double dist = Point.distance(a.getX(), a.getY(), b.getX(), b.getY());

		return new RectVector(g * (b.getX() - a.getX()) / dist, g * (b.getY() - a.getY()) / dist);
	}
	
	public static final Vector inverseAttraction(double multiplier, Point a, Point b) {
		if (a.getX() == b.getX() && a.getY() == b.getY())
			return new RectVector(0, 0);
		
		double dist = a.distance(b);
		double scale = multiplier / dist;
		
		return new RectVector(scale * (b.getX() - a.getX()) / dist, scale * (b.getY() - a.getY()) / dist);
	}

	/**
	 * Calculates attraction between two forces.
	 * 
	 * @param multiplier
	 *            The gravitational constant (G), the value depends on the units
	 *            used.
	 * @param a
	 *            A force.
	 * @param b
	 *            The force <b>a</b> is attracted to.
	 * @return A vector with the attraction of <b>a</b> to
	 *         <b>b</b>. <b>b</b> to <b>a</b> is simply the equlibriant or the
	 *         negative vector.
	 */
	public static final Vector linearAttraction(double multiplier, Point a, Point b) {
		double dist = Point.distance(a.getX(), a.getY(), b.getX(), b.getY());

		// gravitational force
		double g = multiplier * dist;
		
		// normalized vector from i to j
		double fx = (g * ((b.getX() - a.getX()) / dist));
		double fy = (g * ((b.getY() - a.getY()) / dist));
		return new RectVector(fx, fy);
	}
}
