package forceengine.demo.objects;

import java.util.LinkedList;
import java.util.ListIterator;

import forceengine.game.Freezable;
import forceengine.math.CircleMath;
import forceengine.math.VectorMath;
import forceengine.objects.ForceCircle;
import forceengine.objects.Point;
import forceengine.objects.PointVector;
import forceengine.objects.RectVector;
import forceengine.objects.Vector;

/**
 * A <code>ForceCircle</code> with some flexibility.
 * 
 * @author Eric
 * 
 */
public class ForceCircle_opt extends ForceCircle implements Freezable {
	protected boolean alive = true;
	protected boolean frozen;
	protected boolean collide = true;
	protected boolean responsive = true;

	public ForceCircle_opt(double x, double y, double radius, double mass){
		this(x, y, 0, 0, radius, mass, 1);
	}
	
	public ForceCircle_opt(double x, double y, double vx, double vy, double radius, double mass){
		this(x, y, vx, vy, radius, mass, 1);
	}
	public ForceCircle_opt(double x, double y, double vx, double vy, double radius, double mass, double restitution){
		this(x, y, vx, vy, radius, mass, restitution, true);
	}
	public ForceCircle_opt(double x, double y, double vx, double vy, double radius, double mass, double restitution,
			boolean collide){
		this(x, y, vx, vy, radius, mass, restitution, collide, false);
	}
	public ForceCircle_opt(double x, double y, double vx, double vy, double radius, double mass, double restitution,
			boolean collide, boolean frozen){
		super(x, y, vx, vy, radius, mass, restitution);
		this.collide = collide;
		this.frozen = frozen;
		ends = new LinkedList<Constraint>();
	}
	public ForceCircle_opt(double x, double y, double angle, double force, double radius, double mass,
			double restitution, boolean collide, boolean frozen, boolean polar){
		super(x, y, angle, force, radius, mass, restitution, polar);
		this.collide = collide;
		this.frozen = frozen;
		ends = new LinkedList<Constraint>();
	}
	public boolean isFrozen(){
		return frozen;
	}
	public double getRestitution(Object o){
		return restitution;
	}
	public void setFrozen(boolean frozen){
		this.frozen = frozen;
	}
	public boolean isCollide(Object o){
		return collide;
	}
	public boolean isResponsive(Object o){
		return responsive;
	}
	public void setCollide(boolean collide){
		this.collide = collide;
	}
	public void setResponsive(boolean responsive){
		this.responsive = responsive;
	}
	public boolean isAlive(){
		return alive;
	}
	public void setAlive(boolean alive){
		this.alive = alive;
	}
	public Vector responsiveAcceleration(PointVector pv, double t, Point b){
		Vector v = super.responsiveAcceleration(pv, t, b);
		
		if (b instanceof Gel) {
			Gel c = (Gel) b;
			Vector r = new RectVector(x - c.getX(), y - c.getY());
			
			// taken from http://www.plunk.org/~trina/thesis/html/thesis_toc.html
			double w = Gel.W(r.getLength(), this.radius);
			if (w > 0) {
				Vector f = r.getUnitVector().scale(w * 500 * radiussq);
				if (b instanceof Gel)
					f.scale(8 / ((Gel) b).getMass());
				v.add(f);
			}
			if(CircleMath.checkcirclecollide(this, c))
				v.add(new RectVector(-.1 * pv.getvx(), -.1 * pv.getvy()));
		}
		
		Constraint c;
		for(ListIterator<Constraint> i = ends.listIterator(); i.hasNext(); ){
			c = i.next();
			if(c.end == b){
				Vector r = VectorMath.getVector(b, pv);
				double x = c.distance - r.getLength();
				
				if (java.lang.Double.isInfinite(x) || java.lang.Double.isNaN(x))
					x = 0;
				if (x > 100)
					x = 100;
				else if (x < -100)
					x = -100;
				
				Vector unit = r.getUnitVector();
				v.add(unit.scale(-x * .95));
				// not quite sure what this is for (drag?)
				// v.add(unit.scale(-VectorMath.dotproduct(pv, r) * .0001));
				
				// instantaneous angular velocity
				/*double l = VectorMath.crossproduct(unit, pv);
				
				if (java.lang.Double.isInfinite(l) || java.lang.Double.isNaN(l))
					l = 0;
				
				if (l > 100)
					l = 100;
				else if (l < -100)
					l = -100;
				
				if (l > 0)
					v.add(new RectVector(unit.getvy(), -unit.getvx()).scale(Math.abs(l) * .1));
				else if(l < 0)
					v.add(new RectVector(-unit.getvy(), unit.getvx()).scale(Math.abs(l) * .1));*/
			}
		}

		return v;
	}
	
	public class Constraint {
		public Point end;
		public double distance;
		
		private Constraint(Point end, double distance){
			this.end = end;
			this.distance = distance;
		}

	}
	
	private LinkedList<Constraint> ends;

	public void addEnd(Point end, double distance) {
		ends.add(new Constraint(end, distance));
	}

	public LinkedList<Constraint> getEnds() {
		return ends;
	}
	
}
