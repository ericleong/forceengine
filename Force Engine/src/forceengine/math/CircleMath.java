package forceengine.math;

import java.util.ArrayList;

import forceengine.objects.Circle;
import forceengine.objects.StaticCircle;

public class CircleMath {
	/**
	 * checks whether or not 2 circles have collided with each other
	 * 
	 * @param circle1
	 *            the first circle
	 * @param circle2
	 *            the second circle
	 * @return whether or not they have collided
	 */
	public static final boolean checkcirclecollide(Circle circle1,
			Circle circle2) {
		return checkcirclecollide(circle1.getX(), circle1.getY(),
				circle1.getRadius(), circle2.getX(), circle2.getY(),
				circle2.getRadius());
	}

	/**
	 * checks whether or not 2 circles have collided
	 * 
	 * @param x1
	 *            the x value of the first circle
	 * @param y1
	 *            the y value of the first circle
	 * @param radius1
	 *            the radius of the first circle
	 * @param x2
	 *            the x value of the second circle
	 * @param y2
	 *            the y value of the second circle
	 * @param radius2
	 *            the radius of the second circle
	 * @return whether or not they have collided
	 */
	public static final boolean checkcirclecollide(double x1, double y1,
			double radius1, double x2, double y2, double radius2) {
		return Math.abs((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) < (radius1 + radius2)
				* (radius1 + radius2);
	}

	/**
	 * Checks whether or not an array of Circles has collided with a circle.
	 * 
	 * @param circles
	 *            The <code>ArrayList</code> of <code>Circle</code>.
	 * @param circle
	 *            The <code>Circle</code> that collision is to be done with.
	 * @return The index of the <code>Circle</code> in circles that collided
	 *         with circle.
	 */
	public static final ArrayList<Integer> checkcirclescollide(
			ArrayList<Circle> circles, Circle circle) {
		ArrayList<Integer> collisionindex = new ArrayList<Integer>(
				(int) Math.ceil(circles.size() / 10));
		for (int i = 0; i < circles.size(); i++) {
			if (checkcirclecollide(circles.get(i), circle)) { // they
																// intersect
				collisionindex.add(i);
			}
		}
		return collisionindex;
	}

	/**
	 * Checks whether or not an array of StaticCircles has collided with a
	 * circle.
	 * 
	 * @param circles
	 *            The <code>ArrayList</code> of <code>StaticCircle</code>.
	 * @param circle
	 *            The <code>StaticCircle</code> that collision is to be done
	 *            with.
	 * @return The index of the <code>StaticCircle</code> in circles that
	 *         collided with circle.
	 */
	public static final ArrayList<Integer> checkstaticcirclescollide(
			ArrayList<StaticCircle> circles, Circle circle) {
		ArrayList<Integer> collisionindex = new ArrayList<Integer>(
				(int) Math.ceil(circles.size() / 10));
		for (int i = 0; i < circles.size(); i++) {
			if (checkcirclecollide(circles.get(i), circle)) { // they intersect
				collisionindex.add(i);
			}
		}
		return collisionindex;
	}
}
