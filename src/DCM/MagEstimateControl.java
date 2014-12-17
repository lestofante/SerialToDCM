package DCM;


import myGame.DCMlogic;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

public class MagEstimateControl extends AbstractControl {

	private DCMlogic dcm;

	public MagEstimateControl(final DCMlogic dcm){
		this.dcm = dcm;
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
		Vector3f yprStm = dcm.getMagnEstimate();
		if (yprStm.length() > 0){
			yprStm.multLocal(20);
			getSpatial().lookAt(yprStm, new Vector3f(0,1,0));
		}
	}

}
