package forceengine.objects.examples;

import java.awt.Color;

import forceengine.CircleMath;
import forceengine.graphics.Colored;
import forceengine.objects.Circle;
import forceengine.objects.ForceCircle;
import forceengine.objects.Point;
import forceengine.objects.PointVector;
import forceengine.objects.RectVector;
import forceengine.objects.Vector;

@SuppressWarnings("serial")
public class Gel extends ForceCircle implements Colored {
	public static final Color color = new Color(26, 141, 240, 100);
	
	public Gel(double x, double y, double vx, double vy, double radius,
			double mass, double restitution) {
		super(x, y, vx, vy, radius, mass, restitution);
	}

	@Override
	public boolean isCollide(Object o) {
		if (o instanceof ForceCircle)
			return false;
		return true;
	}

	public boolean isResponsive(Object o) {
		return true;
	}

	public Vector responsiveAcceleration(PointVector pv, double t, Point b) {
		Vector v = super.responsiveAcceleration(pv, t, b);

		if (b instanceof Circle) {
			Circle c = (Circle) b;
			Vector r = new RectVector(x - c.getX(), y - c.getY());
			
			// taken from http://www.plunk.org/~trina/thesis/html/thesis_toc.html
			double w = W(r.getLength(), this.radius);
			if (w > 0) {
				Vector f = r.getUnitVector().scale(w * 500 * radiussq);
				if (b instanceof Gel)
					f.scale(8 / ((Gel) b).getMass());
				if (b instanceof ForceCircle)
					f.scale(4 / ((ForceCircle) b).getMass());
				v.add(f);
			}
			if(CircleMath.checkcirclecollide(this, c))
				v.add(new RectVector(-.1 * pv.getvx(), -.1 * pv.getvy()));
		}

		return v;
	}

	// taken from http://www.plunk.org/~trina/thesis/html/thesis_toc.html
	public static final double W(double dist, double radius) {
		double c = 1 / (Math.PI * radius * radius * radius);
		double q = dist / radius;

		if (q > 0 && q <= 1) {
			return c * (1 + 3 / 2 * q * q + 3 / 4 * q * q * q);
		} else if (q > 1 && q <= 2) {
			return c * (1 / 4 * (2 - q) * (2 - q) * (2 - q));
		}
		return 0;
	}

	@Override
	public Color getColor() {
		return color;
	}
}
