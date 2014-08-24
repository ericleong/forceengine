/**
 * Force Engine 2
 * forceengine.objects
 * Line.java
 * 
 * Eric
 *
 * Jun 5, 2011
 * 7:56:03 PM
 */
package forceengine.objects;

import java.awt.Shape;
import java.awt.geom.Line2D;

/**
 * @author Eric
 * @see Line2D.Double
 */
public interface Line extends Shape {

	/**
	 * Returns the starting <code>Point</code> of this <code>Line</code>.
	 * 
	 * @return The starting <code>Point</code> of this <code>Line</code>.
	 */
	public Point getP1();

	/**
	 * Returns the end <code>Point</code> of this <code>Line</code>.
	 * 
	 * @return The end <code>Point</code> of this <code>Line</code>.
	 */
	public Point getP2();

	/**
	 * Returns the X coordinate of the start point in double precision.
	 * 
	 * @return The X coordinate of the start point in double precision.
	 */
	public double getX1();

	/**
	 * Returns the X coordinate of the end point in double precision.
	 * 
	 * @return The X coordinate of the end point in double precision.
	 */
	public double getX2();

	/**
	 * Returns the Y coordinate of the start point in double precision.
	 * 
	 * @return The Y coordinate of the start point in double precision.
	 */
	public double getY1();

	/**
	 * Returns the Y coordinate of the end point in double precision.
	 * 
	 * @return The Y coordinate of the end point in double precision.
	 */
	public double getY2();

	/**
	 * Sets the location of the endpoints of this Line2D to the specified double
	 * coordinates.
	 */
	public void setLine(double x1, double y1, double x2, double y2);
	
}
