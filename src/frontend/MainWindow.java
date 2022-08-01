package frontend;

import backend.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;


/**
 * Initializes the scene and starts the program
 */
public class MainWindow extends JFrame {
	private final RenderingPanel panel;
	private final Rasterizer rasterizer;
	private Scene scene;

	public static void main(String[] args) throws IOException {
		new MainWindow(400, 350, 2);
	}

	private void initializeScene() throws IOException {
		this.scene = new Scene();

		// Store any .obj files and textures in `resources` folder write the path as shown below
		// Ensure that the folder is marked as a resource to the java compiler
		Mesh cube = ObjImporter.importObj(getResource("/shapes/cube.obj"));
		cube.setTexture(ImageIO.read(getResource("/textures/cube.png")));
		cube.setTranslation(new Vector(-0.8,0.2,-1.9));

		Mesh teapot = ObjImporter.importObj(getResource("/shapes/teapot.obj"));
		teapot.setTranslation(new Vector(1.2,-0.5,-3.6));

		Mesh seashell = ObjImporter.importObj(getResource("/shapes/shell.obj"));
		BufferedImage d = ImageIO.read(getResource("/textures/shell.png"));
		seashell.setTexture(d);
		seashell.setTranslation(new Vector(0.1,0.4,-1.5));

		List<Mesh> meshes = scene.getMeshes();
		meshes.add(cube);
		meshes.add(teapot);
		meshes.add(seashell);

		// Create lights and add them to the scene
		java.util.List<Light> lights = scene.getLights();
		lights.add(new Light(new Vector(-1, -.5, .5), new Color(0, 120, 255).getRGB(), 0.25));
		lights.add(new Light(new Vector(1, -.5, .5), new Color(255, 120, 0).getRGB(), 0.25));
		lights.add(new Light(new Vector(0, .2, 1), Color.WHITE.getRGB(), 0.99));

	}

	public MainWindow(int width, int height, int pxSize) throws IOException {
		this.panel = new RenderingPanel(width, height, pxSize);
		this.rasterizer = new CustomRasterizer(panel, width, height);

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setSize(new Dimension(width * pxSize, height * pxSize));
		getContentPane().add(panel);

		setVisible(true);

		Timer t = new Timer(0, e -> {
			scene.update();
			rasterizer.rasterizeScene(scene);
			panel.repaint();
		});

		initializeScene();

		t.start();
	}

	private URL getResource(String path) {
		return Objects.requireNonNull(MainWindow.class.getResource(path));
	}

}
