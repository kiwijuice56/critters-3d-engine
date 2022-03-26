import java.util.ArrayList;
import java.util.List;

/**
 * Rasterize meshes to be represented on a ScreenWorld
  */
public class Camera {
	private static final double fov = Math.toRadians(120);

	/**
	 * Rasterizes a mesh and paints it onto the screen world
	 * @param screen
	 * @param mesh
	 */
	public static void rasterizeMesh(ScreenWorld screen, Mesh mesh) {
		double angle = 1.0/Math.tan(fov/2.0);

		// Create copies of triangle points to transform them, then draws them onto the screen
		for (Triangle tri : mesh.getTris()) {
			List<Vector> rPts = new ArrayList<>();
			for (Vector pt : tri.getPts()) {
				Vector transformedPt = translatePoint(rotatePoint(pt, mesh.getRotation()), mesh.getTranslation());
				rPts.add(rasterizePoint(transformedPt, screen.getWidth(), angle));
			}
			screen.drawTriangle(rPts);
		}
	}

	/**
	 * Applies rotation matrix to rotate a point
	 * @param pt The point to be rotated
	 * @param rot The rotation vector
	 * @return The rotated point
	 */
	private static Vector rotatePoint(Vector pt, Vector rot) {
		Vector rPt = new Vector(pt.x, pt.y, pt.z);
		double rotY = rPt.y * Math.cos(rot.x) + rPt.z * Math.sin(rot.x);
		double rotZ = rPt.y * -1 * Math.sin(rot.x) + rPt.z * Math.cos(rot.x);

		rPt.z = rotZ;
		rPt.y = rotY;

		double rotX = rPt.x * Math.cos(rot.y) + rPt.z * -1 * Math.sin(rot.y);
		rotZ = rPt.x * Math.sin(rot.y) + rPt.z * Math.cos(rot.y);

		rPt.x = rotX;
		rPt.z = rotZ;

		rotX = rPt.x * Math.cos(rot.z) + rPt.y * Math.sin(rot.z);
		rotY = rPt.x * -1 * Math.sin(rot.z) + rPt.y * Math.cos(rot.z);

		rPt.x = rotX;
		rPt.y = rotY;
		return rPt;
	}

	/**
	 * Applies translation to a point
	 * @param pt The point to be translation
	 * @param trans The translation vector
	 * @return The translated point
	 */
	private static Vector translatePoint(Vector pt, Vector trans) {
		return new Vector(pt.x + trans.x, pt.y + trans.y, pt.z + trans.z);
	}

	/**
	 * Applies projection matrix to rotate a point
	 * @param pt The point to be projected
	 * @param screenSize The minimum of the screen's width and height to scale up the rasterization
	 * @return The projected point
	 */
	private static Vector rasterizePoint(Vector pt, double screenSize, double angle) {
		return new Vector(
				screenSize * (0.5 + ((pt.x * angle) / Math.abs(pt.z))),
				screenSize * (0.5 + ((pt.y * angle) / Math.abs(pt.z))),
				0);
	}
}
