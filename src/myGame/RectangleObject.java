package myGame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

public class RectangleObject {
	private Node node1;
	private Node node2;
	private Node node3;

	private void createGeometry(AssetManager assetManager) {

		/** Create meshes for the arms of the quadcopters */
		final Box box1 = new Box(new Vector3f(0f, 0f, 0f), new Vector3f(0.25f, 0.25f, -10f));
		final Box box2 = new Box(new Vector3f(0f, 0f, 0f), new Vector3f(0.25f, 0.25f, -10f));
		final Box box3 = new Box(new Vector3f(0f, 0f, 0f), new Vector3f(0.25f, 0.25f, -10f));

		final Geometry arm1 = new Geometry("Fisso", box1);
		final Geometry arm2 = new Geometry("Muovi1", box2);
		final Geometry arm3 = new Geometry("Muovi2", box3);

		{
			final Material material1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			material1.setColor("Color", new ColorRGBA(1, 0, 0, 1));
			arm1.setMaterial(material1);
		}
		{
			final Material material2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			material2.setColor("Color", new ColorRGBA(0, 1, 0, 1));
			arm2.setMaterial(material2);
		}
		{
			final Material material3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			material3.setColor("Color", new ColorRGBA(0, 0, 1, 1));
			arm3.setMaterial(material3);
		}
		node1 = new Node("fixed");
		node1.attachChild(arm1);

		node2 = new Node("moves1");
		node2.attachChild(arm2);

		node3 = new Node("moves2");
		node3.attachChild(arm3);

		node1.attachChild(node2);
		node1.attachChild(node3);
	}

	public RectangleObject(AssetManager assetManager) {
		createGeometry(assetManager);
	}

	public Spatial getMovingGeometry1() {
		return node2;
	}

	public Spatial getMovingGeometry2() {
		return node3;
	}

	public Spatial getGeometry() {
		return node1;
	}

}
