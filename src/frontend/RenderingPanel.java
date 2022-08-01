package frontend;

import javax.swing.*;
import java.awt.*;

/**
 * Uses java standard library UI to draw output of a Rasterizer
 */

class RenderingPanel extends JPanel {
	private final int[] outImage;
	private int width, height, pxSize;

	public RenderingPanel(int width, int height, int pxSize) {
		this.width = width;
		this.height = height;
		this.pxSize = pxSize;

		this.outImage = new int[width * height * pxSize * pxSize];

		setSize(new Dimension(width * pxSize, height * pxSize));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width*pxSize, height*pxSize);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int argb = outImage[i * width + j];
				if (argb == -1)
					continue;
				g.setColor(new Color(argb));
				g.fillRect(j*pxSize, i*pxSize, pxSize, pxSize);
			}
		}
	}

	public void setPixel(int x, int y, int argb) {
		outImage[y * width + x] = argb;
	}
}