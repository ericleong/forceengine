/*
 * AbstractVector.java
 * Created on Apr 15, 2008 7:27:14 PM
 * By Eric
 */
package forceengine.objects;

import forceengine.math.VectorMath;

/**
 * An Abstract implementation of <code>Vector</code> in order to reduce
 * redundancy while still allowing <code>PointVector</code> to extend
 * <code>Point</code>. Appropriate methods <b>must</b> be overriden depending on
 * how the properties are stored in the subclass. Either &ltvx, vy&gt, (angle,
 * length) or both sets of methods must be overriden.
 * 
 * @author Eric
 * @since Apr 15, 2008
 */
public abstract class AbstractVector implements Vector, Comparable<Vector> {
	@Override
	public Vector add(Vector v) {
		setRect(getvx() + v.getvx(), getvy() + v.getvy());
		return this;
	}
	
	@Override
	public double dot(Vector v) {
		return getvx() * v.getvx() + getvy() * v.getvy();
	}

	/**
	 * Compares the magnitude of the vectors and returns a positive if the
	 * argument is greater than the value of magnitude and vice versa.
	 * 
	 * @return a number indicating the which vector magnitude is larger
	 */
	@Override
	public int compareTo(Vector o) {
		return (int) (Math.abs((getvx()) + (getvy())) - Math.abs((o.getvx())
				+ (o.getvy())));
	}

	@Override
	public boolean equals(Object obj) { // uses <vx, vy> because it's a bit more
										// accurate to not compare doubles
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector other = (Vector) obj;
		if (Double.doubleToLongBits(getvx()) != Double.doubleToLongBits(other
				.getvx()))
			return false;
		if (Double.doubleToLongBits(getvy()) != Double.doubleToLongBits(other
				.getvy()))
			return false;
		return true;
	}

	@Override
	public double getAngle() {
		return Math.atan2(getvy(), getvx());
	}

	@Override
	public double getLength() {
		return Math.sqrt(Math.abs(getvx() * getvx() + getvy() * getvy()));
	}

	@Override
	public Vector getUnitVector() {
		double length = getLength();
		return new RectVector(getvx() / length, getvy() / length);
	}

	@Override
	public double getvx() {
		return getLength() * Math.cos(getAngle());
	}

	@Override
	public double getvy() {
		return getLength() * Math.sin(getAngle());
	}
	
	@Override
	public Vector rotate(double angle) {
		setRect(getvx() * Math.cos(angle) - getvy() * Math.sin(angle), getvx()
				* Math.sin(angle) + getvy() * Math.cos(angle));
		return this;
	}

	@Override
	public Vector scale(double scalar) {
		setRect(getvx() * scalar, getvy() * scalar);
		return this;
	}

	@Override
	public void setAngle(double angle) {
		double length = getLength();
		setvx((length * Math.cos(angle)));
		setvy((length * Math.sin(angle)));
	}

	@Override
	public void setLength(double length) {
		double origlen = getLength();
		setvx((length * (getvx() / origlen)));
		setvy((length * (getvy() / origlen)));
	}

	@Override
	public void setPolar(double angle, double length) {
		setRect(length * Math.cos(angle), length * Math.sin(angle));
	}

	@Override
	public void setRect(double vx, double vy) {
		setPolar(Math.atan2(vy, vx), VectorMath.length(vx, vy));
	}

	@Override
	public abstract void setVector(Vector v);

	@Override
	public void setvx(double vx) {
		double vy = getvy();
		setAngle(Math.atan2(vy, vx));
		setLength(VectorMath.length(vx, vy));
	}

	@Override
	public void setvy(double vy) {
		double vx = getvx();
		setAngle(Math.atan2(vy, vx));
		setLength(VectorMath.length(vx, vy));
	}
	
	@Override
	public Vector subtract(Vector v) {
		setRect(getvx() - v.getvx(), getvy() - v.getvy());
		return this;
	}

	@Override
	public String toString() {
		return "<" + getvx() + ", " + getvy() + ">" + " or " + "(" + getAngle()
				+ ", " + getLength() + ")";
	}
}
