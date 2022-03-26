import java.util.List;

/**
 * Represents 3D triangle
 */
public class Triangle {
	private List<Vector> pts;

	public Triangle(List<Vector> pts) {
		this.pts = pts;
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
}
