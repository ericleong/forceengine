package forceengine.objects.examples;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import forceengine.CircleMath;
import forceengine.VectorMath;
import forceengine.graphics.Painter;
import forceengine.objects.Accelerator;
import forceengine.objects.Circle;
import forceengine.objects.Force;
import forceengine.objects.PointVector;
import forceengine.objects.RectVector;
import forceengine.objects.StaticCircle;
import forceengine.objects.Vector;

public class Pusher extends StaticCircle implements Accelerator, Painter {
	public static final Color color = new Color(255, 125, 125);

	private Vector direction;

	public Pusher(double x, double y, double radius, double angle) {
		super(x, y, radius);
		direction = new RectVector(Math.cos(angle), Math.sin(angle));
	}
	
	public Pusher(double x, double y, double radius, double vx, double vy) {
		super(x, y, radius);
		direction = new RectVector(vx, vy).getUnitVector();
	}
	
	public Vector getDirection(){
		return direction;
	}
	
	public boolean isCollide(Object o){
		return false;
	}

	@Override
	public Vector accelerate(Force f, PointVector pv, double t) {
		if (f instanceof Circle
				&& CircleMath.checkcirclecollide(this, ((Circle) f))){
			Vector v = new RectVector(direction);
			return v.scale(this.distance(pv) * radius / 50 / f.getMass());
		}
		return RectVector.ZERO;
	}

	@Override
	public void paint(Graphics2D g) {
		g.setColor(new Color(255, 125, 125));
		g.fill(new Ellipse2D.Double(getX() - getRadius(), getY() - getRadius(), 2 * getRadius(), 2 * getRadius()));
		
		g.setColor(new Color(255, 45, 15));
		g.draw(new Line2D.Double(getX(), getY(), getX() + getRadius()*getDirection().getvx(), getY() + getRadius()*getDirection().getvy()));
		Vector right = VectorMath.rotate(getDirection(), Math.PI/10);
		g.draw(new Line2D.Double(getX() + getRadius()*getDirection().getvx(), getY() + getRadius()*getDirection().getvy(),
				getX() + .8*getRadius()*right.getvx(), getY() + .8*getRadius()*right.getvy()));
		Vector left = VectorMath.rotate(getDirection(), -Math.PI/10);
		g.draw(new Line2D.Double(getX() + getRadius()*getDirection().getvx(), getY() + getRadius()*getDirection().getvy(),
				getX() + .8*getRadius()*left.getvx(), getY() + .8*getRadius()*left.getvy()));
	}

}
