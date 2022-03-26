import java.util.List;

/**
 * Represents 3D triangle
 */
public class Triangle {
	private List<Vector> pts;
	private Vector normal;

	public Triangle(List<Vector> pts) {
		this.pts = pts;
		this.normal = new Vector();
	}

	@Override
	public String toString() {
		return "T=%s".formatted(pts);
	}

	public List<Vector> getPts() {
		return pts;
	}

	public void setPts(List<Vector> pts) {
		this.pts = pts;
	}

	public Vector getNormal() {
		return normal;
	}

	/**
	 * Recalculates own normal given a transformed list of points using Newell's Method
	 * @param transPts The transformed points
	 */
	public void calculateNormal(List<Vector> transPts) {
		Vector u = transPts.get(1).subtract(transPts.get(0));
		Vector v = transPts.get(2).subtract(transPts.get(0));
		normal.x = (u.y * v.z) - (u.z * v.y);
		normal.y = (u.z * v.x) - (u.x * v.z);
		normal.z = (u.x * v.y) - (u.y * v.x);
	}
}
