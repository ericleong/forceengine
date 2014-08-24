package forceengine.game;

public interface Triggerable {
	/**
	 * This object was trigged by <code>trigger</code>.
	 * 
	 * @param triggers
	 *            The objects that triggered this object.
	 */
	public void trigger(Object[] triggers);
}
