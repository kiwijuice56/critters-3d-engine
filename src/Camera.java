import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Converts 3D meshes to 2D projections to be drawn on a ScreenWorld
 */
public class Camera {
	private static final double FOV = Math.toRadians(120);

	/**
	 * Rasterizes a mesh onto the ScreenWorld
	 * @param screen The screen to draw to
	 * @param mesh The mesh to draw
	 */
	public static void rasterizeMesh(ScreenWorld screen, Mesh mesh) {
		screen.setLoadedTexture(mesh.getTexture());
		screen.setTextureWidth(mesh.getTextureWidth());
		screen.setTextureHeight(mesh.getTextureHeight());

		double angle = 1.0 / Math.tan(FOV / 2.0);
		List<Triangle> triList = new ArrayList<>();

		// Create copies of triangle points to transform them, then add to a list to sort by depth

		for (Triangle tri : mesh.getTris()) {
			List<Vector> transformedPts = new ArrayList<>();
			// Translate and rotate each point of the triangle
			for (Vector pt : tri.getPts())
				transformedPts.add(translatePoint(rotatePoint(pt, mesh.getRotation()), mesh.getTranslation()));
			triList.add(new Triangle(transformedPts, tri.getTPts()));
		}

		// This method of depth correction, called the Painter's Method, is a crude but fast way to ensure that
		// triangles are mostly drawn in the correct order
		// triList.sort(Comparator.comparingDouble(Triangle::getCenterDepth));

		for (Triangle tri : triList) {
			List<Vector> projectedPts = new ArrayList<>();
			// Project each point of the triangle
			for (Vector pt : tri.getPts())
				projectedPts.add(projectPoint(pt, screen.getWidth(), angle));

			Vector normal = Triangle.calculateNormal(tri);
			Triangle projectedTri = new Triangle(projectedPts, tri.getTPts());

			// Skip drawing if projected triangle is facing away from camera
			if (Triangle.calculateNormal(projectedTri).z < 0)
				continue;

			if (screen.isDrawFace()) {
				projectedTri.setColor(Color.BLACK);
				// Add each light color scaled by the dot product of the light direction and the triangle normal
				for (Light light : screen.getScene().getLights()) {
					double strength = Math.max(0, light.getDir().dot(normal)) * light.getStrength();

					Color c = light.getColor();
					projectedTri.tintColor(new Color(
							(int) (c.getRed() * strength),
							(int) (c.getGreen() * strength),
							(int) (c.getBlue() * strength)));
				}
				projectedTri.setColor(Triangle.blendColor(projectedTri.getColor(), mesh.getModulate()));

				screen.setDrawColor(projectedTri.getColor());

				screen.fillTriangle(projectedTri);
			}

			if (screen.isDrawOutline()) {
				screen.setDrawColor(Color.BLACK);
				screen.drawTriangle(projectedTri);
			}
		}
	}

	/**
	 * Applies rotation matrix to a point
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
	 * Applies projection matrix to a point
	 * @param pt The point to be projected
	 * @param screenSize The minimum of the screen's width and height to scale up the projection
	 * @return The projected point
	 */
	private static Vector projectPoint(Vector pt, double screenSize, double angle) {
		return new Vector(
				screenSize * (0.5 + ((pt.x * angle) / -Math.abs(pt.z))),
				screenSize * (0.5 + ((pt.y * angle) / -Math.abs(pt.z))),
				pt.z);
	}
}
