package forceengine.objects;

import forceengine.math.VectorMath;

/**
 * Like Line2D.Double.
 * 
 * @author Eric
 * 
 */
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
	public Rect getBounds() {
		return StaticRect.fromUpperLeft((int) Math.min(getX1(), getX2()), (int) Math.min(getY1(), getY2()), (int) Math.max(getX1(), getX2()),
				(int) Math.max(getY1(), getY2()));
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
