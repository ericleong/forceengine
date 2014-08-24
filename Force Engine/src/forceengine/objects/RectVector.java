/*
 * RectVector.java
 * Created on Jul 1, 2008 12:06:11 AM
 * By Eric
 */
package forceengine.objects;

/**
 * A vector that uses rectangular coordinates for storage. The components of the
 * vector are stored as <code>doubles</code>.
 * 
 * @author Eric
 */
public class RectVector extends AbstractVector implements Vector {

	public static final RectVector ZERO = new RectVector(0, 0);

	private double vx;
	private double vy;

	public RectVector(double vx, double vy) {
		this.vx = vx;
		this.vy = vy;
	}

	public RectVector(Vector v) {
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
		RectVector other = (RectVector) obj;
		if (Double.doubleToLongBits(vx) != Double.doubleToLongBits(other.vx))
			return false;
		if (Double.doubleToLongBits(vy) != Double.doubleToLongBits(other.vy))
			return false;
		return true;
	}

	@Override
	public double getvx() {
		return this.vx;
	}

	@Override
	public double getvy() {
		return this.vy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		long result = 1;
		result = prime * result + Double.doubleToLongBits(vx);
		result = prime * result + Double.doubleToLongBits(vy);
		return (int) result;
	}

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
	public String toString() {
		return "RectVector <" + vx + ", " + vy + ">";
	}
}
