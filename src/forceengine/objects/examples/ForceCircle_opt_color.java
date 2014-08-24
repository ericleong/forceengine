/*
 * ForceCircle_opt_color.java
 * Created on Nov 23, 2008 4:29:58 PM
 * By Eric
 */
package forceengine.objects.examples;

import java.awt.Color;

import forceengine.graphics.Colored;
import forceengine.objects.Accelerator;
import forceengine.objects.Boundaries;
import forceengine.objects.StaticLine;
import forceengine.objects.StaticCircle;

/**
 * 
 * @author Eric
 */
public class ForceCircle_opt_color extends ForceCircle_opt implements Colored {
	private Color color;

	public ForceCircle_opt_color(double x, double y, double vx, double vy, double radius, double mass, Color color){
		super(x, y, vx, vy, radius, mass);
		this.color = color;
	}

	public ForceCircle_opt_color(double x, double y, double vx, double vy, double radius, double mass,
			double restitution, Color color){
		super(x, y, vx, vy, radius, mass, restitution);
		this.color = color;
	}

	public ForceCircle_opt_color(double x, double y, double vx, double vy, double radius, double mass,
			double restitution, boolean collide, Color color){
		super(x, y, vx, vy, radius, mass, restitution, collide);
		this.color = color;
	}

	public ForceCircle_opt_color(double x, double y, double angle, double force, double radius, double mass,
			double restitution, boolean collide, Color color, boolean polar){
		super(x, y, angle, force, radius, mass, restitution, collide, false, polar);
		this.color = color;
	}
	
	public Color getColor(){
		return color;
	}

	public void setColor(Color color){
		this.color = color;
	}
	
	@Override
	public boolean isCollide(Object o) {
		if (o instanceof Accelerator)
			return false;
		if (o instanceof Boundaries || o instanceof StaticLine || o instanceof StaticCircle)
			return true;
		return super.isCollide(o);
	}

}
