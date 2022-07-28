import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class MainRunner {

	public static void main(String[] args) throws IOException {
		// Create the screen
		ScreenWorld screen = new ScreenWorld(100, 100);

		// Initialize meshes and add them to the scene
		Mesh cube = ObjImporter.importObj(new Scanner(new File("resources/shapes/cube.obj")));
		cube.setTranslation(new Vector(-.6,.2,-1.7));
		BufferedImage b = ImageIO.read(Objects.requireNonNull(MainRunner.class.getResource("/textures/cube.png")));
		cube.setTexture(b);

		Mesh pot = ObjImporter.importObj(new Scanner(new File("resources/shapes/teapot.obj")));
		pot.setTranslation(new Vector(1.2,-.5,-3.6));

		Mesh monkey = ObjImporter.importObj(new Scanner(new File("resources/shapes/monkey.obj")));
		monkey.setTranslation(new Vector(-.2,-.4,-1.55));

		List<Mesh> meshes = screen.getScene().getMeshes();
		meshes.add(pot);
		meshes.add(cube);
		meshes.add(monkey);

		// Create lights and add them to the scene
		List<Light> lights = screen.getScene().getLights();
		lights.add(new Light(new Vector(-1, -.5, .5), new Color(0, 120, 255), 0.35));
		lights.add(new Light(new Vector(1, -.5, .5), new Color(255, 120, 0), 0.35));
		lights.add(new Light(new Vector(0, .2, 1), Color.WHITE, 0.99));

		screen.show();
	}
}
