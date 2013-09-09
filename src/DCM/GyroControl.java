package DCM;


import myGame.DCMlogic;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

public class GyroControl extends AbstractControl {

	private final DCMlogic dcm;
	
	public GyroControl(final DCMlogic dcm){
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
		getSpatial().getParent().setLocalRotation(new Quaternion(new float[]{0,-(float) (Math.PI/2), 0}));
		
		Quaternion quat = dcm.getQuaternionStm();
		
		//conjugate (black magic happen here)
		Quaternion q = new Quaternion(quat.getX(),-quat.getY(),-quat.getZ(),-quat.getW() );
		getSpatial().setLocalRotation(q);
	}

}
