package DCM;


import java.util.LinkedList;

import myGame.DCMlogic;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Sphere;

public class MagControl extends AbstractControl {

	private static final int POINT_MAX_NUMBER = 10000;

	private final DCMlogic dcm;
	
	LinkedList <Node> plotPoint = new LinkedList<>();

	private AssetManager assetManager;
	
	public MagControl(final DCMlogic dcm, AssetManager m){
		this.dcm=dcm;
		this.assetManager = m;
	}
	

	@Override
	public Control cloneForSpatial(Spatial arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void controlRender(RenderManager arg0, ViewPort arg1) {
		// TODO Auto-generated method stub

	}

	
	
	@Override
	protected void controlUpdate(float g0) {
		
		
		/** loop sketch*/
		//process buffers
			//since we have different samplerates should we only get the last full information triplet
		Vector3f temp = dcm.getMagn();
		
		
		if(temp.length() == 0)
			return;
		
		/*float a = temp.z;
		temp.z = temp.y;
		temp.y = a;
		*/
		/*
		Quaternion q = new Quaternion();
		q.lookAt(temp, Vector3f.UNIT_Y);
		getSpatial().setLocalRotation(q);
		*/
		temp.x = map(temp.x-17, -452, 452, -300, 300);
		temp.y = map(temp.y-2, -472, 472, -300, 300);
		temp.z = map(temp.z-25, -415, 415, -300, 300);
		
		System.out.println("lengh:"+temp.length() );
		
		
		Sphere sphere = new Sphere(10, 10, 0.05f);
		final Geometry point = new Geometry("sphere", sphere);
		
		
		final Material material1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		
		float colore = map(temp.length(), 250, 350, 0, 1);
		
		material1.setColor("Color", new ColorRGBA(colore, 0, 0, 0.8f));
		
		point.setMaterial(material1);
		
		Node tmpN = new Node();
		tmpN.attachChild(point);
		
		//temp = temp.normalize();
		
		tmpN.setLocalTranslation(temp.mult(0.01f));
		plotPoint.add( tmpN );
		getSpatial().getParent().attachChild(tmpN);
		
		if (plotPoint.size() > POINT_MAX_NUMBER){
			getSpatial().getParent().detachChild(plotPoint.removeFirst());
		}
		
	}
	
	float map(float x, float in_min, float in_max, float out_min, float out_max)
	{
	  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

}
