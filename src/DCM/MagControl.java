package DCM;


import java.util.LinkedList;

import myGame.DCMlogic;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
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
		//set convenient position for camera
		getSpatial().getParent().setLocalRotation(new Quaternion(new float[]{0,-(float) (Math.PI/2),0}));
				
		Quaternion quat = dcm.getQuaternionSTM();
				
		//conjugate (black magic happen here)
		Quaternion q = new Quaternion(quat.getX(),-quat.getY(),-quat.getZ(),-quat.getW() );
		getSpatial().setLocalRotation(q);
		
		
		
	}
	
	float map(float x, float in_min, float in_max, float out_min, float out_max)
	{
	  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

}
