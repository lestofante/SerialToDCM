package DCM;

import myGame.DCMlogic;

import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class DirectQuaternionControl extends AbstractControl {

	private DCMlogic dcm;

	public DirectQuaternionControl(DCMlogic dcm) {
		this.dcm = dcm;
	}

	@Override
	protected void controlRender(RenderManager arg0, ViewPort arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void controlUpdate(float arg0) {
		// set convenient position for camera
		//getSpatial().getParent().setLocalRotation(new Quaternion(new float[] { 0, -(float) (Math.PI / 2), 0 }));

		Quaternion quat = dcm.getQuaternionStm();

		// conjugate (black magic happen here)
		Quaternion q = new Quaternion(quat.getX(), -quat.getY(), -quat.getZ(), -quat.getW());
		getSpatial().setLocalRotation(q);
	}

}