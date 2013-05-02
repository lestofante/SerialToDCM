package DCM;


import myGame.DCMlogic;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

public class AcceControl extends AbstractControl {

	private final DCMlogic dcm;
	
	public AcceControl(final DCMlogic dcm){
		this.dcm=dcm;
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
		
		Vector3f temp = dcm.getAcc();	
		/*
		float a = temp.z;
		temp.z = temp.y;
		temp.y = a;
		*/
		Quaternion q = new Quaternion();
		q.lookAt(temp, Vector3f.UNIT_Z);
		getSpatial().setLocalRotation(q);
		
		
	}

}
