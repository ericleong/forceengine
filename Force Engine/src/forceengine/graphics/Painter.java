package forceengine.graphics;

import java.awt.Graphics2D;

/**
 * An object that can draw itself.
 * 
 * @author Eric
 *
 */
public interface Painter {
	/**
	 * Paints this object.
	 * 
	 * @param g the {@link Graphics2D} context on which to paint
	 */
	public void paint(Graphics2D g);
}
