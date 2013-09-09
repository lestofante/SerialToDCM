package DCM;


import myGame.DCMlogic;

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
		
		//set convenient position for camera
		getSpatial().getParent().setLocalRotation(new Quaternion(new float[]{0,-(float) (Math.PI/2), 0}));
		
		Quaternion quat = dcm.getQuaternion();
		
		//conjugate (black magic happen here)
		Quaternion q = new Quaternion(quat.getX(),-quat.getY(),-quat.getZ(),-quat.getW() );
		getSpatial().setLocalRotation(q);
		
		//quat.t
		//float angles[] = quat.toAngles(null); //yaw,roll,pitch
		
		//System.out.println(quat);
		
		//quat = quat.add(getSpatial().getWorldRotation());
		//getSpatial().setLocalRotation(quat);
		
		//getSpatial().getParent().setLocalRotation(quat);
		
		//
		
		//Quaternion q = new Quaternion();
		//q = q.add( new Quaternion(new float[]{0,0,-angles[2]}) );
		//q = q.add( new Quaternion(new float[]{-angles[1],0,0}) );
		//q = q.add( new Quaternion(new float[]{0,-angles[0],0}) );
		
		//getSpatial().setLocalRotation(quat);
	}
	
	// return the quaternion conjugate of quat
	float [] quatConjugate(float [] quat) {
	  float [] conj = new float[4];
	  
	  conj[0] = quat[0];
	  conj[1] = -quat[1];
	  conj[2] = -quat[2];
	  conj[3] = -quat[3];
	  
	  return conj;
	}

}
