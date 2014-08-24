package forceengine.objects;

import forceengine.VectorMath;

/**
 * A force. Meant to be extended with a shape, such as <code>Circle</code> and
 * <code>Rect</code>. Not meant to be used directly; use implementations of
 * <code>Vector</code> or <code>PointVector</code> if you need a force that is
 * shape-independent (such as the normal force).
 * 
 * @author Eric
 */
@SuppressWarnings("serial")
public class Force extends PointVector implements Vector {
	protected double length;
	protected double mass;
	protected double restitution = 1; // maybe i should put this in _opt
	
	public Force(double x, double y, double mass){
		this(x, y, 0, 0, mass, 1);
	}
	
	public Force(double x, double y, double vx, double vy, double mass){
		this(x, y, vx, vy, mass, 1);
	}

	/**
	 * Creates a {@link Force} object.
	 * 
	 * @param x
	 *            The x component of the location.
	 * @param y
	 *            The y component of the location.
	 * @param vx
	 *            The x component of the velocity.
	 * @param vy
	 *            The y component of the velcotiy.
	 * @param mass
	 *            The mass of the object.
	 * @param restitution
	 *            The % energy left after a collision.
	 */
	public Force(double x, double y, double vx, double vy, double mass,
			double restitution) {
		super(x, y, vx, vy);
		this.length = VectorMath.length(vx, vy); // distance formula
		this.mass = mass;
		if (restitution > 1)
			restitution = 1;
		else if (restitution < 0)
			restitution = 0;
		this.restitution = restitution;
	}

	public Force(double x, double y, double angle, double force, double mass,
			double restitution, boolean polar) {
		this(x, y, force * Math.cos(angle), force * Math.sin(angle), mass,
				restitution);
	}

	public Force(Force f) {
		super(f);
		this.length = f.getLength();
		this.mass = f.getMass();
		this.restitution = f.getRestitution(f);
	}

	public Force(Point p, Vector v, double mass) {
		super(p, v);
		this.length = v.getLength();
		this.mass = mass;
	}

	public Force(PointVector pv, double mass) {
		super(pv);
		this.length = pv.getLength();
	}

	/**
	 * The acceleration of this {@link Force}.
	 * 
	 * @param pv
	 *            the current position of this object
	 * @param t
	 *            the time interval
	 * @return This object's acceleration.
	 */
	public Vector getAcceleration(PointVector pv, double t) {
		return new RectVector(0, 0);
	}

	public int compareTo(Force o) {
		if (this.length > o.length)
			return -1;
		else if (this.length < o.length)
			return 1;
		else
			return 0;
	}

	@Override
	public double getLength() {
		return length;
	}

	public double getMass() {
		return mass;
	}

	public double getnormvx() {
		if (this.length > 0)
			return this.vx / this.length;
		else
			return 0;
	}

	public double getnormvy() {
		if (this.length > 0)
			return this.vy / this.length;
		else
			return 0;
	}

	@Override
	public double getRestitution(Object o) {
		return restitution;
	}

	/**
	 * Acceleration on this {@link Force} that depends <code>b</code>.
	 * 
	 * @param pv
	 *            The current point being integrated.
	 * @param t
	 *            The time interval.
	 * @param b
	 *            The force that is being compared to.
	 * @return The acceleration on this object that is dependent on
	 *         <code>b</code>.
	 */
	public Vector responsiveAcceleration(PointVector pv, double t, Point b) {
		return new RectVector(0, 0);
	}
	
	@Override
	public Vector scale(double scalar) {
		this.vx *= scalar;
		this.vy *= scalar;
		this.length *= scalar;
		
		return this;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	@Override
	public void setPointVector(PointVector v) {
		this.x = v.getX();
		this.y = v.getY();
		if (!this.isFrozen()) {
			if (this.vx != v.getvx() || this.vy != v.getvy()) {
				this.vx = v.getvx();
				this.vy = v.getvy();
				this.length = VectorMath.length(vx, vy);
			}
		}
	}
	
	@Override
	public void setRect(double vx, double vy){
		this.vx = vx;
		this.vy = vy;
		this.length = VectorMath.length(vx, vy);
	}

	public void setRestitution(double restitution) {
		if (restitution > 1)
			restitution = 1;
		else if (restitution < 0)
			restitution = 0;
		this.restitution = restitution;
	}

	@Override
	public void setVector(Vector v) {
		setRect(v.getvx(), v.getvy());
	}

	@Override
	public void setvx(double vx) {
		this.vx = vx;
		this.length = VectorMath.length(vx, vy);
	}

	@Override
	public void setvy(double vy) {
		this.vy = vy;
		this.length = VectorMath.length(vx, vy);
	}

	@Override
	public String toString() {
		return super.toString() + "; F = " + this.length + "; m = " + this.mass
				+ "; Cr = " + this.restitution;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = java.lang.Double.doubleToLongBits(length);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = java.lang.Double.doubleToLongBits(mass);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = java.lang.Double.doubleToLongBits(restitution);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Force other = (Force) obj;
		if (java.lang.Double.doubleToLongBits(length) != java.lang.Double
				.doubleToLongBits(other.length))
			return false;
		if (java.lang.Double.doubleToLongBits(mass) != java.lang.Double
				.doubleToLongBits(other.mass))
			return false;
		if (java.lang.Double.doubleToLongBits(restitution) != java.lang.Double
				.doubleToLongBits(other.restitution))
			return false;
		return true;
	}

}