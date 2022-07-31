package backend;


/**
 * Applies global sun lighting to meshes in a scene
 */
public class Light {
	private Vector dir;
	private int color;
	private double strength;

	public Light(Vector dir, int color, double strength) {
		this.dir = dir.normalized();
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

	public void setDir(Vector dir) {
		this.dir = dir.normalized();
	}

	public Vector getDir() {
		return dir;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
}
