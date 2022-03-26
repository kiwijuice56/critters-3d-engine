import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MainRunner {

	public static void main(String[] args) throws FileNotFoundException {
		Mesh cube = ObjImporter.importObj(new Scanner(new File("resources/cube.obj")));
		Mesh monkey = ObjImporter.importObj(new Scanner(new File("resources/monkey.obj")));

		cube.setTranslation(new Vector(-0.25,0,.55));
		monkey.setTranslation(new Vector(.25,0,.45));

		ScreenWorld screen = new ScreenWorld(100, 100);
		screen.getScene().add(cube); screen.getScene().add(monkey);
		screen.show();
	}
}
