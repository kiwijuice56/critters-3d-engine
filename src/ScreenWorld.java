import info.gridworld.actor.ActorWorld;
import info.gridworld.grid.BoundedGrid;
import info.gridworld.grid.Location;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Creates pixel interface using actors from gridworld
 */
public class ScreenWorld extends ActorWorld {
	private Scene scene;
	private final int width, height;
	private Color drawColor;
	private Color backgroundColor;
	private boolean drawOutline;

	public ScreenWorld(int width, int height) {
		this.scene = new Scene();
		this.width = width;
		this.height = height;
		this.drawColor = Color.BLACK;
		this.backgroundColor = Color.WHITE;

		// Initialize grid with new actors
		setGrid(new BoundedGrid<>(this.height, this.width));
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				PixelActor p = new PixelActor(this);
				p.putSelfInGrid(getGrid(), new Location(i, j));
			}
		}
	}

	@Override
	public void step() {
		clear();
		for (Mesh m : scene.getMeshes()) {
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
			setPixelColor((int) x1, (int) y1, drawColor);

			x1 += moveX;
			y1 += moveY;
		}
	}

	public void drawTriangle(Triangle tri) {
		List<Vector> pts = tri.getPts();
		double 	x1 = pts.get(0).x, y1 = pts.get(0).y,
				x2 = pts.get(1).x, y2 = pts.get(1).y,
				x3 = pts.get(2).x, y3 = pts.get(2).y;
		drawLine(x1, y1, x2, y2);
		drawLine(x2, y2, x3, y3);
		drawLine(x3, y3, x1, y1);
	}

	public void fillTriangle(Triangle tri) {
		List<Vector> sortedYPts = new ArrayList<>(tri.getPts());
		sortedYPts.sort(Comparator.comparingDouble(o -> o.y));

		Vector v1 = sortedYPts.get(0), v2 = sortedYPts.get(1), v3 = sortedYPts.get(2);
		// Create fourth point to split the triangle into a top-flat and bottom-flat triangle
		Vector v4 = new Vector(v1.x + ((v2.y - v1.y) / (v3.y - v1.y)) * (v3.x - v1.x), v2.y, 0);

		// Fill each line and keep track of the legs on each side of the triangle
		double slope1 = (v2.x - v1.x) / (v2.y - v1.y);
		double slope2 = (v4.x - v1.x) / (v4.y - v1.y);

		// Offset the x positions by the distance the y position moves when snapped to a pixel
		double offset = Math.ceil(v1.y) - v1.y;
		double startX = v1.x + offset * slope1, endX = v1.x + offset * slope2;

		for (double line = Math.ceil(v1.y); line < v2.y; line++) {
			drawLine(startX, line, endX, line);
			startX += slope1;
			endX += slope2;
		}

		// Repeat the steps above for the bottom half of the triangle
		offset = Math.floor(v3.y) - v3.y;
		slope1 = (v3.x - v2.x) / (v3.y - v2.y);
		slope2 = (v3.x - v4.x) / (v3.y - v4.y);
		startX = v3.x + offset * slope1; endX = v3.x + offset * slope2;

		for (double line = Math.floor(v3.y); line > v4.y; line--) {
			drawLine(startX, line, endX, line);
			startX -= slope1;
			endX -= slope2;
		}

		if (isDrawOutline()) {
			setDrawColor(Color.BLACK);
			drawTriangle(tri);
		}
	}

	public void setPixelColor(int x, int y, Color c) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return;
		getGrid().get(new Location(y, x)).setColor(c);
	}

	public void clear() {
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				setPixelColor(j, i, backgroundColor);
	}

	/* * * Accessor and mutator methods * * */

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public Color getDrawColor() {
		return drawColor;
	}

	public void setDrawColor(Color drawColor) {
		this.drawColor = drawColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public boolean isDrawOutline() {
		return drawOutline;
	}

	public void setDrawOutline(boolean drawOutline) {
		this.drawOutline = drawOutline;
	}
}
