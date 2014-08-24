package forceengine.objects.examples;

import java.awt.Color;

import forceengine.VectorMath;
import forceengine.graphics.Colored;
import forceengine.objects.ForceCircle;
import forceengine.objects.Point;
import forceengine.objects.PointVector;
import forceengine.objects.RectVector;
import forceengine.objects.Vector;

/**
 * Doesn't work.
 * 
 * @author Eric
 *
 */
@SuppressWarnings("serial")
public class Liquid extends ForceCircle implements Colored {
	public static final Color color = new Color(26, 141, 240, 100);
	
	/**
	 * Usually set to these values.
	 */
	public static final double alpha = 1, beta = 2;
	
	/**
	 * Ratio of specific heats.
	 */
	public static final double gamma = 1.33; // water
	
	public static final double g = 10;
	
	private static final double default_density = 1000;
	private static final double standard_density = 1;
	
	double d;
	double u;
	double P;
	
	public Liquid(double x, double y, double vx, double vy, double radius, double mass, double restitution) {
		super(x, y, vx, vy, radius, mass, restitution);
		d = default_density + Math.random() - .5;
		u = 1 / 2 * getMass() * getLength() * getLength();
		setMass(d * Math.PI * getRadiusSq());
	}
	
	@Override
	public boolean isCollide(Object o) {
		if(o instanceof ForceCircle)
			return false;
		return true;
	}
	
	public boolean isResponsive(Object o) {
		return true;
	}
	
	PointVector prev_pv = new PointVector();

	public Vector responsiveAcceleration(PointVector pv,
			double t, Point b) {
		Vector v = super.responsiveAcceleration(pv, t, b);
		
		double sumrad = radius;
		if (b instanceof Liquid) {
			Liquid liq = (Liquid) b;
			
			
			Vector r = new RectVector(pv.getX() - liq.getX(), pv.getY() - liq.getY());
			double r_length = r.getLength();
			double r_prev_length = new RectVector(prev_pv.getX() - liq.prev_pv.getX(), prev_pv.getY() - liq.prev_pv.getY()).getLength();
			
			// taken from http://www.plunk.org/~trina/thesis/html/thesis_toc.html
			double dw = W(r_prev_length, this.radius) - W(r_length, this.radius);
			
			double dr = r_length - r_prev_length;
			
			Vector dW;
			if(dr == 0)
				dW = new RectVector(0, 0);
			else
				dW = new RectVector(dw / (dr), 0);
			
			Vector sum = dW.scale(liq.getMass() * (P / (d * d) + liq.P / (liq.d * liq.d)));
				
			Vector f = r.getUnitVector().scale(sum.getvx());
			
			v.add(f);
			
			Vector v_ij = pv.getVector();
			
			d += getMass() * v_ij.dot(dW);
			u += .5 * v_ij.scale(liq.getMass() * (P / (d * d) + liq.P / (liq.d * liq.d))).dot(dW);
			
			double B = 200 * d * g * 100 / (gamma);
			P = B * (Math.pow(d / standard_density, gamma) - 1);
			setMass(d * Math.PI * getRadiusSq());
			
			sumrad = radius + liq.getRadius();
		}
		if(v.getLength() > sumrad){
			v.setLength(sumrad);
		}

		prev_pv = pv;
		
		return v;
	}
	
	// r, h
	public static final double W(double dist, double radius){
		double c = 1 / (Math.PI * radius * radius * radius);
		
		double q = dist / radius;
		
		if(q > 0 && q <= 1){
			return c * (1 + 3 / 2 * q * q + 3 / 4 * q * q * q);
		} else if (q > 1 && q <= 2){
			return c * (1 / 4 * (2 - q) * (2 - q) * (2 - q));
		}
		return 0;
	}
	
	public static final double PI_ij(Liquid i, Liquid j){
		
		double density = i.getMass() / (i.getRadiusSq() * Math.PI);
		
		// P = (gamma - 1) * density * mu
		double P = (gamma - 1) * density;
		
		double mean_density = (i.getMass() + j.getMass())
				/ (i.getRadiusSq() * j.getRadiusSq() * Math.PI * Math.PI) / 2;
		
		double etaSq = 0.01 * i.getRadiusSq();
		
		Vector r_ij = VectorMath.getVector(j, i);
		
		// v_ab * r_ab
		double vr = VectorMath.subtract(j, i).dot(r_ij);
		
		// speed of sound
		double B = 200 * i.d * g * 100 / (gamma);
		double c = Math.sqrt(gamma * (P + B)/ density);
		
		if(vr < 0){
			double mu_ij = i.getRadius() * vr / (r_ij.getLength() * r_ij.getLength() + etaSq);
			return -alpha * c * mu_ij + beta * mu_ij * mu_ij / mean_density; 
		} else
			return 0;
	}

	@Override
	public Color getColor() {
		return color;
	}
	
}
