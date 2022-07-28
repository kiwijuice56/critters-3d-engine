import info.gridworld.actor.ActorWorld;
import info.gridworld.grid.BoundedGrid;
import info.gridworld.grid.Location;

import java.awt.Color;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Creates pixel interface using actors from gridworld
 */
public class ScreenWorld extends ActorWorld {
	private final int width, height;
	private Color drawColor, backgroundColor;
	private boolean drawOutline, drawFace, shadeFace, showcaseRotation;

	private Scene scene;

	private byte[] loadedTexture;
	private int textureWidth, textureHeight;

	private int minCanvasX, minCanvasY, maxCanvasX, maxCanvasY;
	private final double[][] depthBuffer;

	private final Vector rotationPerStep = new Vector(0.05, -0.1, 0.02);

	public ScreenWorld(int width, int height) {
		this.scene = new Scene();
		this.width = width;
		this.height = height;
		this.drawColor = Color.BLACK;
		this.backgroundColor = Color.BLACK;
		this.depthBuffer = new double[height][width];

		this.drawOutline = false;
		this.drawFace = true;
		this.shadeFace = true;
		this.showcaseRotation = true;

		// Initialize grid with new actors
		setGrid(new BoundedGrid<>(this.height, this.width));
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				PixelActor p = new PixelActor(this);
				p.putSelfInGrid(getGrid(), new Location(i, j));
			}
		}
		updateCanvasBorder(0, 0);
		updateCanvasBorder(width - 1, height - 1);
		clear();
	}

	@Override
	public void step() {
		clear();
		minCanvasX = width-1; minCanvasY = height-1;
		maxCanvasX = 0; maxCanvasY = 0;
		for (Mesh m : scene.getMeshes()) {
			if (isShowcaseRotation())
				m.setRotation(new Vector(
						m.getRotation().x + rotationPerStep.x,
						m.getRotation().y + rotationPerStep.y,
						m.getRotation().z + rotationPerStep.z));

			Camera.rasterizeMesh(this, m);
		}
	}

	/**
	 * Implements a "Digital Differential Analyzer" algorithm to draw a line at any angle
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

	/**
	 * Draws the edges of a triangle for wireframe mode
	 */
	public void outlineTriangle(Triangle tri) {
		List<Vector> pts = tri.getPts();
		double 	x1 = pts.get(0).x, y1 = pts.get(0).y,
				x2 = pts.get(1).x, y2 = pts.get(1).y,
				x3 = pts.get(2).x, y3 = pts.get(2).y;
		drawLine(x1, y1, x2, y2);
		drawLine(x2, y2, x3, y3);
		drawLine(x3, y3, x1, y1);
	}

	/**
	 * Fills a triangle face
	 */
	public void fillTriangle(Triangle tri) {
		// Sort points by y order
		Integer[] triYOrder = {0, 1, 2};
		Arrays.sort(triYOrder, Comparator.comparingDouble(i -> tri.getPts().get(i).y));

		// Create variables for vertices on the projected triangle and the UV map
		Vector v1 = tri.getPts().get(triYOrder[0]), v2 = tri.getPts().get(triYOrder[1]), v3 = tri.getPts().get(triYOrder[2]);
		Vector t1 = tri.getTPts().get(triYOrder[0]), t2 = tri.getTPts().get(triYOrder[1]), t3 = tri.getTPts().get(triYOrder[2]);

		// Create fourth point to split the triangle into a top-flat and bottom-flat triangle
		Vector v4 = new Vector(v1.x + ((v2.y - v1.y) / (v3.y - v1.y)) * (v3.x - v1.x), v2.y, (v1.z + v3.z) / 2);

		// Match the new point to the UV triangle
		Vector t4 = new Vector(
				(v1.x == v3.x ? t1.x : (t1.x + ((v4.x - v1.x) / (v3.x - v1.x)) * (t3.x - t1.x))),
				(v1.y == v3.y ? t1.y : (t1.y + ((v4.y - v1.y) / (v3.y - v1.y)) * (t3.y - t1.y))), 0);

		// Fill each line and keep track of the legs on each side of the triangle
		double dx1 = (v2.x - v1.x) / (v2.y - v1.y);
		double dx2 = (v4.x - v1.x) / (v4.y - v1.y);

		double scanLines = v2.y - v1.y;
		double du1 = (t2.x - t1.x) / scanLines, du2 = (t4.x - t1.x) / scanLines;
		double dv1 = (t2.y - t1.y) / scanLines, dv2 = (t4.y - t1.y) / scanLines;

		if (dx2 < dx1) {
			double temp = dx2; dx2 = dx1; dx1 = temp; // Swap edge slopes
			temp = du1; du1 = du2; du2 = temp; // Swap texture x slopes
			temp = dv1; dv1 = dv2; dv2 = temp; // Swap texture y slopes
		}

		// Offset the x positions by the distance the y position moves when snapped to a pixel
		double offset = Math.ceil(v1.y) - v1.y;
		double x1 = v1.x + offset * dx1, x2 = v1.x + offset * dx2;
		double tx1 = t1.x, tx2 = t1.x, ty1 = t1.y, ty2 = t1.y;

		for (double y = Math.ceil(v1.y); y < v2.y; y++) {
			double z1 = v1.z + (y - v1.y) * ((v2.z - v1.z) / (v2.y - v1.y));
			double z2 = v1.z + (y - v1.y) * ((v4.z - v1.z) / (v2.y - v1.y));

			for (double x = x1; x < x2; x++) {
				if (y < 0 || x < 0 || y >= height || x >= width)
					continue;
				double z = z1 + (x - x1) * ((z2 - z1) / (x2 - x1));

				if (z > depthBuffer[(int) y][(int) x]) {
					depthBuffer[(int) y][(int) x] = z;

					double tx = tx1 + (tx2 - tx1) * ((x - x1) / (x2 - x1));
					double ty = ty1 + (ty2 - ty1) * ((x - x1) / (x2 - x1));

					setPixelColorTexture((int) x, (int) y, tx, ty);
				}
			}

			x1 += dx1; x2 += dx2;

			tx1 += du1; tx2 += du2;
			ty1 += dv1; ty2 += dv2;

		}

		// Repeat the steps above for the bottom split of the triangle
		offset = Math.floor(v3.y) - v3.y;
		dx1 = (v3.x - v2.x) / (v3.y - v2.y);
		dx2 = (v3.x - v4.x) / (v3.y - v4.y);

		scanLines = Math.abs(v4.y - v3.y);
		du1 = (t2.x - t3.x) / scanLines; du2 = (t4.x - t3.x) / scanLines;
		dv1 = (t2.y - t3.y) / scanLines; dv2 = (t4.y - t3.y) / scanLines;

		if (dx2 > dx1) {
			double temp = dx2; dx2 = dx1; dx1 = temp;
			temp = du1; du1 = du2; du2 = temp;
			temp = dv1; dv1 = dv2; dv2 = temp;
		}

		x1 = v3.x + offset * dx1; x2 = v3.x + offset * dx2;

		tx1 = t3.x; tx2 = t3.x;
		ty1 = t3.y; ty2 = t3.y;

		for (double y = Math.floor(v3.y); y > v4.y; y--) {
			double z1 = v3.z + (y - v3.y) * ((v2.z - v3.z) / (v4.y - v3.y));
			double z2 = v3.z + (y - v3.y) * ((v4.z - v3.z) / (v2.y - v3.y));

			for (double x = x1; x < x2; x++) {
				if (y < 0 || x < 0 || y >= height || x >= width)
					continue;
				double z = z1 + (x - x1) * ((z2 - z1) / (x2 - x1));
				if (z > depthBuffer[(int) y][(int) x]) {
					depthBuffer[(int) y][(int) x] = z;

					double tx = tx1 + (tx2 - tx1) * ((x - x1) / (x2 - x1));
					double ty = ty1 + (ty2 - ty1) * ((x - x1) / (x2 - x1));

					setPixelColorTexture((int) x, (int) y, tx, ty);
				}
			}

			x1 -= dx1; x2 -= dx2;

			tx1 += du1; tx2 += du2;
			ty1 += dv1; ty2 += dv2;
		}
	}

	/**
	 * Paints a pixel color onto the screen given screen and UV coordinates
	 */
	private void setPixelColorTexture(int x, int y, double tx, double ty) {
		if (loadedTexture != null) {
			int col = (int) (ty * textureHeight);
			int row = (int) (tx * textureWidth);

			int idx = (col * textureWidth + row) * 4 ;

			if (idx >= loadedTexture.length)
				return;
			int argb = 0;
			argb += (((int) loadedTexture[idx] & 0xff) << 24); // alpha
			argb += ((int) loadedTexture[idx + 1] & 0xff); // blue
			argb += (((int) loadedTexture[idx + 2] & 0xff) << 8); // green
			argb += (((int) loadedTexture[idx + 3] & 0xff) << 16); // red

			setPixelColor(x, y, ColorHelper.blendColor(new Color(argb), getDrawColor()));
		} else {
			setPixelColor(x, y, getDrawColor());
		}
	}

	/**
	 * Paints a pixel color onto the screen given screen coordinates and a color
	 */
	public void setPixelColor(int x, int y, Color c) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return;
		updateCanvasBorder(x, y);
		getGrid().get(new Location(y, x)).setColor(c);
	}

	public void clear() {
		for (int i = minCanvasY; i <= maxCanvasY; i++){
			for (int j = minCanvasX; j <= maxCanvasX; j++) {
				setPixelColor(j, i, backgroundColor);
				depthBuffer[i][j] = Integer.MIN_VALUE;
			}
		}
	}

	private void updateCanvasBorder(int x, int y) {
		minCanvasX = Math.min(minCanvasX, x);
		minCanvasY = Math.min(minCanvasY, y);
		maxCanvasX = Math.max(maxCanvasX, x);
		maxCanvasY = Math.max(maxCanvasY, y);
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
		updateCanvasBorder(0, 0);
		updateCanvasBorder(width-1, height-1);
		this.backgroundColor = backgroundColor;
	}

	public boolean isDrawOutline() {
		return drawOutline;
	}

	public void setDrawOutline(boolean drawOutline) {
		this.drawOutline = drawOutline;
	}

	public boolean isDrawFace() {
		return drawFace;
	}

	public void setDrawFace(boolean drawFace) {
		this.drawFace = drawFace;
	}

	public boolean isShadeFace() {
		return shadeFace;
	}

	public void setShadeFace(boolean shadeFace) {
		this.shadeFace = shadeFace;
	}

	public boolean isShowcaseRotation() {
		return showcaseRotation;
	}

	public void setShowcaseRotation(boolean showcaseRotation) {
		this.showcaseRotation = showcaseRotation;
	}

	public byte[] getLoadedTexture() {
		return loadedTexture;
	}

	public void setLoadedTexture(byte[] loadedTexture) {
		this.loadedTexture = loadedTexture;
	}

	public int getTextureWidth() {
		return textureWidth;
	}

	public void setTextureWidth(int textureWidth) {
		this.textureWidth = textureWidth;
	}

	public int getTextureHeight() {
		return textureHeight;
	}

	public void setTextureHeight(int textureHeight) {
		this.textureHeight = textureHeight;
	}
}
