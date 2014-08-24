package forceengine.input;

import java.awt.event.InputEvent;

public class HoldState implements Action, BiState {
	public boolean state;

	public HoldState() {
		state = false;
	}

	public HoldState(boolean initalState) {
		state = initalState;
	}

	@Override
	public void act(int action, InputEvent event) {
		if (action == Action.PRESSED)
			state = true;
		else if (action == Action.RELEASED)
			state = false;
	}

	@Override
	public boolean getState() {
		return state;
	}

	@Override
	public void setState(boolean state) {
		this.state = state;
	}

}
