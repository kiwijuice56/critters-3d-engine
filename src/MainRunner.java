import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MainRunner {

	public static void main(String[] args) throws FileNotFoundException {
		Mesh cube = ObjImporter.importObj(new Scanner(new File("resources/cube.obj")));
		Mesh pot = ObjImporter.importObj(new Scanner(new File("resources/teapot.obj")));

		cube.setTranslation(new Vector(-0.55,-.5,-.95));
		pot.setTranslation(new Vector(1.25,0,-4.9));
		ScreenWorld screen = new ScreenWorld(100, 100);
		screen.getScene().add(cube); screen.getScene().add(pot);
		screen.show();
	}
}
