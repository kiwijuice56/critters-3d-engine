package frontend;

import backend.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;


public class MainWindow extends JFrame {
	private RenderingPanel panel;
	private Rasterizer rasterizer;
	private Scene scene;

	public static void main(String[] args) throws IOException {
		new MainWindow(400, 300, 2);
	}

	private void initializeScene() throws IOException {
		this.scene = new Scene();

		Mesh cube = ObjImporter.importObj(new Scanner(new File("resources/shapes/cube.obj")));
		cube.setTranslation(new Vector(-.6,.2,-1.7));
		BufferedImage b = ImageIO.read(Objects.requireNonNull(MainWindow.class.getResource("/textures/cube.png")));
		cube.setTexture(b);

		Mesh pot = ObjImporter.importObj(new Scanner(new File("resources/shapes/teapot.obj")));
		pot.setTranslation(new Vector(1.2,-.5,-3.6));

		Mesh monkey = ObjImporter.importObj(new Scanner(new File("resources/shapes/monkey.obj")));
		monkey.setTranslation(new Vector(-.2,-.4,-1.55));

		java.util.List<Mesh> meshes = scene.getMeshes();
		meshes.add(pot);
		meshes.add(cube);
		meshes.add(monkey);

		// Create lights and add them to the scene
		java.util.List<Light> lights = scene.getLights();
		lights.add(new Light(new Vector(-1, -.5, .5), new Color(0, 120, 255).getRGB(), 0.25));
		lights.add(new Light(new Vector(1, -.5, .5), new Color(255, 120, 0).getRGB(), 0.35));
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



}
