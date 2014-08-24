/**
 * Force Engine 2
 * forceengine.input
 * InputBind.java
 * 
 * Eric
 *
 * Jun 4, 2011
 * 10:30:46 PM
 */
package forceengine.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ActionMap;
import javax.swing.InputMap;

/**
 * A lighter version of Swing's {@link ActionMap} and {@link InputMap}.
 * 
 * @author Eric
 * 
 */
public class InputBind implements MouseListener, MouseMotionListener,
		MouseWheelListener, KeyListener {
	public Map<Integer, String> mouseWheelMap;
	public Map<Integer, String> mouseMap;
	public Map<Integer, String> keyMap;
	public Map<String, Action> actionMap;
	// current
	public int x;
	public int y;

	public InputBind() {
		this(new HashMap<Integer, String>(), new HashMap<Integer, String>(),
				new HashMap<Integer, String>(), new HashMap<String, Action>());
	}

	public InputBind(Map<Integer, String> mouseMap,
			Map<Integer, String> mouseWheelMap, Map<Integer, String> keyMap,
			Map<String, Action> actionMap) {
		this.mouseMap = mouseMap;
		this.mouseWheelMap = mouseWheelMap;
		this.keyMap = keyMap;
		this.actionMap = actionMap;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (keyMap.containsKey(e.getKeyCode()))
			actionMap.get(keyMap.get(e.getKeyCode())).act(Action.PRESSED, e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (keyMap.containsKey(e.getKeyCode()))
			actionMap.get(keyMap.get(e.getKeyCode())).act(Action.RELEASED, e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (keyMap.containsKey(e.getKeyCode()))
			actionMap.get(keyMap.get(e.getKeyCode())).act(Action.TYPED, e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		if (mouseMap.containsKey(e.getButton()))
			actionMap.get(mouseMap.get(e.getButton())).act(Action.TYPED, e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		if (mouseMap.containsKey(e.getButton()))
			actionMap.get(mouseMap.get(e.getButton())).act(Action.PRESSED, e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		if (mouseMap.containsKey(e.getButton()))
			actionMap.get(mouseMap.get(e.getButton())).act(Action.RELEASED, e);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		x = e.getX();
		y = e.getY();

		if (mouseWheelMap.containsKey((int) Math.signum(e.getWheelRotation()))) {
			for (int i = 0; i < Math.abs(e.getWheelRotation()); i++) {
				actionMap.get(
						mouseWheelMap.get((int) Math.signum(e
								.getWheelRotation()))).act(Action.PRESSED, e);
			}
			actionMap.get(
					mouseWheelMap.get((int) Math.signum(e.getWheelRotation())))
					.act(Action.RELEASED, e);
			actionMap.get(
					mouseWheelMap.get((int) Math.signum(e.getWheelRotation())))
					.act(Action.TYPED, e);
		} else if (mouseWheelMap.containsKey(e.getButton())) {
			actionMap.get(mouseWheelMap.get(e.getButton())).act(Action.PRESSED,
					e);
			actionMap.get(mouseWheelMap.get(e.getButton())).act(
					Action.RELEASED, e);
			actionMap.get(mouseWheelMap.get(e.getButton())).act(
					Action.TYPED, e);
		}

	}

	public void addMouseWheelAction(Integer button, String name, Action action) {
		mouseWheelMap.put(button, name);
		actionMap.put(name, action);
	}

	public void addMouseAction(Integer button, String name, Action action) {
		mouseMap.put(button, name);
		actionMap.put(name, action);
	}

	public void addKeyAction(Integer key, String name, Action action) {
		keyMap.put(key, name);
		actionMap.put(name, action);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}

}
