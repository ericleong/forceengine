package forceengine.objects;


/**
 * A non-moving rectangle with X and Y as the center
 * 
 * @author Eric
 * 
 */
public class StaticRect extends Point implements Rect {
	protected double width = 0;
	protected double height = 0;
	
	public static final StaticRect fromUpperLeft(double x, double y, double width, double height) {
		return new StaticRect(x + width / 2, y + width / 2, width, height);
	}

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
	public Rect getBounds() {
		return this;
	}
}
