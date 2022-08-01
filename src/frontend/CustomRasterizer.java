package frontend;

import backend.Rasterizer;

/**
 * Connects Rasterizer to RenderingPanel
 */

class CustomRasterizer extends Rasterizer {
	private final RenderingPanel panel;

	public CustomRasterizer(RenderingPanel panel, int width, int height) {
		super(width, height);
		this.panel = panel;
	}

	@Override
	public void setPixelColor(int x, int y, int argb) {
		super.setPixelColor(x, y, argb);
		if (panel != null)
			panel.setPixel(x, y, argb);
	}
}
