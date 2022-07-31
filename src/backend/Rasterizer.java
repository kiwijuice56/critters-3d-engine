package backend;

import java.awt.Color;
import java.util.List;

/**
 * Creates pixel interface using actors from gridworld
 */
public class Rasterizer {
	private int drawColor, edgeColor;
	private int width, height;
	private byte[] loadedTexture;
	private int textureWidth, textureHeight;

	private int minCanvasX, minCanvasY, maxCanvasX, maxCanvasY;
	private final double[][] depthBuffer;

	public Rasterizer(int width, int height) {
		this.width = width;
		this.height = height;

		this.drawColor = 0x000000;
		this.edgeColor = 0xFFFFFF;
		this.depthBuffer = new double[height][width];

		minCanvasX = 0; minCanvasY = 0;
		maxCanvasX = width-1; maxCanvasY = height-1;
		clear();
	}

	public void rasterizeScene(Scene scene) {
		clear();
		minCanvasX = width-1; minCanvasY = height-1;
		maxCanvasX = 0; maxCanvasY = 0;

		for (Mesh m : scene.getMeshes()) {
			scene.getMainCamera().rasterizeMesh(this, scene.getLights(), m);
		}
	}

	/**
	 * Implements a "Digital Differential Analyzer" algorithm to draw a line at any angle
	 */
	public void drawEdge(double x1, double y1, double x2, double y2) {
		if (x1 > x2) {
			double temp = x2;
			x2 = x1;
			x1 = temp;

			temp = y2;
			y2 = y1;
			y1 = temp;
		}

		double pixelCnt = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
		double dx = (x2 - x1) / pixelCnt, dy = (y2 - y1) / pixelCnt;

		for (int i = 0; i < pixelCnt; i++) {
			setPixelColor((int) x1, (int) y1, edgeColor);

			x1 += dx;
			y1 += dy;
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
		drawEdge(x1, y1, x2, y2);
		drawEdge(x2, y2, x3, y3);
		drawEdge(x3, y3, x1, y1);
	}

	/**
	 * Fills a triangle face
	 */
	public void fillTriangle(Triangle tri) {
		// Create variables for vertices on the projected triangle and the UV map
		Vector v1 = tri.getPts().get(0), v2 = tri.getPts().get(1), v3 = tri.getPts().get(2);
		Vector t1 = tri.getTPts().get(0), t2 = tri.getTPts().get(1), t3 = tri.getTPts().get(2);

		// Sort points by y order with bubble sort
		if (v1.y > v2.y) {
			Vector temp = v1; v1 = v2; v2 = temp;
			temp = t1; t1 = t2; t2 = temp;
		}
		if (v2.y > v3.y) {
			Vector temp = v2; v2 = v3; v3 = temp;
			temp = t2; t2 = t3; t3 = temp;
		}
		if (v1.y > v2.y) {
			Vector temp = v1; v1 = v2; v2 = temp;
			temp = t1; t1 = t2; t2 = temp;
		}

		// Create fourth point to split the triangle into a top-flat and bottom-flat triangle
		Vector v4 = new Vector(
				v1.x + ((v2.y - v1.y) / (v3.y - v1.y)) * (v3.x - v1.x),
				v2.y, (v1.z + v3.z) / 2);

		// Match the new point to the UV triangle

		Vector t4 = new Vector(
				(v1.x == v3.x ? t2.x : (t1.x + ((v4.x - v1.x) / (v3.x - v1.x)) * (t3.x - t1.x))),
				(v1.y == v3.y ? t2.y : (t1.y + ((v4.y - v1.y) / (v3.y - v1.y)) * (t3.y - t1.y))), 0);

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

		double zyRatio1 = ((v2.z - v1.z) / (v2.y - v1.y)), zyRatio2 = ((v4.z - v1.z) / (v2.y - v1.y));

		for (double y = Math.ceil(v1.y); y < v2.y; y++) {
			double z1 = v1.z + (y - v1.y) * zyRatio1;
			double z2 = v1.z + (y - v1.y) * zyRatio2;

			for (double x = x1; x < x2; x++) {
				if (y < 0 || x < 0 || y >= height || x >= width)
					continue;
				double z = z1 + (x - x1) * ((z2 - z1) / (x2 - x1));

				if (z > depthBuffer[(int) y][(int) x]) {
					depthBuffer[(int) y][(int) x] = z;

					if (loadedTexture != null) {
						double tx = tx1 + (tx2 - tx1) * ((x - x1) / (x2 - x1));
						double ty = ty1 + (ty2 - ty1) * ((x - x1) / (x2 - x1));

						setPixelColorTexture((int) x, (int) y, tx, ty);
					} else {
						setPixelColor((int) x, (int) y, getDrawColor());
					}
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

		zyRatio1 = ((v2.z - v3.z) / (v4.y - v3.y)); zyRatio2 = ((v4.z - v3.z) / (v2.y - v3.y));

		for (double y = Math.floor(v3.y); y > v4.y; y--) {
			double z1 = v3.z + (y - v3.y) * zyRatio1;
			double z2 = v3.z + (y - v3.y) * zyRatio2;

			for (double x = x1; x < x2; x++) {
				if (y < 0 || x < 0 || y >= height || x >= width)
					continue;
				double z = z1 + (x - x1) * ((z2 - z1) / (x2 - x1));
				if (z > depthBuffer[(int) y][(int) x]) {
					depthBuffer[(int) y][(int) x] = z;

					if (loadedTexture != null) {
						double tx = tx1 + (tx2 - tx1) * ((x - x1) / (x2 - x1));
						double ty = ty1 + (ty2 - ty1) * ((x - x1) / (x2 - x1));

						setPixelColorTexture((int) x, (int) y, tx, ty);
					} else {
						setPixelColor((int) x, (int) y, getDrawColor());
					}
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

			int idx = (col * textureWidth + row) * 4;

			if (idx >= loadedTexture.length)
				return;
			// https://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
			int argb = 0;
			argb += (((int) loadedTexture[idx + 3] & 0xff) << 16); // red
			argb += (((int) loadedTexture[idx + 2] & 0xff) << 8); // green
			argb += ((int) loadedTexture[idx + 1] & 0xff); // blue
			argb += (((int) loadedTexture[idx] & 0xff) << 24); // alpha

			setPixelColor(x, y, ColorHelper.blendColor(argb, drawColor));
		} else {
			setPixelColor(x, y, getDrawColor());
		}
	}

	/**
	 * Paints a pixel color onto the screen given screen coordinates and a color
	 */
	public void setPixelColor(int x, int y, int argb) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return;
		updateCanvasBorder(x, y);

		// Up to front end implementation

	}


	public void clear() {
		for (int i = minCanvasY; i <= maxCanvasY; i++){
			for (int j = minCanvasX; j <= maxCanvasX; j++) {
				setPixelColor(j, i, 0);
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

	public int getDrawColor() {
		return drawColor;
	}

	public void setDrawColor(int drawColor) {
		this.drawColor = drawColor;
	}

	public int getEdgeColor() {
		return edgeColor;
	}

	public void setEdgeColor(int edgeColor) {
		this.edgeColor = edgeColor;
	}

	public byte[] getLoadedTexture() {
		return loadedTexture;
	}

	public void setLoadedTexture(byte[] loadedTexture, int textureWidth, int textureHeight) {
		this.loadedTexture = loadedTexture;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}
}
