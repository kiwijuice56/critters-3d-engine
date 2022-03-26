import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MainRunner {

	public static void main(String[] args) throws FileNotFoundException {
		Mesh cube = ObjImporter.importObj(new Scanner(new File("resources/cube.obj")));
		Mesh cube2 = ObjImporter.importObj(new Scanner(new File("resources/cube.obj")));

		cube.setTranslation(new Vector(-0.25,0,.55));
		cube2.setTranslation(new Vector(.15,0,.85));
		cube2.setRotation(new Vector(1.5, 0, 0));

		ScreenWorld screen = new ScreenWorld(100, 100);
		screen.getScene().add(cube); screen.getScene().add(cube2);
		screen.show();
	}
}
