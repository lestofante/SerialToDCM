package DCM;


import myGame.DCMlogic;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.util.TempVars;

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
		Vector3f vec = dcm.getSimpleGyro();		
		TempVars vars = TempVars.get();
		Quaternion q = new Quaternion(new float[]{vec.y,vec.z,vec.x}); 
		getSpatial().rotate(vec.y,vec.z,vec.x);
	}

}
