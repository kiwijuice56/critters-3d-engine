package backend;


public class ColorHelper {
	public static int tintColor(int a, int b, double bStrength) {
		return
				Math.min(255, ((a & 0xFF0000) >> 16) + (int) (((b & 0xFF0000) >> 16) * bStrength)) << 16 |
				Math.min(255, ((a & 0x00FF00) >> 8) + (int) (((b & 0x00FF00) >> 8) * bStrength)) << 8 |
				Math.min(255, ((a & 0x0000FF) + (int) ((b & 0x0000FF) * bStrength)));
	}

	public static int blendColor(int a, int b) {
		return
				(int) (255 * (((a & 0xFF0000) >> 16) / 255.0) * (((b & 0xFF0000) >> 16) / 255.0)) << 16 |
				(int) (255 * (((a & 0x00FF00) >> 8) / 255.0) * (((b & 0x00FF00) >> 8) / 255.0)) << 8 |
				(int) (255 * ((a & 0x0000FF) / 255.0) * ((b & 0x0000FF) / 255.0));
	}
}
