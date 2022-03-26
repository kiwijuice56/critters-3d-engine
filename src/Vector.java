/**
 * Represents a 3D point
 */
public class Vector {
	public double x, y, z;

	public Vector() {
		this(0, 0, 0);
	}

	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Returns the result of subtracting the other vector from this vector
	 * @param other
	 * @return This vector - other vector
	 */
	public Vector subtract(Vector other) {
		Vector subtracted = new Vector();
		subtracted.x = x - other.x;
		subtracted.y = y - other.y;
		subtracted.z = z - other.z;
		return subtracted;
	}

	@Override
	public String toString() {
		return "(%.3f,%.3f,%.3f)".formatted(x, y, z);
	}
}
