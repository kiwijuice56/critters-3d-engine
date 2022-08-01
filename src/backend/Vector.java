package backend;

/**
 * Represents a 3D point or direction
 */
public class Vector {
	// public for ease of access
	public double x, y, z;

	public Vector() {
		this(0, 0, 0);
	}

	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector sub(Vector other) {
		return new Vector(x - other.x, y - other.y, z - other.z);
	}

	public Vector add(Vector other) {
		return new Vector(x + other.x, y + other.y, z + other.z);
	}

	public double dot(Vector other) {
		return other.x * x + other.y * y + other.z * z;
	}

	/* * * Accessor and mutator methods * * */

	@Override
	public String toString() {
		return "(%.3f,%.3f,%.3f)".formatted(x, y, z);
	}

	public Vector normalized() {
		double length = length();
		return new Vector(x/length, y/length, z/length);
	}

	public double length() {
		return Math.sqrt(x*x + y*y + z*z);
	}
}
