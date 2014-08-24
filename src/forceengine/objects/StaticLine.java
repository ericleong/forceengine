package forceengine.objects;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import forceengine.VectorMath;

/**
 * Like Line2D.Double.
 * 
 * @author Eric
 * 
 */
@SuppressWarnings("serial")
public class StaticLine extends Point implements Line {
	private double x2, y2;

	public StaticLine(double x1, double y1, double x2, double y2) {
		super(x1, y1);
		this.x2 = x2;
		this.y2 = y2;
	}

	public Point getClosestPointOnLine(double x, double y) {
		return VectorMath.closestpointonline(this.getX1(), this.getY1(),
				this.getX2(), this.getY2(), x, y);
	}

	public Point getClosestPointOnLine(Point p) {
		return VectorMath.closestpointonline(this, p);
	}

	@Override
	public boolean contains(Point2D p) {
		return false;
	}

	@Override
	public boolean contains(Rectangle2D r) {
		return false;
	}

	@Override
	public boolean contains(double x, double y) {
		return false;
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		return false;
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle((int) Math.min(getX1(), getX2()), (int) Math.min(
				getY1(), getY2()), (int) Math.max(getX1(), getX2()),
				(int) Math.max(getY1(), getY2()));
	}

	@Override
	public Rectangle2D getBounds2D() {
		return new Rectangle2D.Double(Math.min(getX1(), getX2()), Math.min(
				getY1(), getY2()), Math.max(getX1(), getX2()), Math.max(
				getY1(), getY2()));
	}

	@Override
	public PathIterator getPathIterator(AffineTransform arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PathIterator getPathIterator(AffineTransform arg0, double arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		return r.intersects(getBounds2D());
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return getBounds2D().intersects(x, y, w, h);
	}

	@Override
	public Point getP1() {
		return new Point(getX1(), getY1());
	}

	@Override
	public Point getP2() {
		return new Point(getX2(), getY2());
	}

	@Override
	public double getX1() {
		return x;
	}

	@Override
	public double getX2() {
		return x2;
	}

	@Override
	public double getY1() {
		return y;
	}

	@Override
	public double getY2() {
		return y2;
	}

	@Override
	public void setLine(double x1, double y1, double x2, double y2) {
		x = x1;
		y = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
}
