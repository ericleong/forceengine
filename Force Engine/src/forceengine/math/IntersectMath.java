package forceengine.math;

import forceengine.objects.Circle;
import forceengine.objects.StaticLine;

public class IntersectMath {
	/**
	 * checks whether or not a circle and a line have intersected
	 * 
	 * @param circle
	 *            the circle
	 * @param line
	 *            the line
	 * @return whether or not they have intersected
	 */
	public static final boolean checkcirclelinecollide(Circle circle,
			StaticLine line) {
		return checkcirclelinecollide(circle.getX(), circle.getY(),
				circle.getRadius(), line);
	}

	public static final boolean checkcirclelinecollide(double x, double y,
			double radius, StaticLine line) {
		double A1 = (line.getY2() - line.getY1());
		double B1 = (line.getX1() - line.getX2());
		double C1 = (line.getY2() - line.getY1()) * line.getX1()
				+ (line.getX1() - line.getX2()) * line.getY1();
		double C3 = -B1 * x + A1 * y;
		double det2 = (A1 * A1 - -B1 * B1);
		double cx2 = 0;
		double cy2 = 0;
		if (det2 != 0) {
			cx2 = (A1 * C1 - B1 * C3) / det2;
			cy2 = (A1 * C3 - -B1 * C1) / det2;
		}
		if (Math.min(line.getX1(), line.getX2()) <= cx2
				&& cx2 <= Math.max(line.getX1(), line.getX2())
				&& Math.min(line.getY1(), line.getY2()) <= cy2
				&& cy2 <= Math.max(line.getY1(), line.getY2())) {
			if (Math.abs((cx2 - x) * (cx2 - x) + (cy2 - y) * (cy2 - y)) < radius
					* radius + 1) { // line has thickness
				return true; // the second you find a collision, report
								// it
			}
		}
		return false;
	}

	/**
	 * checks whether or not a line and a circle have collided
	 * 
	 * @param x
	 *            the x value of the circle
	 * @param y
	 *            the y value of the circle
	 * @param radius
	 *            the radius of the circle
	 * @param x1
	 *            the x value of the first point
	 * @param y1
	 *            the y value of the first point
	 * @param x2
	 *            the x value of the second point
	 * @param y2
	 *            the y value of the second point
	 * @return whether or not the circle and the line have collided
	 */
	public static final boolean checkcirclelinecollide(double x, double y,
			double radius, double x1, double y1, double x2, double y2) {
		double A1 = (y2 - y1);
		double B1 = (x1 - x2);
		double C1 = (y2 - y1) * x1 + (x1 - x2) * y1;
		double C3 = -B1 * x + A1 * y;
		double det2 = (A1 * A1 - -B1 * B1);
		double cx2 = 0;
		double cy2 = 0;
		if (det2 != 0) {
			cx2 = (A1 * C1 - B1 * C3) / det2;
			cy2 = (A1 * C3 - (-B1 * C1)) / det2;
		}
		if (Math.min(x1, x2) <= cx2 && cx2 <= Math.max(x1, x2)
				&& Math.min(y1, y2) <= cy2 && cy2 <= Math.max(y1, y2)) {
			if (Math.abs((cx2 - x) * (cx2 - x) + (cy2 - y) * (cy2 - y)) < radius
					* radius + 1) { // line has thickness
				return true; // the second you find a collision, report it
			}
		}
		return false;
	}
}
