import info.gridworld.actor.ActorWorld;
import info.gridworld.grid.BoundedGrid;
import info.gridworld.grid.Location;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates pixel interface using actors from gridworld
 */
public class ScreenWorld extends ActorWorld {
	private List<Mesh> scene;
	private final int width, height;

	public ScreenWorld(int width, int height) {
		scene = new ArrayList<>();
		this.width = width;
		this.height = height;

		// Initialize grid with new actors
		setGrid(new BoundedGrid<>(this.height, this.width));
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				PixelActor p = new PixelActor();
				getGrid().put(new Location(i, j), p);
			}
		}
	}

	@Override
	public void step() {
		clear();
		for (Mesh m : scene) {
			// Rotation transformation here is a temporary choice to allow objects to rotate in every step
			m.setRotation(new Vector(m.getRotation().x + 0.075, m.getRotation().y + 0.1, m.getRotation().z));

			Camera.rasterizeMesh(this, m);
		}
	}

	/**
	 * Implements a "Digital Differential Analyzer" algorithm to draw a line
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public void drawLine(double x1, double y1, double x2, double y2) {
		if (x1 > x2) {
			double temp = x2;
			x2 = x1;
			x1 = temp;

			temp = y2;
			y2 = y1;
			y1 = temp;
		}

		double pixelCnt = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
		double moveX = (x2 - x1) / pixelCnt, moveY = (y2 - y1) / pixelCnt;

		for (int i = 0; i < pixelCnt; i++) {
			setPixelColor((int) Math.floor(x1), (int) Math.floor(y1), Color.CYAN);

			x1 += moveX;
			y1 += moveY;
		}
	}

	/**
	 * Draws a triangle with three calls to drawLine
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 */
	public void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3) {
		drawLine(x1, y1, x2, y2);
		drawLine(x2, y2, x3, y3);
		drawLine(x3, y3, x1, y1);
	}

	public void drawTriangle(List<Vector> pts) {
		drawTriangle(pts.get(0).x, pts.get(0).y, pts.get(1).x, pts.get(1).y, pts.get(2).x, pts.get(2).y);
	}

	public void setPixelColor(int x, int y, Color c) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return;
		getGrid().get(new Location(y, x)).setColor(c);
	}

	public void clear() {
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				setPixelColor(j, i, Color.BLACK);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public List<Mesh> getScene() {
		return scene;
	}

	public void setScene(List<Mesh> scene) {
		this.scene = scene;
	}
}
