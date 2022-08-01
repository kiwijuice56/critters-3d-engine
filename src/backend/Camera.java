package backend;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Converts 3D meshes to 2D projections to be drawn on a ScreenWorld
 */
public class Camera {
	private static final double FOV = Math.toRadians(120);
	private static final double ANGLE = 1.0 / Math.tan(FOV / 2.0);

	private final boolean[] options = new boolean[8];

	// Configuration flags

	public static final int FILL_FACES = 0;
	public static final int LIGHT_FACES = 1;
	public static final int DRAW_EDGES = 2;
	public static final int SORT_DEPTH = 3;
	public static final int CULL_BACK = 4;

	public Camera() {
		setOption(FILL_FACES, true);
		setOption(LIGHT_FACES, true);
		setOption(DRAW_EDGES, false);
		setOption(SORT_DEPTH, false);
		setOption(CULL_BACK, true);
	}

	/**
	 * Rasterizes a mesh onto the ScreenWorld
	 * @param screen The screen to draw to
	 * @param mesh The mesh to draw
	 */
	public void rasterizeMesh(Rasterizer screen, List<Light> lights, Mesh mesh) {
		screen.setLoadedTexture(mesh.getTexture(), mesh.getTextureWidth(), mesh.getTextureHeight());

		List<Triangle> triList = new ArrayList<>();

		// Create copies of triangle points to transform them, then add to a list to sort by depth

		for (Triangle tri : mesh.getTris()) {
			List<Vector> transformedPts = new ArrayList<>(3);
			// Translate and rotate each point of the triangle
			for (Vector pt : tri.getPts())
				transformedPts.add(translatePoint(rotatePoint(pt, mesh.getRotation()), mesh.getTranslation()));
			triList.add(new Triangle(transformedPts, tri.getTPts()));
		}

		// This method of depth correction, called the Painter's Method, is a crude but fast way to ensure that
		// triangles are mostly drawn in the correct order
		// Disabled by default, but necessary for transparent objects
		if (options[SORT_DEPTH])
			triList.sort(Comparator.comparingDouble(Triangle::getCenterDepth));

		for (Triangle tri : triList) {
			List<Vector> projectedPts = new ArrayList<>(3);

			// Project each point of the triangle
			for (Vector pt : tri.getPts())
				projectedPts.add(projectPoint(pt, screen.getWidth()));

			Triangle projectedTri = new Triangle(projectedPts, tri.getTPts());

			// Skip drawing if projected triangle is facing away from camera
			if (options[CULL_BACK] && Triangle.calculateNormal(projectedTri).z < 0)
				continue;

			if (options[FILL_FACES]) {
				if (options[LIGHT_FACES]) {
					screen.setFillColor(0x000000);
					// Add each light color scaled by the dot product of the light direction and the triangle normal
					for (Light light : lights) {
						double strength = Math.max(0, light.getDirection().dot(Triangle.calculateNormal(tri))) * light.getStrength();
						screen.setFillColor(ColorHelper.tintColor(screen.getFillColor(), light.getColor(), strength));
					}
					screen.setFillColor(ColorHelper.blendColor(screen.getFillColor(), mesh.getModulate()));
				} else {
					screen.setFillColor(mesh.getModulate());
				}
				screen.fillTriangle(projectedTri);
			}

			if (options[DRAW_EDGES]) {
				screen.outlineTriangle(projectedTri);
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
	private static Vector projectPoint(Vector pt, double screenSize) {
		return new Vector(
				screenSize * (0.5 + ((pt.x * ANGLE) / -Math.abs(pt.z))),
				screenSize * (0.5 + ((pt.y * ANGLE) / -Math.abs(pt.z))),
				pt.z);
	}

	@Override
	public String toString() {
		String[] optionNames = {"Fill Faces", "Shade Faces", "Draw Edges", "Sort Depth", "Cull Back"};
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < optionNames.length; i++)
			out.append("	").append(optionNames[i]).append(": ").append(options[i]).append("\n");
		return out.toString();
	}

	public void setOption(int idx, boolean val) {
		options[idx] = val;
	}

	public boolean getOption(int idx) {
		return options[idx];
	}
}
