package myGame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

public class SphereObject implements GeometryProvider{
	
	private Node createGeometry(AssetManager assetManager) {
		
		/** Create sphere mesh */
		final Sphere sphere = new Sphere(5, 5, 5);
		
		/** Build spatials from it */
		final Geometry main = new Geometry("sphere", sphere);
		final Material material1 = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		material1.setColor("Color", new ColorRGBA(1, 0, 0, 1));
		main.setMaterial(material1);

		/** Create the quadcopter spatial */
		final Node node = new Node("Axes");
		node.attachChild(main);
		return node;
	}

	private final Node	geometry;

	public SphereObject(AssetManager assetManager) {
		geometry = createGeometry(assetManager);
	}

	@Override
	public Node getGeometry() {
		return geometry;
	}

}
