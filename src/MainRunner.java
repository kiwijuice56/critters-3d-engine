import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class MainRunner {

	public static void main(String[] args) throws IOException {
		Mesh cube = ObjImporter.importObj(new Scanner(new File("resources/shapes/cube.obj")));
		BufferedImage b =ImageIO.read(Objects.requireNonNull(MainRunner.class.getResource("/textures/cube.png")));
		cube.setTexture(b);

		Mesh pot = ObjImporter.importObj(new Scanner(new File("resources/shapes/teapot.obj")));

		cube.setTranslation(new Vector(-.1,.2,-1.5));
		pot.setTranslation(new Vector(.5,-.5,-3.8));

		ScreenWorld screen = new ScreenWorld(100, 100);
		screen.getScene().getMeshes().add(pot);
		screen.getScene().getMeshes().add(cube);

		// screen.getScene().getLights().add(new Light(new Vector(-1, -.5, .5), new Color(0, 120, 255), 0.35));
		screen.getScene().getLights().add(new Light(new Vector(1, -.5, .5), new Color(255, 120, 0), 0.35));
		screen.getScene().getLights().add(new Light(new Vector(0, .2, 1), Color.WHITE, 0.99));

		screen.show();
	}
}
