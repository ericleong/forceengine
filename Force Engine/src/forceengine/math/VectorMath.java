/*
 * VectorMath.java
 * Created on Oct 21, 2008 8:43:37 PM
 * By Eric
 */
package forceengine.math;

import forceengine.objects.StaticLine;
import forceengine.objects.Point;
import forceengine.objects.RectVector;
import forceengine.objects.Vector;

/**
 * Contains methods for vector manipulation.
 * 
 * @author Eric
 */
public class VectorMath {
	public static final Vector add(Vector v, Vector u) {
		return new RectVector(v.getvx() + u.getvx(), v.getvy() + u.getvy());
	}
	public static final Vector subtract(Vector v, Vector u) {
		return new RectVector(v.getvx() - u.getvx(), v.getvy() - u.getvy());
	}
	/**
	 * Computes the length of a vector.
	 * 
	 * @param vx The x component of the vector.
	 * @param vy The y component of the vector.
	 * @return The length of the vector.
	 */
	public static final double length(double vx, double vy){
		return Point.distance(0, 0, vx, vy);
	}
	/**
	 * Computes the dot product of two vectors.
	 * 
	 * @param u A <code>Vector</code>.
	 * @param v Another <code>Vector</code>.
	 * @return The dot product <b>u</b> and <b>v</b>.
	 */
	public static final double dotproduct(Vector u, Vector v){
		return dotproduct(u.getvx(), u.getvy(), v.getvx(), v.getvy());
	}
	/**
	 * Computes the dot product of two vectors.
	 * 
	 * @param vx1 The x component of the first vector.
	 * @param vy1 The y component of the first vector.
	 * @param vx2 The x component of the second vector.
	 * @param vy2 The y component of the second vector.
	 * @return The dot product of two vectors.
	 */
	public static final double dotproduct(double vx1, double vy1, double vx2, double vy2){
		return vx1 * vx2 + vy1 * vy2;
	}
	/**
	 * Computes the cosine of the smaller angle between the two vectors. Formula is <br>
	 * dotproduct(u, v)/|u||v|
	 * 
	 * @param u A <code>Vector</code>.
	 * @param v Another <code>Vector</code>.
	 * @return The cosine of the smaller angle between <b>u</b> and <b>v</b>.
	 */
	public static final double cosproj(Vector u, Vector v){
		return dotproduct(u, v) / (u.getLength() * v.getLength());
	}
	/**
	 * Computes the cosine of the smaller angle between the two vectors. Formula is <br>
	 * dotproduct(u, v)/|u||v|
	 * 
	 * @param vx1 The x component of the first vector.
	 * @param vy1 The y component of the first vector.
	 * @param vx2 The x component of the second vector.
	 * @param vy2 The y component of the second vector.
	 * @return The cosine of the smaller angle between the two vectors.
	 */
	public static final double cosproj(double vx1, double vy1, double vx2, double vy2){
		return dotproduct(vx1, vy1, vx2, vy2) / (length(vx1, vy1) * length(vx2, vy2));
	}
	/**
	 * Computes the angle between the two vectors. Uses dot product.
	 * 
	 * @param u A <code>Vector</code>.
	 * @param v Another <code>Vector</code>.
	 * @return The angle between <b>u</b> and <b>v</b>.
	 */
	public static final double anglebetween(Vector u, Vector v){
		return Math.acos(cosproj(u, v));
	}
	/**
	 * Computes the angle between the two vectors. Uses dot product.
	 * 
	 * @param vx1 The x component of the first vector.
	 * @param vy1 The y component of the first vector.
	 * @param vx2 The x component of the second vector.
	 * @param vy2 The y component of the second vector.
	 * @return The angle between the two vectors.
	 */
	public static final double anglebetween(double vx1, double vy1, double vx2, double vy2){
		return Math.acos(cosproj(vx1, vy1, vx2, vy2));
	}
	/**
	 * Computes the cross product of two 2-dimensional vectors.
	 * 
	 * @param u A <code>Vector</code>.
	 * @param v Another <code>Vector</code>.
	 * @return The cross product of <b>u</b> and <b>v</b>.
	 */
	public static final double crossproduct(Vector u, Vector v){
		return crossproduct(u.getvx(), u.getvy(), v.getvx(), v.getvy());
	}
	/**
	 * Computes the cross product of two 2-dimensional vectors.
	 * 
	 * @param vx1 The x component of the first vector.
	 * @param vy1 The y component of the first vector.
	 * @param vx2 The x component of the second vector.
	 * @param vy2 The y component of the second vector.
	 * @return Yhe cross product of two 2-dimensional vectors.
	 */
	public static final double crossproduct(double vx1, double vy1, double vx2, double vy2){
		return vx1 * vy2 - vy1 * vx2;
	}
	/**
	 * Computes the sine of the smaller angle between the two vectors. Formula is <br>
	 * crossproduct(u, v)/|u||v|
	 * 
	 * @param u A <code>Vector</code>.
	 * @param v Another <code>Vector</code>.
	 * @return The sine of the smaller angle between <b>u</b> and <b>v</b>.
	 */
	public static final double sinproj(Vector u, Vector v){
		return crossproduct(u, v) / (u.getLength() * v.getLength());
	}
	/**
	 * Computes the sine of the smaller angle between the two vectors. Formula is <br>
	 * crossproduct(u, v)/|u||v|
	 * 
	 * @param vx1 The x component of the first vector.
	 * @param vy1 The y component of the first vector.
	 * @param vx2 The x component of the second vector.
	 * @param vy2 The y component of the second vector.
	 * @return The sine of the smaller angle between the two vectors.
	 */
	public static final double sinproj(double vx1, double vy1, double vx2, double vy2){
		return crossproduct(vx1, vy1, vx2, vy2) / (length(vx1, vy1) * length(vx2, vy2));
	}
	
	public static final boolean equaldirection(double x1, double y1, double x2, double y2, double x3, double y3, double v1len, double v2len){
		//1 = common point between the two vectors
		//2 = where vector 1 points to
		//3 = where vector 2 points to
		//v1len = length of vector from 1 to 2
		//v2len = length of vetor from 1 to 3
		if(x1-x2 != 0 || y1-y2 != 0){ // check to make sure that x2 and y2 goes somewhere 
			double cos = (((x1-x2)*(x1-x3)+(y1-y2)*(y1-y3))/ (Math.abs(v1len)*Math.abs(v2len)));
			if(cos > .995 && cos <= 1) return true;
			else return false;
		}else return true;
	}
	public static final boolean equaldirection(double x1, double y1, double x2, double y2, double x3, double y3, double v1len, double v2len, double range){
		//range is how close the vector has to be to the other vector
		//0.999 would be basically equivalent to the other vector
		//-.999 would be 180 degrees
		//0 would be perpendicular
		if(x1-x2 != 0 || y1-y2 != 0){ // check to make sure that x2 and y2 goes somewhere 
			double cos = (((x1-x2)*(x1-x3)+(y1-y2)*(y1-y3))/ (Math.abs(v1len)*Math.abs(v2len)));
			if(cos > range && cos <= 1) return true;
			else return false;
		}else return true;
	}
	public static final boolean equaldirection(double vx1, double vy1, double vx2, double vy2, double v1len, double v2len, double range){
		//range is how close the vector has to be to the other vector
		//0.999 would be basically equivalent to the other vector
		//-.999 would be 180 degrees
		//0 would be perpendicular
		if(vx1 != 0 || vy1 != 0){ // check to make sure that x2 and y2 goes somewhere 
			double cos = (((vx1)*(vx2)+(vy1)*(vy2))/ (Math.abs(v1len)*Math.abs(v2len)));
			if(cos > range && cos <= 1) return true;
			else return false;
		}else return true;
	}
	public static final boolean equaldirection(double angle1, double angle2, double range){
		if(angle1 > Math.PI*2) angle1 -= Math.PI*2;
		else if(angle1 < 0) angle1 += Math.PI*2;
		if(angle2 > Math.PI*2) angle2 -= Math.PI*2;
		else if(angle2 < 0) angle2 += Math.PI*2;
		if(Math.abs(angle1-angle2) < range) return true;
		return false;
	}
	
	/**
     * Finds the shortest distance between a line and a point.
     * @param x1 The x value of the first point of the line.
     * @param y1 The y value of the first point of the line.
     * @param lx1 The x value of the second point of the line.
     * @param ly1 The y value of the second point of the line.
     * @param lx2 The x value of the point.
     * @param ly2 The y value of the point.
     * @return The shortest distance between a line and a point.
     */
    public static final double linepointdistance(double x1, double y1, double lx1, double ly1, double lx2, double ly2){
		Point p = closestpointonline(x1, y1, lx1, ly1, lx2, ly2);
		return (double)Point.distance(lx2, ly2, p.getX(), p.getY());
	}
    /**
     * gives you the shortest distance squared between a line and a point
     * @param x1 the x value of the first point of the line
     * @param y1 the y value of the first point of the line
     * @param x2 the x value of the second point of the line
     * @param y2 the y value of the second point of the line
     * @param x3 the x value of the point
     * @param y3 the y value of the point
     * @return the shortest distance squared between a line and a point
     */
    public static final double linepointdistancesq(double x1, double y1, double x2, double y2, double x3, double y3){
    	double A1 = (y2 - y1);
    	double B1 = (x1 - x2);
    	double C1 = (y2 - y1)*x1 + (x1 - x2)*y1;
    	double C2 = -B1*x3 + A1*y3;
    	double det = (A1*A1 - (-B1*B1));
    	double cx = 0;
    	double cy = 0;
    	if(det != 0){
	    	cx = (A1*C1 - B1*C2)/det;
	    	cy = (A1*C2 - -B1*C1)/det;
    	}
    	return Math.abs((cx-x3)*(cx-x3)+(cy-y3)*(cy-y3));
    }
    
    public static final Point closestpointonline(StaticLine l, Point p){
    	return closestpointonline(l.getX(), l.getY(), l.getX2(), l.getY2(), p.getX(), p.getY());
    }
    
	/**
	 * Finds a point on the given line closest to the given point.
	 * 
	 * @param lx1
	 *            The x value of the first point of the line.
	 * @param ly1
	 *            The y value of the first point of the line.
	 * @param lx2
	 *            The x value of the second point of the line.
	 * @param ly2
	 *            The y value of the second point of the line.
	 * @param x0
	 *            The x value of the point.
	 * @param y0
	 *            The y value of the point.
	 * @return A <code>Point</code> with the coordinates of the point on the
	 *         line closest to the given point.
	 */
	public static final Point closestpointonline(double lx1, double ly1,
			double lx2, double ly2, double x0, double y0) {
		double A1 = (ly2 - ly1);
		double B1 = (lx1 - lx2);
		double C1 = (ly2 - ly1) * lx1 + (lx1 - lx2) * ly1;
		double C2 = -B1 * x0 + A1 * y0;
		double det = (A1 * A1 - (-B1 * B1));
		double cx = 0;
		double cy = 0;
		if (det != 0) {
			cx = (double) ((A1 * C1 - B1 * C2) / det);
			cy = (double) ((A1 * C2 - -B1 * C1) / det);
		} else {
			cx = x0;
			cy = y0;
		}
		return new Point(cx, cy);
	}
	
	/**
	 * Gets the {@link Vector} from the <code>tail</code> {@link Point} to the <code>head</code> point.
	 * @param head the end of the <code>Vector</code>
	 * @param tail the beginning of the <code>Vector</code>
	 * @return the {@link Vector} from the <code>tail</code> to the <code>head</code>.
	 */
	public static final Vector getVector(Point head, Point tail){
		return new RectVector(head.getX() - tail.getX(), head.getY() - tail.getY());
	}
	
	/**
	 * Rotates a vector without changing it.
	 * 
	 * @param v
	 *            the vector to be rotated
	 * @param angle
	 *            the angle to rotate by
	 * @return The rotated vector.
	 */
	public static final Vector rotate(Vector v, double angle) {
		return new RectVector(v.getvx() * Math.cos(angle) - v.getvy()
				* Math.sin(angle), v.getvx() * Math.sin(angle) + v.getvy()
				* Math.cos(angle));
	}
}
