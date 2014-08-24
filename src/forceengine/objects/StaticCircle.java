package forceengine.objects;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

@SuppressWarnings("serial")
public class StaticCircle extends Point implements Circle {
	protected double radius;
	protected double radiussq;

	public StaticCircle(double x, double y, double radius){
		super(x, y);
		this.radius = radius;
		this.radiussq = radius * radius;
	}
	public StaticCircle(Point p, double radius){
		super(p);
		setRadius(radius);
	}

	@Override
	public boolean contains(double x, double y) {
		return this.distanceSq(x, y) <= radiussq;
	}
	@Override
	public boolean contains(double x, double y, double w, double h) {
		return contains(x, y) && contains(x, y + h) && contains(x + w, y)
				&& contains(x + w, y + h);
	}
	@Override
	public boolean contains(Point2D p) {
		return p.distanceSq(this) <= radiussq;
	}
	@Override
	public boolean contains(Rectangle2D r) {
		return contains(new Point2D.Double(r.getMinX(), r.getMinY()))
				&& contains(new Point2D.Double(r.getMinX(), r.getMaxY()))
				&& contains(new Point2D.Double(r.getMaxX(), r.getMinY()))
				&& contains(new Point2D.Double(r.getMaxX(), r.getMinY()));
	}
	@Override
	public boolean equals(Object obj){
		if(this == obj)
			return true;
		if(!super.equals(obj))
			return false;
		if(getClass() != obj.getClass())
			return false;
		StaticCircle other = (StaticCircle)obj;
		if(java.lang.Double.doubleToLongBits(radius) != java.lang.Double.doubleToLongBits(other.radius))
			return false;
		return true;
	}
	@Override
	public Rectangle getBounds() {
		return new Rectangle((int)(x - radius), (int)(y - radius), (int)(2 * radius), (int)(2 * radius));
	}
	
	@Override
	public Rectangle2D getBounds2D() {
		return new Rectangle2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
	}

	@Override
	public double getMaxX() {
		return x + radius;
	}
	@Override
	public double getMaxY() {
		return y + radius;
	}

	@Override
	public double getMinX() {
		return x - radius;
	}
	@Override
	public double getMinY() {
		return y - radius;
	}
	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public double getRadius(){
		return radius;
	}
	@Override
	public double getRadiusSq(){
		return radiussq;
	}
	@Override
	public int hashCode(){
		final int prime = 31;
		int result = super.hashCode();
		result = (int)(prime * result + java.lang.Double.doubleToLongBits(radius));
		return result;
	}
	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return (getX() >= x && getX() <= x + w && getY() >= y && getY() <= y
				+ h)
				|| contains(x, y)
				|| contains(x, y + h)
				|| contains(x + w, y)
				|| contains(x + w, y + h);
	}
	@Override
	public boolean intersects(Rectangle2D r) {
		return (getX() >= r.getMinX() && getX() <= r.getMaxX() && getY() >= r.getMinY() && getY() <= r.getMaxY())
				|| contains(r.getMinX(), r.getMinY())
				|| contains(r.getMinX(), r.getMaxY())
				|| contains(r.getMaxX(), r.getMinY())
				|| contains(r.getMaxX(), r.getMaxY());
	}
	@Override
	public void setRadius(double radius){
		radius = Math.abs(radius);
		this.radius = radius;
		this.radiussq = radius * radius;
	}
	@Override
	public String toString(){
		return "(" + this.x + ", " + this.y + "); r = " + this.radius;
	}
	@Override
	public double getHeight() {
		return 2 * radius;
	}
	@Override
	public double getWidth() {
		return 2 * radius;
	}
	@Override
	public void setHeight(double height) {
		setRadius(height / 2);
	}
	@Override
	public void setWidth(double width) {
		setRadius(width / 2);
	}
}
