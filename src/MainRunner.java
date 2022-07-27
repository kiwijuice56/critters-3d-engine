import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MainRunner {

	public static void main(String[] args) throws FileNotFoundException {
		Mesh cube = ObjImporter.importObj(new Scanner(new File("resources/donut.obj")));
		Mesh cube2 = ObjImporter.importObj(new Scanner(new File("resources/cube.obj")));
		Mesh cube3 = ObjImporter.importObj(new Scanner(new File("resources/monkey.obj")));
		Mesh pot = ObjImporter.importObj(new Scanner(new File("resources/teapot.obj")));

		cube.setTranslation(new Vector(-0.6,-.2,-1.45));
		cube2.setTranslation(new Vector(-1.2,-.8,-2));
		cube3.setTranslation(new Vector(.1,-.4,-1.25));

		cube.setModulate(Color.RED);
		cube2.setModulate(Color.BLUE);
		cube3.setModulate(new Color(170,70,200));

		pot.setTranslation(new Vector(.2,.2,-3.8));

		ScreenWorld screen = new ScreenWorld(100, 100);
		screen.getScene().getMeshes().add(pot);
		screen.getScene().getMeshes().add(cube);
		screen.getScene().getMeshes().add(cube2);
		screen.getScene().getMeshes().add(cube3);

		screen.getScene().getLights().add(new Light(new Vector(1, -.5, .5), new Color(255, 120, 0), 0.25));
		screen.getScene().getLights().add(new Light(new Vector(.1, .9, .5), Color.WHITE, 0.97));

		screen.show();
		screen.step();

	}
}
