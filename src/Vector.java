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

	@Override
	public String toString() {
		return "(%.3f,%.3f,%.3f)".formatted(x, y, z);
	}
}
