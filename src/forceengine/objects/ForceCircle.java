package forceengine.objects;


/**
 * A <code>Force</code> that implements <code>Circle</code>.
 * 
 * @author Eric
 * 
 */
public class ForceCircle extends Force implements Circle {
	protected double radius;
	protected double radiussq; // probably won't dump but not all that useful to keep

	/**
	 * Creates a <code>ForceCircle</code> at (<code>x</code>, <code>y</code>) with the specified mass and radius. Vector
	 * components are set to 0 (no movement).
	 * 
	 * @param x The x value.
	 * @param y The y value.
	 * @param radius The radius of the circle.
	 * @param mass The mass of the circle.
	 */
	public ForceCircle(double x, double y, double radius, double mass){
		this(x, y, 0, 0, radius, mass);
	}
	/**
	 * Creates a <code>ForceCircle</code> at (<code>x</code>, <code>y</code>) with vector components &#60<code>vx</code>
	 * , <code>vy</code>&#62 and the specified mass and radius.
	 * 
	 * @param x The x value.
	 * @param y The y value.
	 * @param vx The horizontal vector component.
	 * @param vy The vertical vector component.
	 * @param radius The radius of the circle.
	 * @param mass The mass of the circle.
	 */
	public ForceCircle(double x, double y, double vx, double vy, double radius, double mass){
		this(x, y, vx, vy, radius, mass, 1);
	}
	/**
	 * Creates a <code>ForceCircle</code> at (<code>x</code>, <code>y</code>) with vector components &#60<code>vx</code>
	 * , <code>vy</code>&#62 and the specified mass and radius.
	 * 
	 * @param x The x value.
	 * @param y The y value.
	 * @param vx The horizontal vector component.
	 * @param vy The vertical vector component.
	 * @param radius The radius of the circle.
	 * @param mass The mass of the circle.
	 */
	public ForceCircle(double x, double y, double vx, double vy, double radius, double mass, boolean polar){
		this(x, y, vx, vy, radius, mass, 1);
	}
	public ForceCircle(double x, double y, double vx, double vy, double radius, double mass, double restitution){
		super(x, y, vx, vy, mass, restitution);
		this.radius = radius;
		this.radiussq = radius * radius;
	}
	public ForceCircle(double x, double y, double vx, double vy, double radius, double mass, double restitution, boolean polar){
		super(x, y, vx, vy, mass, restitution, polar);
		this.radius = radius;
		this.radiussq = radius * radius;
	}

	public ForceCircle(Force f, double radius){
		super(f);
		this.setRadius(radius);
	}
	public ForceCircle(ForceCircle f){
		super(f);
		this.radius = f.getRadius();
		this.radiussq = f.getRadiusSq();
	}
	public ForceCircle(Point p, Vector v, double radius, double mass){
		super(p, v, mass);
		this.setRadius(radius);
	}
	public ForceCircle(PointVector v, double radius, double mass){
		super(v, mass);
		this.setRadius(radius);
	}

	@Override
	public boolean equals(Object obj){
		if(this == obj)
			return true;
		if(!super.equals(obj))
			return false;
		if(getClass() != obj.getClass())
			return false;
		ForceCircle other = (ForceCircle)obj;
		if(java.lang.Double.doubleToLongBits(radius) != java.lang.Double.doubleToLongBits(other.radius))
			return false;
		return true;
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
	public void setRadius(double radius){
		if(radius > 0){
			this.radius = radius;
			this.radiussq = radius * radius;
		}
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
