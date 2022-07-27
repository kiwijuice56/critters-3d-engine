import java.util.ArrayList;
import java.util.List;

/**
 * Represents all the meshes and lights to be drawn onto a ScreenWorld at one time
 */
public class Scene {
	private final List<Mesh> meshes;
	private final List<Light> lights;

	public Scene() {
		this(new ArrayList<>(), new ArrayList<>());
	}

	public Scene(List<Mesh> meshes, List<Light> lights) {
		this.meshes = meshes;
		this.lights = lights;
	}

	/* * * Accessor and mutator methods * * */

	public List<Mesh> getMeshes() {
		return meshes;
	}

	public List<Light> getLights() {
		return lights;
	}
}
