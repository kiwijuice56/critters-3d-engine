import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents 3D triangle
 */
public class Triangle {
	private final List<Vector> pts;
	private final List<Vector> tPts;
	private Color color;

	public Triangle(List<Vector> pts) {
		this(pts, new ArrayList<>(Arrays.asList(
				new Vector(0, 0, 0),
				new Vector(1, 1, 0),
				new Vector(0, 1, 0))));
	}

	public Triangle(List<Vector> pts, List<Vector> tPts) {
		this.pts = pts;
		this.tPts = tPts;
	}

	/**
	 * Calculates and returns normal of a triangle using the cross-product of two edges
	 * @param tri
	 */
	public static Vector calculateNormal(Triangle tri) {
		List<Vector> pts = tri.getPts();

		Vector u = pts.get(1).subtract(pts.get(0));
		Vector v = pts.get(2).subtract(pts.get(0));
		Vector normal = new Vector();

		normal.x = (u.y * v.z) - (u.z * v.y);
		normal.y = (u.z * v.x) - (u.x * v.z);
		normal.z = (u.x * v.y) - (u.y * v.x);
		return  normal.normalized();
	}

	/* * * Accessor and mutator methods * * */

	@Override
	public String toString() {
		return "T=%s".formatted(pts);
	}

	public List<Vector> getPts() {
		return pts;
	}

	public List<Vector> getTPts() {
		return tPts;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void tintColor(Color add) {
		this.color = new Color(
				Math.min(255, this.color .getRed() + add.getRed()),
				Math.min(255, this.color .getGreen() + add.getGreen()),
				Math.min(255, this.color .getBlue() + add.getBlue()));
	}

	public static Color blendColor(Color blend1, Color blend2) {
		return new Color(
				(int) (255 * (blend2.getRed() / 255.0) * (blend1.getRed() / 255.0)),
				(int) (255 * (blend2.getGreen() / 255.0) * (blend1.getGreen() / 255.0)),
				(int) (255 * (blend2.getBlue() / 255.0) * (blend1.getBlue() / 255.0)));
	}

	public double getCenterDepth() {
		return (pts.get(0).z + pts.get(1).z + pts.get(2).z) / 3.0;
	}
}
