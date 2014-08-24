package forceengine.input;

/**
 * An object that has two states, <code>true</code> or <code>false</code>.
 * @author Eric
 *
 */
public interface BiState {
	public void setState(boolean state);
	public boolean getState();
}
