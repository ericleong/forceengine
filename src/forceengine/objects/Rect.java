package forceengine.objects;

import java.awt.Shape;

/**
 * A non rotating rectangle (though one can extend it and make it rotating)
 * 
 * @author Eric
 * @since Feb 28, 2008
 */
public interface Rect extends Shape {
	
	public double getHeight();

	public double getMinX();

	public double getMaxY();

	public double getMaxX();

	public double getMinY();

	public double getWidth();

	public double getX();

	public double getY();
	
	public Point getPoint();
	
	public void setPoint(Point p);
	
	/**
	 * Sets the x and y coordinates of the circle.
	 * 
	 * @param x
	 *            The new x coordinate.
	 * @param y
	 *            The new y coordinate.
	 */
	public void setPoint(double x, double y);

	public void setHeight(double height);

	public void setWidth(double width);

	public void setX(double x);

	public void setY(double y);
}
