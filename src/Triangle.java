import java.awt.Color;
import java.util.List;

/**
 * Represents 3D triangle
 */
public class Triangle {
	private final List<Vector> pts;
	private Color color;

	public Triangle(List<Vector> pts) {
		this.pts = pts;
	}

	/**
	 * Calculates and returns normal of a triangle using the crossproduct of two edges
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

	public void blendColor(Color blend) {
		this.color = new Color(
				(int) (255 * (this.color.getRed() / 255.0) * (blend.getRed() / 255.0)),
				(int) (255 * (this.color.getGreen() / 255.0) * (blend.getGreen() / 255.0)),
				(int) (255 * (this.color.getBlue() / 255.0) * (blend.getBlue() / 255.0)));
	}

	public double getCenterDepth() {
		return (pts.get(0).z + pts.get(1).z + pts.get(2).z) / 3.0;
	}
}
