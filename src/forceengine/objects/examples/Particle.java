package forceengine.objects.examples;

import java.awt.Color;

import forceengine.game.Living;
import forceengine.graphics.Colored;
import forceengine.objects.Boundaries;
import forceengine.objects.ForceCircle;
import forceengine.objects.Vector;

/**
 * A particle with a lifetime
 * 
 * @author Eric
 * 
 */
public class Particle extends ForceCircle implements Colored, Living {
	protected Color color;
	protected double age;

	public Particle(double x, double y, double angle, double magnitude,
			double radius, double mass, double restitution, int life,
			Color color, boolean polar) {
		super(x, y, magnitude * Math.cos(angle), magnitude * Math.sin(angle),
				radius, mass, restitution);
		this.age = life;
		this.color = color;
	}

	public Particle(double x, double y, double vx, double vy, double radius,
			double mass, int life, Color color) {
		super(x, y, vx, vy, radius, mass);
		this.age = life;
		this.color = color;
	}

	@Override
	public void age(double time) {
		age -= time;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public double getRestitution(Object o) {
		return super.getRestitution(o);
	}

	@Override
	public boolean isAlive() {
		return age > 0;
	}

	@Override
	public boolean isCollide(Object o) {
		return !(o instanceof Vector) || o instanceof Boundaries;
	}

	@Override
	public void setAge(double age) {
		this.age = age;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
