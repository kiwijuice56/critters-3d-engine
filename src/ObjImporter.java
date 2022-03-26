import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Imports .obj files and creates meshes from the data
 */
public final class ObjImporter {
	// Disable default constructor
	private ObjImporter() {

	}

	/**
	 * Creates a mesh using a scanner pointing to .obj data
	 * @param s The scanner with .obj data
	 * @return The created mesh
	 */
	public static Mesh importObj(Scanner s) {
		List<Triangle> tris = new ArrayList<>();
		List<Vector> pts = new ArrayList<>();

		while (s.hasNext()) {
			String line = s.nextLine();
			if (line.length() == 0)
				continue;
			switch (line.charAt(0)) {
				case 'v' -> {
					String[] ptStr = line.substring(2).split(" ");
					double x = Double.parseDouble(ptStr[0]);
					double y = Double.parseDouble(ptStr[1]);
					double z = Double.parseDouble(ptStr[2]);
					pts.add(new Vector(x, y, z));
				}
				case 'f' -> {
					List<Vector> triPts = new ArrayList<>(3);
					for (String pt : line.substring(2).split(" "))
						triPts.add(pts.get(Integer.parseInt(pt) - 1));
					tris.add(new Triangle(triPts));
				}
			}
		}
		return new Mesh(tris);
	}
}
