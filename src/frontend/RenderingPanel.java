package frontend;

import javax.swing.*;
import java.awt.*;

class RenderingPanel extends JPanel {
	private int[] outImage;
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
		g.fillRect(0, 0, width, height);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int argb = outImage[i * width + j];

				g.setColor(new Color(argb));
				g.fillRect(j*pxSize, i*pxSize, pxSize, pxSize);
			}
		}
	}

	public void setPixel(int x, int y, int argb) {
		outImage[y * width + x] = argb;

	}
}