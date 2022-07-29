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
		screen.redraw();
	}

	public void setEdgeColor(Color edgeColor) {
		screen.setEdgeColor(edgeColor);
		screen.redraw();
	}

	public void toggleOutline(boolean drawOutline) {
		screen.getScene().getMainCamera().setOption(Camera.DRAW_EDGES, drawOutline);
		screen.redraw();
	}

	public void toggleFill(boolean drawFaces) {
		screen.getScene().getMainCamera().setOption(Camera.FILL_FACES, drawFaces);
		screen.redraw();
	}

	public void toggleShading(boolean shadeFaces) {
		screen.getScene().getMainCamera().setOption(Camera.LIGHT_FACES, shadeFaces);
		screen.redraw();
	}

	public void toggleBackCulling(boolean cullBack) {
		screen.getScene().getMainCamera().setOption(Camera.CULL_BACK, cullBack);
		screen.redraw();
	}

	public String checkScene() {
		StringBuilder out = new StringBuilder();
		out.append("Camera: \n").append(screen.getScene().getMainCamera());
		for (int i = 0; i < screen.getScene().getMeshes().size(); i++) {
			out.append("Mesh ").append(i).append(": ").append(screen.getScene().getMeshes().get(i)).append("\n");
		}
		return out.toString();
	}

	public void meshTranslate(int meshIdx, double x, double y, double z) {
		if (meshIdx < 0 || meshIdx >= screen.getScene().getMeshes().size())
			return;
		Mesh m = screen.getScene().getMeshes().get(meshIdx);
		m.setTranslation(m.getTranslation().add(new Vector(x, y, z)));

		screen.redraw();
	}

	public void meshRotate(int meshIdx, double x, double y, double z) {
		if (meshIdx < 0 || meshIdx >= screen.getScene().getMeshes().size())
			return;
		Mesh m = screen.getScene().getMeshes().get(meshIdx);
		m.setRotation(m.getRotation().add(new Vector(x, y, z)));

		screen.redraw();
	}
}
