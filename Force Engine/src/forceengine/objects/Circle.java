package forceengine.objects;

/**
 * A circle with rectangular coordinates (x, y) and a radius. Implement this
 * interface if an object is a circle.
 * 
 * @author Eric
 * @since Feb 28, 2008
 */
public interface Circle extends Rect {
	/**
	 * (double) getRadius
	 * 
	 * @return the radius
	 */
	public double getRadius();

	/**
	 * (double) getRadius
	 * 
	 * @return the radius squared (radius^2)
	 */
	public double getRadiusSq();


	/**
	 * Sets the radius of the circle.
	 * 
	 * @param radius
	 *            The new radius of this circle.
	 */
	public void setRadius(double radius);

}
