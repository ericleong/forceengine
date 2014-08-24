package forceengine.physics.integrator;

import forceengine.objects.Accelerator;
import forceengine.objects.Force;
import forceengine.objects.PointVector;

/**
 * An {@link Integrator} finds the displacement and change in velocity of a
 * given {@link Force}, which forms the basis for its movement through space.
 * 
 * @author Eric
 */
public interface Integrator {

	/**
	 * Determines the displacement and change in velocity of a {@link Force}.
	 * 
	 * @param external
	 *            an external acceleration to apply to the force.
	 * @param f
	 *            the {@link Force} being integrated.
	 * @param t
	 *            the time interval.
	 * @param dt
	 *            controls the granularity of the integrator (if applicable).
	 * @return A {@link PointVector} representing the displacement and change in
	 *         velocity of <code>f</code>.
	 */
	public PointVector integrate(Accelerator external, Force f, double t,
			double dt);
}
