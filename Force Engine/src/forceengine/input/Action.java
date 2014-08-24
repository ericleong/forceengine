package forceengine.input;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public interface Action {
	public static final int PRESSED = 1;
	public static final int TYPED = 2;
	public static final int RELEASED = 3;
	public static final int HELD = 4;
	/**
	 * Triggers this action to act.
	 * 
	 * @param action The action that was performed.
	 * @param event The event that triggered this action.
	 * @see InputEvent
	 * @see KeyEvent
	 * @see MouseEvent
	 * @see MouseWheelEvent
	 */
	public void act(int action, InputEvent event);
}
