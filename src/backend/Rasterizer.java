package backend;

/**
 * Creates pixel interface using actors from gridworld
 */
public class Rasterizer {
	private int fillColor, lineColor;
	private final int width, height;
	private byte[] loadedTexture;
	private int textureWidth, textureHeight;

	// Store status of the screen
	private final double[][] depthBuffer;

	public Rasterizer(int width, int height) {
		this.width = width;
		this.height = height;

		this.fillColor = 0x000000;
		this.lineColor = 0x000000;
		this.depthBuffer = new double[height][width];

		clear();
	}

	/**
	 * Draws each mesh in a scene using the camera and lights
	 */
	public void rasterizeScene(Scene scene) {
		clear();

		for (Mesh m : scene.getMeshes()) {
			scene.getMainCamera().rasterizeMesh(this, scene.getLights(), m);
		}
	}

	/**
	 * Draws line from p1 to p2 using edgeColor
	 */
	public void drawEdge(Vector p1, Vector p2) {
		double x1 = p1.x, x2 = p2.x, y1 = p1.y, y2 = p2.y, z1 = p1.z, z2 = p2.z;
		if (x1 > x2) {
			double temp = x2; x2 = x1; x1 = temp;
			temp = y2; y2 = y1; y1 = temp;
			temp = z2; z2 = z1; z1 = temp;
		}

		double pixelCnt = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
		double dx = (x2 - x1) / pixelCnt, dy = (y2 - y1) / pixelCnt, dz = (z2 - z1) / pixelCnt;

		for (int i = 0; i < pixelCnt; i++) {
			if (x1 < 0 || y1 < 0 || x1 >= width || y1 >= height || depthBuffer[(int) y1][(int) x1] > z1)
				continue;
			depthBuffer[(int) y1][(int) x1] = z1;

			setPixelColor((int) x1, (int) y1, lineColor);

			x1 += dx;
			y1 += dy;
			z1 += dz;
		}
	}

	/**
	 * Draws the edges of a triangle for wireframe mode
	 */
	public void outlineTriangle(Triangle tri) {
		drawEdge(tri.getPts().get(0), tri.getPts().get(1));
		drawEdge(tri.getPts().get(1), tri.getPts().get(2));
		drawEdge(tri.getPts().get(2), tri.getPts().get(0));
	}

	/**
	 * Fills a triangle face
	 */
	public void fillTriangle(Triangle tri) {
		// Create variables for vertices on the projected triangle and the UV map
		Vector p1 = tri.getPts().get(0), p2 = tri.getPts().get(1), p3 = tri.getPts().get(2);
		Vector t1 = tri.getTPts().get(0), t2 = tri.getTPts().get(1), t3 = tri.getTPts().get(2);

		// Sort points by y order with bubble sort
		if (p1.y > p2.y) {
			Vector temp = p1; p1 = p2; p2 = temp;
			temp = t1; t1 = t2; t2 = temp;
		}
		if (p2.y > p3.y) {
			Vector temp = p2; p2 = p3; p3 = temp;
			temp = t2; t2 = t3; t3 = temp;
		}
		if (p1.y > p2.y) {
			Vector temp = p1; p1 = p2; p2 = temp;
			temp = t1; t1 = t2; t2 = temp;
		}

		// Snap points to pixel space
		p1.x = (int) p1.x; p1.y = (int) p1.y;
		p2.x = (int) p2.x; p2.y = (int) p2.y;
		p3.x = (int) p3.x; p3.y = (int) p3.y;


		// Calculate starting and end points for triangle lines in screen and texture space
		double dx1 = p2.x - p1.x, dx2 = p3.x - p1.x;
		double dy1 = p2.y - p1.y, dy2 = p3.y - p1.y;
		double dz1 = p2.z - p1.z, dz2 = p3.z - p1.z;

		double du1 = t2.x - t1.x, du2 = t3.x - t1.x;
		double dv1 = t2.y - t1.y, dv2 = t3.y - t1.y;


		// Calculate the slope of each line

		double xStep1 = 0, xStep2 = 0, uStep1 = 0, uStep2 = 0, vStep1 = 0, vStep2 = 0, zStep1 = 0, zStep2 = 0;

		if (dy1 != 0) xStep1 = dx1 / Math.abs(dy1);
		if (dy1 != 0) zStep1 = dz1 / Math.abs(dy1);
		if (dy1 != 0) uStep1 = du1 / Math.abs(dy1);
		if (dy1 != 0) vStep1 = dv1 / Math.abs(dy1);

		if (dy2 != 0) xStep2 = dx2 / Math.abs(dy2);
		if (dy2 != 0) zStep2 = dz2 / Math.abs(dy2);
		if (dy2 != 0) uStep2 = du2 / Math.abs(dy2);
		if (dy2 != 0) vStep2 = dv2 / Math.abs(dy2);

		// Iterate through each scan line and draw a horizontal line using the calculated start and end points for x, z and UV
		// Stop after reaching the end of the p1 -> p2 line, as this is where the triangle is no longer bottom flat
		for (double y = p1.y; y < p2.y; y++) {
			double x1 = p1.x + xStep1 * (y - p1.y);
			double x2 = p1.x + xStep2 * (y - p1.y);

			double z1 = p1.z + zStep1 * (y - p1.y);
			double z2 = p1.z + zStep2 * (y - p1.y);

			double u1 = t1.x + uStep1 * (y - p1.y);
			double u2 = t1.x + uStep2 * (y - p1.y);

			double v1 = t1.y + vStep1 * (y - p1.y);
			double v2 = t1.y + vStep2 * (y - p1.y);

			if (x2 < x1) {
				double temp = x1; x1 = x2; x2 = temp;
				temp = z1; z1 = z2; z2 = temp;
				temp = u1; u1 = u2; u2 = temp;
				temp = v1; v1 = v2; v2 = temp;
			}

			for (double x = x1; x < x2; x++) {
				double progress = (x - x1) / (x2 - x1);
				double z = z1 + (z2 - z1) * progress;

				if (x < 0 || y < 0 || x >= width || y >= height || depthBuffer[(int) y][(int) x] > z)
					continue;
				depthBuffer[(int) y][(int) x] = z;

				double u = u1 + (u2 - u1) * progress;
				double v = v1 + (v2 - v1) * progress;

				setPixelColorTexture((int) x, (int) y, u, v);
			}
		}

		// Repeat the process for the other line to draw a top flat triangle

		dx1 = p3.x - p2.x;
		dy1 = p3.y - p2.y;
		dz1 = p3.z - p2.z;

		du1 = t3.x - t2.x;
		dv1 = t3.y - t2.y;

		xStep1 = 0; uStep1 = 0; vStep1= 0; zStep1 = 0;

		if (dy1 != 0) xStep1 = dx1 / Math.abs(dy1);
		if (dy1 != 0) uStep1 = du1 / Math.abs(dy1);
		if (dy1 != 0) vStep1 = dv1 / Math.abs(dy1);
		if (dy1 != 0) zStep1 = dz1 / Math.abs(dy1);

		for (double y = p2.y; y < p3.y; y++) {
			double x1 = p2.x + xStep1 * (y - p2.y);
			double x2 = p1.x + xStep2 * (y - p1.y);

			double z1 = p2.z + zStep1 * (y - p2.y);
			double z2 = p1.z + zStep2 * (y - p1.y);

			double u1 = t2.x + uStep1 * (y - p2.y);
			double u2 = t1.x + uStep2 * (y - p1.y);

			double v1 = t2.y + vStep1 * (y - p2.y);
			double v2 = t1.y + vStep2 * (y - p1.y);

			if (x2 < x1) {
				double temp = x1; x1 = x2; x2 = temp;
				temp = z1; z1 = z2; z2 = temp;
				temp = u1; u1 = u2; u2 = temp;
				temp = v1; v1 = v2; v2 = temp;
			}

			for (double x = x1; x < x2; x++) {
				double progress = (x - x1) / (x2 - x1);
				double z = z1 + (z2 - z1) * progress;

				if (x < 0 || y < 0 || x >= width || y >= height || depthBuffer[(int) y][(int) x] > z)
					continue;
				depthBuffer[(int) y][(int) x] = z;

				double u = u1 + (u2 - u1) * progress;
				double v = v1 + (v2 - v1) * progress;

				setPixelColorTexture((int) x, (int) y, u, v);
			}
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

			setPixelColor(x, y, ColorHelper.blendColor(argb, fillColor));
		} else {
			setPixelColor(x, y, getFillColor());
		}
	}

	/**
	 * Paints a pixel color onto the screen given screen coordinates and a color
	 */
	public void setPixelColor(int x, int y, int argb) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return;

		// Front end implements how to draw each pixel
	}


	public void clear() {
		for (int i = 0; i < height; i++){
			for (int j = 0; j < width; j++) {
				setPixelColor(j, i, -1);
				depthBuffer[i][j] = Integer.MIN_VALUE;
			}
		}
	}

	/* * * Accessor and mutator methods * * */

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getFillColor() {
		return fillColor;
	}

	public void setFillColor(int fillColor) {
		this.fillColor = fillColor;
	}

	public int getLineColor() {
		return lineColor;
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
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
