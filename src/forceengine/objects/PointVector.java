package forceengine.objects;

import forceengine.VectorMath;

/**
 * A "free" vector class with points (x, y) and vector components (vx, vy), both
 * stored as rectangular coordinates. The vector is tied to the location, so
 * <code>equals</code> and related methods take into account (x, y).
 * 
 * @author Eric
 * @since Feb 28, 2008
 */
public class PointVector extends Point implements Vector, Line {
	protected double vx = 0;
	protected double vy = 0;
	
	public PointVector(){
		this(0, 0);
	}

	public PointVector(double x, double y) {
		this(x, y, 0, 0);
	}

	public PointVector(double x, double y, double vx, double vy) {
		super(x, y);
		this.vx = vx;
		this.vy = vy;
	}

	public PointVector(Point p) {
		this(p.getX(), p.getY(), 0, 0);
	}

	public PointVector(Point p, Vector v) {
		this(p.getX(), p.getY(), v.getvx(), v.getvy());
	}

	public PointVector(PointVector pv) {
		this(pv.getX(), pv.getY(), pv.getvx(), pv.getvy());
	}

	public PointVector(Vector v) {
		this(0, 0, v.getvx(), v.getvy());
	}

	@Override
	public PointVector add(Vector v) {
		setRect(getvx() + v.getvx(), getvy() + v.getvy());
		return this;
	}

	@Override
	public double dot(Vector v) {
		return getvx() * v.getvx() + getvy() * v.getvy();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PointVector other = (PointVector) obj;
		if (java.lang.Double.doubleToLongBits(x) != java.lang.Double.doubleToLongBits(other.x))
			return false;
		if (java.lang.Double.doubleToLongBits(y) != java.lang.Double.doubleToLongBits(other.y))
			return false;
		if (java.lang.Double.doubleToLongBits(vx) != java.lang.Double.doubleToLongBits(other.vx))
			return false;
		if (java.lang.Double.doubleToLongBits(vy) != java.lang.Double.doubleToLongBits(other.vy))
			return false;
		return true;
	}

	@Override
	public double getAngle() {
		return Math.atan2(this.vy, this.vx);
	}

	@Override
	public Rect getBounds() {
		return StaticRect.fromUpperLeft((int)Math.min(getX1(), getX2()), (int)Math.min(getY1(), getY2()), (int)Math.max(getX1(), getX2()), (int)Math.max(getY1(), getY2()));
	}

	@Override
	public double getLength() {
		return VectorMath.length(vx, vy);
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
	public Vector getUnitVector() {
		double length = getLength();
		return new RectVector(getvx() / length, getvy() / length);
	}

	/**
	 * Returns the vector components.
	 * 
	 * @return The vector components of this <code>PointVector</code>.
	 */
	public Vector getVector() {
		return new RectVector(this.vx, this.vy);
	}

	@Override
	public double getvx() {
		return vx;
	}

	@Override
	public double getvy() {
		return vy;
	}

	@Override
	public double getX1() {
		return x;
	}

	@Override
	public double getX2() {
		return x + vx;
	}

	@Override
	public double getY1() {
		return y;
	}

	@Override
	public double getY2() {
		return y + vy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (int) (prime * result + java.lang.Double.doubleToLongBits(vx));
		result = (int) (prime * result + java.lang.Double.doubleToLongBits(vy));
		return result;
	}

	/**
	 * Checks if this object is frozen (immobile or static).
	 * 
	 * @return Whether or not this object is frozen.
	 */
	public boolean isFrozen() {
		return false;
	}

	@Override
	public Vector rotate(double angle) {
		setRect(getvx() * Math.cos(angle) - getvy() * Math.sin(angle), getvx()
				* Math.sin(angle) + getvy() * Math.cos(angle));
		return this;
	}
	@Override
	public Vector scale(double scalar) {
		this.vx *= scalar;
		this.vy *= scalar;
		
		return this;
	}

	@Override
	public void setAngle(double angle) {
		double length = getLength();
		this.vx = length * Math.cos(angle);
		this.vy = length * Math.sin(angle);
	}

	@Override
	public void setLength(double length) {
		double origlen = getLength();
		setRect((length * (getvx() / origlen)), (length * (getvy() / origlen)));
	}

	@Override
	public void setLine(double x1, double y1, double x2, double y2) {
		x = x1;
		y = y1;
		vx = x2 - x1;
		vy = y2 - y1;
	}

	public void setPointVector(PointVector v) {
		this.x = v.getX();
		this.y = v.getY();
		this.vx = v.getvx();
		this.vy = v.getvy();
	}

	@Override
	public void setPolar(double angle, double length) {
		setRect(length * Math.cos(angle), length * Math.sin(angle));
	}

	@Override
	public void setRect(double vx, double vy) {
		this.vx = vx;
		this.vy = vy;
	}

	@Override
	public void setVector(Vector v) {
		this.vx = v.getvx();
		this.vy = v.getvy();
	}

	@Override
	public void setvx(double vx) {
		this.vx = vx;
	}

	@Override
	public void setvy(double vy) {
		this.vy = vy;
	}

	@Override
	public Vector subtract(Vector v) {
		setRect(getvx() - v.getvx(), getvy() - v.getvy());
		return this;
	}
	
	public PointVector translate(Vector v){
		translate(v.getvx(), v.getvy());
		return this;
	}

	@Override
	public String toString() {
		return super.toString() + String.format("\t<%4.8f, %4.8f>", this.vx, this.vy);
	}
}
