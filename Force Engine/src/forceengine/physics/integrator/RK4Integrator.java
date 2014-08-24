package forceengine.physics.integrator;

import forceengine.objects.Accelerator;
import forceengine.objects.Force;
import forceengine.objects.PointVector;
import forceengine.objects.Vector;

public class RK4Integrator implements Integrator {

	private static class Derivative {
		double dx;
		double dy;
		double dvx;
		double dvy;
	}

	public static Vector accelerate(Force f, PointVector pv, double t) {
		return f.getAcceleration(pv, t);
	}

	private static Derivative evaluate(Accelerator external, Force initial,
			double t, double dt, Derivative d) {
		PointVector n = new PointVector(initial.getX() + d.dx * dt,
				initial.getY() + d.dy * dt, initial.getvx() + d.dvx * dt,
				initial.getvy() + d.dvy * dt);

		Derivative output = new Derivative();
		Vector v = accelerate(initial, n, t + dt).add(
				external.accelerate(initial, n, dt));
		output.dx = n.getvx();
		output.dvx = v.getvx();
		output.dy = n.getvy();
		output.dvy = v.getvy();
		return output;
	}

	public PointVector integrate(Accelerator external, Force f, double t,
			double dt) {
		Derivative a = evaluate(external, f, t, 0, new Derivative());
		Derivative b = evaluate(external, f, t + dt * 0.5, dt * 0.5, a);
		Derivative c = evaluate(external, f, t + dt * 0.5, dt * 0.5, b);
		Derivative d = evaluate(external, f, t + dt, dt, c);

		double dxdt = (1 / 6.0) * (a.dx + 2 * (b.dx + c.dx) + d.dx);
		double dvxdt = (1 / 6.0) * (a.dvx + 2 * (b.dvx + c.dvx) + d.dvx);
		double dydt = (1 / 6.0) * (a.dy + 2 * (b.dy + c.dy) + d.dy);
		double dvydt = (1 / 6.0) * (a.dvy + 2 * (b.dvy + c.dvy) + d.dvy);

		return new PointVector(f.getX() + dxdt * dt, f.getY() + dydt * dt,
				f.getvx() + dvxdt * dt, f.getvy() + dvydt * dt);
	}

}
