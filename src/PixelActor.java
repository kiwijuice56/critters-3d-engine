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
		refresh();
	}

	public void toggleOutline(boolean isWireframeVisible) {
		screen.setDrawOutline(isWireframeVisible);
		refresh();
	}

	public void toggleFill(boolean isFaceVisible) {
		screen.setDrawFace(isFaceVisible);
		refresh();
	}

	public void toggleShading(boolean isFaceVisible) {
		screen.setShadeFace(isFaceVisible);
		refresh();
	}

	public void toggleAutoRotate(boolean isRotating) {
		screen.setShowcaseRotation(isRotating);
	}

	public String checkScene() {
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < screen.getScene().getMeshes().size(); i++){
			output.append(i).append(": ").append(screen.getScene().getMeshes().get(i)).append("\n");
		}
		return output.toString();
	}

	public void meshTranslate(int meshIdx, double x, double y, double z) {
		if (meshIdx < 0 || meshIdx >= screen.getScene().getMeshes().size())
			return;
		Mesh m = screen.getScene().getMeshes().get(meshIdx);
		m.setTranslation(m.getTranslation().add(new Vector(x, y, z)));

		refresh();
	}

	public void meshRotate(int meshIdx, double x, double y, double z) {
		if (meshIdx < 0 || meshIdx >= screen.getScene().getMeshes().size())
			return;
		Mesh m = screen.getScene().getMeshes().get(meshIdx);
		m.setRotation(m.getRotation().add(new Vector(x, y, z)));

		refresh();
	}

	private void refresh() {
		boolean wasRotating = screen.isShowcaseRotation();
		screen.setShowcaseRotation(false);
		screen.step();
		screen.setShowcaseRotation(wasRotating);
	}
}
