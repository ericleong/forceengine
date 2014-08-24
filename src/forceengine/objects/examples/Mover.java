package forceengine.objects.examples;

import forceengine.CircleMath;
import forceengine.objects.Accelerator;
import forceengine.objects.Circle;
import forceengine.objects.Force;
import forceengine.objects.PointVector;
import forceengine.objects.RectVector;
import forceengine.objects.StaticCircle;
import forceengine.objects.Vector;
import forceengine.physics.PhysicsMath;

public class Mover extends StaticCircle implements Accelerator {
	private boolean alive;
	private int direction;
	
	public Mover(double x, double y, double power, int direction) {
		super(x, y, power);
		alive = true;
		this.direction = direction;
	}

	@Override
	public Vector accelerate(Force f, PointVector pv, double t) {
		if (f instanceof Circle && CircleMath.checkcirclecollide(this, ((Circle)f)))
			return RectVector.ZERO;
		return PhysicsMath.inverseSquaredAttraction(-radius * radius * 250 * direction, this, pv);
	}
	
	public boolean isCollide(Object o){
		return false;
	}
	
	public boolean isResponsive(Object o){
		return true;
	}
	
	public void setAlive(boolean alive){
		this.alive = alive;
	}
	
	public boolean isAlive(){
		return alive;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
}
