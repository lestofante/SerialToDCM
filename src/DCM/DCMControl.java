package DCM;


import myGame.DCMlogic;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

public class DCMControl extends AbstractControl {

	private final DCMlogic dcm;
	
	public DCMControl(final DCMlogic dcm){
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
		/*

		Quaternion quat = dcm.getQuaternion();
		//quat.t
		float angles[] = quat.toAngles(null); //yaw,roll,pitch
		
		//System.out.println(quat);
		getSpatial().setLocalRotation(new Quaternion(new float[]{-angles[2],angles[0],-angles[1]}));
		
		*/
	}

}
