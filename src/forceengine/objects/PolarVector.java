/*
 * PolarPoint.java
 * Created on Jun 30, 2008 7:27:14 PM
 * By Eric
 */
package forceengine.objects;

/**
 * A point in polar coordinate space, with angle stored as <code>double</code>
 * and radius stored as <code>double</code>. Can also be used as a
 * <code>Vector</code>.
 * 
 * @author Eric
 */
public class PolarVector extends AbstractVector implements Vector {
	
	public static final PolarVector ZERO = new PolarVector(0, 0);
	
	private double angle;
	private double length;

	public PolarVector(double angle, double length) {
		this.angle = angle;
		this.length = length;
	}

	public PolarVector(Vector v) {
		setVector(v);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PolarVector other = (PolarVector) obj;
		if (Double.doubleToLongBits(angle) != Double
				.doubleToLongBits(other.angle))
			return false;
		if (Double.doubleToLongBits(length) != Double
				.doubleToLongBits(other.length))
			return false;
		return true;
	}

	@Override
	public double getAngle() {
		return angle;
	}

	@Override
	public double getLength() {
		return length;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(angle);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = (int) (prime * result + Double.doubleToLongBits(length));
		return result;
	}

	@Override
	public void setAngle(double angle) {
		this.angle = angle;
	}

	@Override
	public void setLength(double length) {
		this.length = length;
	}

	@Override
	public void setVector(Vector v) {
		this.angle = v.getAngle();
		this.length = v.getLength();
	}
}
