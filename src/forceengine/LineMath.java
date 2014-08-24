package forceengine;

import java.awt.geom.Line2D;

public class LineMath {

	/**
	 * checks if 2 lines have collided
	 * 
	 * @param x1
	 *            x value of first point on the first line
	 * @param y1
	 *            y value of first point on the first line
	 * @param x2
	 *            x value of second point on the first line
	 * @param y2
	 *            y value of second point on the first line
	 * @param x3
	 *            x value of first point on the second line
	 * @param y3
	 *            y value of first point on the second line
	 * @param x4
	 *            x value of second point on the second line
	 * @param y4
	 *            y value of second point on the second line
	 * @return whether or not the lines have collided
	 */
	public static final boolean checklinescollide(double x1, double y1,
			double x2, double y2, double x3, double y3, double x4, double y4) {
		return Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4);
	}

	/**
	 * Checks if 2 lines have collided.
	 * 
	 * @param x1
	 *            x value of first point on the first line
	 * @param y1
	 *            y value of first point on the first line
	 * @param x2
	 *            x value of second point on the first line
	 * @param y2
	 *            y value of second point on the first line
	 * @param thickness1
	 *            thickness of the first line (width/2)
	 * @param x3
	 *            x value of first point on the second line
	 * @param y3
	 *            y value of first point on the second line
	 * @param x4
	 *            x value of second point on the second line
	 * @param y4
	 *            y value of second point on the second line
	 * @param thickness2
	 *            thickness of the second line (width/2)
	 * @return whether 2 lines have collided or not
	 */
	public static final boolean checklinescollide(double x1, double y1,
			double x2, double y2, double thickness1, double x3, double y3,
			double x4, double y4, double thickness2) {
		double A1 = y2 - y1;
		double B1 = x1 - x2;
		double C1 = A1 * x1 + B1 * y1;
		double A2 = y4 - y3;
		double B2 = x3 - x4;
		double C2 = A2 * x3 + B2 * y3;
		double det = A1 * B2 - A2 * B1;
		if (det != 0) {
			double x = (B2 * C1 - B1 * C2) / det;
			double y = (A1 * C2 - A2 * C1) / det;
			if (x >= Math.min(x1, x2) - thickness1
					&& x <= Math.max(x1, x2) + thickness1
					&& x >= Math.min(x3, x4) - thickness2
					&& x <= Math.max(x3, x4) + thickness2
					&& y >= Math.min(y1, y2) - thickness1
					&& y <= Math.max(y1, y2) + thickness1
					&& y >= Math.min(y3, y4) - thickness2
					&& y <= Math.max(y3, y4) + thickness2)
				return true;
		}
		return false;
	}
}
