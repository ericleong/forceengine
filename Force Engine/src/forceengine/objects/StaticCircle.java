package forceengine.objects;

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
	public Rect getBounds() {
		return StaticRect.fromUpperLeft((int)(x - radius), (int)(y - radius), (int)(2 * radius), (int)(2 * radius));
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
