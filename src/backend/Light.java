package backend;

/**
 * Applies global sun lighting to meshes in a scene
 */
public class Light {
	private Vector direction;
	private int color;
	private double strength;

	public Light(Vector dir, int color, double strength) {
		this.direction = dir.normalized();
		this.color = color;
		this.strength = Math.min(1, Math.max(0, strength));
	}

	/* * * Accessor and mutator methods * * */

	public double getStrength() {
		return strength;
	}

	public void setStrength(double strength) {
		this.strength = Math.min(1, Math.max(0, strength));
	}

	public void setDirection(Vector direction) {
		this.direction = direction.normalized();
	}

	public Vector getDirection() {
		return direction;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
}
