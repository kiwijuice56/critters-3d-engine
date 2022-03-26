import java.util.List;

/**
 * Represents a collection of Triangles with a position and rotation
 */
public class Mesh {
	private List<Triangle> tris;
	private Vector rotation;
	private Vector translation;

	public Mesh(List<Triangle> tris) {
		this.tris = tris;
		this.rotation = new Vector(0, 0, 0);
		this.translation = new Vector(0,0,0);
	}

	@Override
	public String toString() {
		return "M=[%s, rot=%s, trans=%s]".formatted(tris, rotation, translation);
	}

	public List<Triangle> getTris() {
		return tris;
	}

	public void setTris(List<Triangle> tris) {
		this.tris = tris;
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
}
