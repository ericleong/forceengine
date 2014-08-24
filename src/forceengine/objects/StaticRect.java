package forceengine.objects;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A non-moving rectangle with X and Y as the center
 * 
 * @author Eric
 * 
 */
@SuppressWarnings("serial")
public class StaticRect extends Point implements Rect {
	protected double width = 0;
	protected double height = 0;

	public StaticRect(double x, double y, double width, double height) {
		super(x, y);
		this.width = width;
		this.height = height;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public double getMinX() {
		return this.getX() - this.width / 2;
	}

	@Override
	public double getMaxY() {
		return this.getY() + this.height / 2;
	}

	@Override
	public double getMaxX() {
		return this.getX() + this.width / 2;
	}

	@Override
	public double getMinY() {
		return this.getY() - this.height / 2;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public void setHeight(double height) {
		this.height = height;
	}

	@Override
	public void setWidth(double width) {
		this.width = width;
	}

	@Override
	public String toString() {
		return this.getX() + ", " + this.getY() + " | " + this.width + ", "
				+ this.height;
	}

	@Override
	public boolean contains(Point2D p) {
		return p.getX() >= getMinX() && p.getX() <= getMaxX()
				&& p.getY() >= getMinY() && p.getY() <= getMaxY();
	}

	@Override
	public boolean contains(Rectangle2D r) {
		return r.getMinX() >= getMinX() && r.getMaxX() <= getMaxX()
				&& r.getMinY() >= getMinY() && r.getMaxY() <= getMaxY();
	}

	@Override
	public boolean contains(double x, double y) {
		return x >= getMinX() && x <= getMaxX() && y >= getMinY()
				&& y <= getMaxY();
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		return x >= getMinX() && x + w <= getMaxX() && y >= getMinY()
				&& y + h <= getMaxY();
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle((int) (x - width / 2), (int) (y - height / 2),
				(int) width, (int) height);
	}

	@Override
	public Rectangle2D getBounds2D() {
		return new Rectangle2D.Double(x - width / 2, y - height / 2, width, height);
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
	public boolean intersects(Rectangle2D r) {
		return !(getMinX() > r.getMaxX() || getMaxX() < r.getMinX()
				|| getMinY() > r.getMaxY() || getMaxY() < r.getMinY());
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return !(getMinX() > x + w || getMaxX() < x || getMinY() > y + h || getMaxY() < y);
	}
}
