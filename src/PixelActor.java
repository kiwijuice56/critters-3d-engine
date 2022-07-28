import info.gridworld.actor.Actor;

import java.awt.Color;
import java.util.List;

/**
 * Represents a pixel in a screen world and provides access to screen parameters
 * Due to GridWorld specifications, the sprite (PixelActor.gif) must be kept in the same folder as this class
 */
public class PixelActor extends Actor {
	private final ScreenWorld screen;
	private List<Vector> z;

	public PixelActor(ScreenWorld screen) {
		setColor(Color.BLACK);
		this.screen = screen;
	}

	public void setBackgroundColor(Color backgroundColor) {
		screen.setBackgroundColor(backgroundColor);
	}

	public void setWireframeVisible(boolean isWireframeVisible) {
		screen.setDrawOutline(isWireframeVisible);
	}

	public void setFaceVisible(boolean isFaceVisible) {
		screen.setDrawFace(isFaceVisible);
	}

	public List<Vector> getZ() {
		return z;
	}

	public void setZ(List<Vector> z) {
		this.z = z;
	}
}
