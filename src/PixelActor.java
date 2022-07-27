import info.gridworld.actor.Actor;

import java.awt.Color;

/**
 * Represents a pixel in a screen world and provides access to screen parameters
 * Due to GridWorld specifications, the sprite (PixelActor.gif) must be kept in the same folder as this class
 */
public class PixelActor extends Actor {
	private final ScreenWorld screen;

	public PixelActor(ScreenWorld screen) {
		setColor(Color.BLACK);
		this.screen = screen;
	}

	public void setBackgroundColor(Color backgroundColor) {
		screen.setBackgroundColor(backgroundColor);
	}
}
