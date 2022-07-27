import java.awt.Color;
import java.util.List;

/**
 * Represents a collection of Triangles with a position and rotation
 */
public class Mesh {
	private final List<Triangle> tris;
	private Vector rotation;
	private Vector translation;
	private Color modulate;

	public Mesh(List<Triangle> tris) {
		this.tris = tris;
		this.rotation = new Vector(0, 0, 0);
		this.translation = new Vector(0,0,0);
		this.modulate = Color.WHITE;
	}

	/* * * Accessor and mutator methods * * */

	@Override
	public String toString() {
		return "M=[%s, rot=%s, trans=%s]".formatted(tris, rotation, translation);
	}

	public List<Triangle> getTris() {
		return tris;
	}

	public Vector getRotation() {
		return rotation;
	}

	public void setRotation(Vector rotation) {
		this.rotation = rotation;
	}

	public Vector getTranslation() {
		return translation;
	}

	public void setTranslation(Vector translation) {
		this.translation = translation;
	}

	public Color getModulate() {
		return modulate;
	}

	public void setModulate(Color modulate) {
		this.modulate = modulate;
	}
}
