package forceengine.objects;

import java.awt.geom.Point2D;


/**
 * The basic class of PhysicsEngine -<br \>
 * A rectangular coordinate with X and Y values stored as doubles and in the form (x, y). Very similar to
 * <code>Point2D.Double</code>, the only exception being that the <code>getX()</code> and <code>getY()</code> methods in
 * <code>Point2D.Double</code> return values of type <code>double</code>, whereas in this class they return values of
 * type <code>double</code>.
 * 
 * @author Eric
 * @since Feb 28, 2008
 */
@SuppressWarnings("serial")
public class Point extends Point2D.Double {
	/**
	 * Finds the midpoint between two points, in rectangular coordinates.
	 * 
	 * @param x1 The x value of the first point.
	 * @param y1 The y value of the first point.
	 * @param x2 The x value of the second point.
	 * @param y2 The y value of the second point.
	 * @return A <code>Point</code> with the coordinates of the midpoint of (x1, y1) and (x2, y2).
	 */
	public static final Point midpoint(double x1, double y1, double x2, double y2){
		return new Point(((x1 + x2) / 2), ((y1 + y2) / 2));
	}
	/**
	 * Finds the midpoint between two points.
	 * 
	 * @param p1 The first <code>Point</code>.
	 * @param p2 The second <code>Point</code>.
	 * @return The midpoint between <b>p1</b> and <b>p2</b>.
	 */
	public static final Point midpoint(Point p1, Point p2){
		return midpoint(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}
	
	/**
	 * Constructs a Point with the coordinates (0, 0).
	 */
	public Point(){
		x = 0;
		y = 0;
	}
	/**
	 * Constructs a Point with specified x and y values.
	 * 
	 * @param x The x value.
	 * @param y The y value.
	 */
	public Point(double x, double y){
		if(java.lang.Double.isNaN(x) || java.lang.Double.isInfinite(x))
			x = 0;
		if(java.lang.Double.isNaN(y) || java.lang.Double.isInfinite(y))
			y = 0;
		this.x = x;
		this.y = y;
	}
	public Point(Point p){
		this(p.getX(), p.getY());
	}
	/**
	 * Contstructs a <code>Point</code>, given a <code>Vector</code>. It takes the endpoint of the head of the vector if
	 * the tail of the vector is at (0, 0).
	 * 
	 * @param v The vector to be used.
	 */
	public Point(Vector v){
		this(v.getvx(), v.getvy());
	}

	@Override
	public boolean equals(Object obj){
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Point other = (Point)obj;
		if(java.lang.Double.doubleToLongBits(x) != java.lang.Double.doubleToLongBits(other.x))
			return false;
		if(java.lang.Double.doubleToLongBits(y) != java.lang.Double.doubleToLongBits(other.y))
			return false;
		return true;
	}
	/**
	 * Return's this object's location (included to match Point2D).
	 * 
	 * @return This object.
	 */
	public Point getLocation(){
		return this;
	}

	/**
	 * Returns the (1 - ) coefficient of restitution between this object and another object.
	 * <code>a.getRestitution(b)</code> does not necessarily have to equal <code>b.getRestitution(a)</code>, but the
	 * coefficient of restitution for <code>a</code> is determined by <code>a.getRestitution(b)</code> to avoid casting
	 * b. Default is 1.
	 * 
	 * @param o The <code>Object</code> to be checked against.
	 * @return The coefficient of restitution between this object and <b>o</b> if this object collides into <b>o</b>.
	 */
	public double getRestitution(Object o){
		return 1;
	}

	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public Point getPoint(){
		return this;
	}
	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = (int)(prime * result + java.lang.Double.doubleToLongBits(x));
		result = (int)(prime * result + java.lang.Double.doubleToLongBits(y));
		return result;
	}
	
	/**
	 * @return Whether or not this {@link Point} is alive.
	 */
	public boolean isAlive() {
		return true;
	}

	/**
	 * Checks if this object collides with another object. Default is <code>true</code>. <code>a.isCollide(b)</code> and
	 * <code>b.isCollide(a)</code> must be <code>true</code> for collision checking to go through.
	 * 
	 * @param o The <code>Object</code> to be checked against.
	 * @return Whether or not this object collides with <b>o</b>.
	 */
	public boolean isCollide(Object o){
		return true;
	}
	/**
	 * Checks if this object responds to collision with another object. Should not return <code>true</code> if
	 * <code>isCollide(<b>o</b>)</code> returns <code>false</code>. Default is <code>true</code>.
	 * <code>a.isResponsive(b)</code> does not have to equal <code>b.isResponsive(a)</code>, but the reaction of
	 * <code>a</code> depends on the value of <code>a.isResponsive(b)</code>.
	 * 
	 * @param o The <code>Object</code> to be checked against.
	 * @return Whether or not this object responds to <b>o</b>.
	 */
	public boolean isResponsive(Object o){
		return isCollide(o);
	}

	/**
	 * Converts the X and Y values into doubles and sets both of the X and Y values in one function.
	 * 
	 * @param x the new x value
	 * @param y the new y value
	 */
	public void setPoint(double x, double y){
		this.x = x;
		this.y = y;
	}
	/**
	 * Changes the x and y coordinates of this point to match those of the argument.
	 * 
	 * @param p The new coordinates.
	 */
	public void setPoint(Point p){
		this.x = p.getX();
		this.y = p.getY();
	}
	public void setX(double x){
		this.x = x;
	}
	public void setY(double y){
		this.y = y;
	}
	@Override
	public String toString(){
		return String.format("(%4.8f, %4.8f)", this.x, this.y);
	}
	public Point translate(double dx, double dy){
		setPoint(x + dx, y + dy);
		return this;
	}
	public Point translate(Vector v){
		return translate(v.getvx(), v.getvy());
	}
}
