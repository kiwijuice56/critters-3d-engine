import java.awt.*;

public class ColorHelper {
	public static Color tintColor(Color a, Color b) {
		return new Color(
				Math.min(255, a.getRed() + b.getRed()),
				Math.min(255, a.getGreen() + b.getGreen()),
				Math.min(255, a.getBlue() + b.getBlue()));
	}

	public static Color blendColor(Color a, Color b) {
		return new Color(
				(int) (255 * (b.getRed() / 255.0) * (a.getRed() / 255.0)),
				(int) (255 * (b.getGreen() / 255.0) * (a.getGreen() / 255.0)),
				(int) (255 * (b.getBlue() / 255.0) * (a.getBlue() / 255.0)));
	}
}
