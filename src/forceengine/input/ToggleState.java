package forceengine.input;

import java.awt.event.InputEvent;

public class ToggleState implements Action, BiState {
	public boolean state;
	
	public ToggleState(){
		state = false;
	}
	
	public ToggleState(boolean initalState){
		state = initalState;
	}
	
	@Override
	public void act(int action, InputEvent event) {
		if(action == Action.PRESSED)
			state = !state;
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
