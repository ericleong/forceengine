package forceengine.physics.integrator;

import forceengine.objects.Accelerator;
import forceengine.objects.Force;
import forceengine.objects.PointVector;
import forceengine.objects.Vector;

public class EulerIntegrator implements Integrator {

	@Override
	public PointVector integrate(Accelerator external, Force f, double t,
			double dt) {
		Vector a = f.getAcceleration(f, t).add(external.accelerate(f, f, t));
		return new PointVector(f).translate(f.getVector()).add(a);
	}

}
