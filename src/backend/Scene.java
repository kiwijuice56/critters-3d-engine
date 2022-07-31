package backend;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents all the meshes and lights to be drawn onto a ScreenWorld at one time
 */
public class Scene {
	private final List<Mesh> meshes;
	private final List<Light> lights;
	private final Camera mainCamera;

	public Scene() {
		this(new ArrayList<>(), new ArrayList<>());
	}

	public Scene(List<Mesh> meshes, List<Light> lights) {
		this.meshes = meshes;
		this.lights = lights;
		this.mainCamera = new Camera();
	}

	public void update() {
		for (Mesh m : meshes) {
			m.setRotation(new Vector(m.getRotation().x + 0.02, m.getRotation().y - 0.015, m.getRotation().z + 0.05));
		}
	}

	/* * * Accessor and mutator methods * * */

	public List<Mesh> getMeshes() {
		return meshes;
	}

	public List<Light> getLights() {
		return lights;
	}

	public Camera getMainCamera() {
		return mainCamera;
	}
}
