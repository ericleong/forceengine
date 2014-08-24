package forceengine.objects;

/**
 * A representation of a 2-dimensional vector (as defined by physics), it is
 * related to the Java API <code>Vector</code> . Used by objects that have a
 * direction but no location (such as wind). Mainly used when returning values
 * (use <code>RectVector</code> or <code>PolarPoint</code> to create a
 * <code>Vector</code> reference). This vector is <i>not</i> tied to a location
 * like <code>PointVector</code>. Implementations can use either polar or
 * rectangular coordinates but must be able to translate from one system to
 * other.
 * 
 * @author Eric
 * @since Apr 15, 2008
 */
public interface Vector {
	/**
	 * Adds a vector to this vector.
	 * 
	 * @param v
	 *            The vector to be added.
	 * 
	 * @return The resultant vector.
	 */
	public Vector add(Vector v);

	/**
	 * Takes the dot product of this vector with another vector.
	 * 
	 * @param v
	 *            The vector to be multiplied.
	 * @return The resultant vector.
	 */
	public double dot(Vector v);

	/**
	 * Returns the angle of the x and y components when translating to polar
	 * coordinates.
	 * 
	 * @return The angle of the x and y components.
	 */
	public double getAngle();

	/**
	 * Gets the length of the <code>Vector</code>.
	 * 
	 * @return The length of the <code>Vector</code>.
	 */
	public double getLength();

	/**
	 * Gets the normal vector.
	 * 
	 * @return The normal vector.
	 */
	public Vector getUnitVector();

	/**
	 * Gets the x component.
	 * 
	 * @return The x component.
	 */
	public double getvx();

	/**
	 * Gets the y component.
	 * 
	 * @return The y component.
	 */
	public double getvy();

	/**
	 * Scales this vector.
	 * 
	 * @param scalar
	 *            The scalar to multiply this vector by.
	 * 
	 * @return The resultant vector.
	 */
	public Vector scale(double scalar);

	/**
	 * Subtracts the given vector from this vector.
	 * 
	 * @param v
	 *            The vector to subtract.
	 * @return The resultant vector.
	 */
	public Vector subtract(Vector v);

	/**
	 * Rotates this vector.
	 * 
	 * @param angle
	 *            The angle to rotate this vector by.
	 * @return The rotated vector.
	 */
	public Vector rotate(double angle);

	/**
	 * Sets the angle (in radians).
	 * 
	 * @param angle
	 *            The new angle (in radians).
	 */
	public void setAngle(double angle);

	/**
	 * Sets the length.
	 * 
	 * @param length
	 *            The new length.
	 */
	public void setLength(double length);

	/**
	 * Sets the values of this {@link Vector} to the given polar coordinates.
	 * 
	 * @param angle
	 *            The angle of the new vector.
	 * @param length
	 *            The length of the new vector.
	 */
	public void setPolar(double angle, double length);

	/**
	 * Sets the values of this {@link Vector} to the given rectangular
	 * coordinates.
	 * 
	 * @param vx
	 *            The x component of the new vector.
	 * @param vy
	 *            The y component of the new vector.
	 */
	public void setRect(double vx, double vy);

	/**
	 * Sets the values of this <code>Vector</code> to the values of another
	 * <code>Vector</code>. Does not guarantee equality.
	 * 
	 * @param v
	 *            The new values for this <code>Vector</code>.
	 */
	public void setVector(Vector v);

	/**
	 * Sets the x component.
	 * 
	 * @param vx
	 *            The new x component.
	 */
	public void setvx(double vx);

	/**
	 * Sets the y component.
	 * 
	 * @param vy
	 *            The new y component.
	 */
	public void setvy(double vy);
}
