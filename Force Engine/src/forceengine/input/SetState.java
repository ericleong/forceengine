/**
 * Force Engine 2
 * forceengine.input
 * SetState.java
 * 
 * Eric
 *
 * Jul 3, 2011
 * 5:57:14 PM
 */
package forceengine.input;

import java.awt.event.InputEvent;

/**
 * @author Eric
 * 
 */
public class SetState implements Action, BiState {
	public boolean state;

	@Override
	public void act(int action, InputEvent event) {
		if (action == Action.PRESSED)
			state = true;
	}

	@Override
	public void setState(boolean state) {
		this.state = state;
	}

	@Override
	public boolean getState() {
		return state;
	}

}
