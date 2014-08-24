package forceengine.objects;

/**
 * Accelerates a {@link Force}.
 * 
 * @author Eric
 * 
 */
public interface Accelerator {

	/**
	 * Accelerates a {@link Force}.
	 * 
	 * @param f
	 *            the force to accelerate
	 * @param pv
	 *            the current position and velocity of the force
	 * @param t
	 *            the time interval
	 * @return The acceleration on <code>f</code> due to this
	 *         {@link Accelerator}.
	 */
	public Vector accelerate(Force f, PointVector pv, double t);

}
