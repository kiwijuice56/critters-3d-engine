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
		Mesh cube = ObjImporter.importObj(new Scanner(new File("resources/cube.obj")));
		BufferedImage b =ImageIO.read(Objects.requireNonNull(MainRunner.class.getResource("/Cube.png")));
		System.out.println(b.getAlphaRaster());
		cube.setTexture(b);

		Mesh cube2 = ObjImporter.importObj(new Scanner(new File("resources/cube.obj")));
		Mesh cube3 = ObjImporter.importObj(new Scanner(new File("resources/cube.obj")));
		Mesh pot = ObjImporter.importObj(new Scanner(new File("resources/cube.obj")));

		//cube.setTranslation(new Vector(.5,.2,-2.5));
		cube.setTranslation(new Vector(.5,.2,-1.4));
		cube2.setTranslation(new Vector(-.2,-.2,-1.5));
		cube3.setTranslation(new Vector(.1,-.4,-1.25));

		cube2.setModulate(Color.BLUE);
		cube3.setModulate(new Color(170,70,200));

		pot.setTranslation(new Vector(-.4,-.5,-3.8));

		ScreenWorld screen = new ScreenWorld(100, 100);
		//screen.getScene().getMeshes().add(pot);
		screen.getScene().getMeshes().add(cube);
		//screen.getScene().getMeshes().add(cube2);
		//screen.getScene().getMeshes().add(cube3);

		//screen.getScene().getLights().add(new Light(new Vector(-1, .5, .5), new Color(0, 70, 140), 0.35));
		screen.getScene().getLights().add(new Light(new Vector(1, -.5, .5), new Color(255, 120, 0), 0.35));
		screen.getScene().getLights().add(new Light(new Vector(.1, .9, .5), Color.WHITE, 0.9));

		screen.show();
		screen.step();

	}
}
