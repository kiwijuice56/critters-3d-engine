package backend;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;

/**
 * Represents a collection of Triangles with a position and rotation
 */
public class Mesh {
	private final List<Triangle> tris;

	private String name;

	private Vector rotation;
	private Vector translation;

	private int modulate;

	private byte[] texture;
	private int textureWidth, textureHeight;

	public Mesh(List<Triangle> tris) {
		this.tris = tris;
		this.rotation = new Vector(0, 0, 0);
		this.translation = new Vector(0,0,0);
		this.modulate = 0xFFFFFF;

		this.textureWidth = 0;
		this.textureHeight = 0;
	}

	/* * * Accessor and mutator methods * * */

	@Override
	public String toString() {
		return "%s:\n	position: %s\n	rotation: %s\n	modulate: %s".formatted(name, translation, rotation, modulate);
	}

	public List<Triangle> getTris() {
		return tris;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Vector getRotation() {
		return rotation;
	}

	public void setRotation(Vector rotation) {
		this.rotation = rotation;
	}

	public Vector getTranslation() {
		return translation;
	}

	public void setTranslation(Vector translation) {
		this.translation = translation;
	}

	public int getModulate() {
		return modulate;
	}

	public void setModulate(int modulate) {
		this.modulate = modulate;
	}

	public byte[] getTexture() {
		return texture;
	}

	public void setTexture(BufferedImage img) {
		this.texture = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		this.textureHeight = img.getHeight();
		this.textureWidth = img.getWidth();
	}

	public int getTextureWidth() {
		return textureWidth;
	}

	public int getTextureHeight() {
		return textureHeight;
	}
}
