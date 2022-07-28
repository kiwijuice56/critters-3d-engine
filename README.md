# ap-gridworld-3d-engine
A fully functioning 3d engine using the APCS Gridworld interface as a display.
Solution to APCS Gridworld assignment as described [here](https://docs.google.com/document/d/1MLoiBiA2aGNJt47wUIgn6nQSzdvB5Oo5CMJqowSeZwI/edit?usp=sharing).

![Sample execution](resources/sample.png)

## Features
- Basic shading with customizable lights and meshes
- .OBJ importer to preview any models
- Proper depth buffering
- UV mapping and texturing

## How to use
Scenes can be configured in the `MainRunner.java` class. A complete demonstration of importing and
initializing models can be found in the `main` function.

Place any textures and .OBJ files inside the `resources` folder.

.OBJ files can easily be exported from any 3d editing program and imported to the engine.
If using Blender, ensure that the export includes UV data and "Keep Vertex Order" is enabled.
Textures must be PNG-32.
