package reader.USB;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import myGame.DCMlogic;
import reader.SensorReader;
import src.LibUSBTest;
import src.USBLIstener;

public class USBReader extends SensorReader implements USBLIstener{

	private static final float ALPHA = 0.01f;
	private static float xCenterMag = -18.5f;
	private static float yCenterMag = -27.5f;
	private static float zCenterMag = 8.0f;

	private static float xCenterAcc = -350f;
	private static float yCenterAcc = -300f;
	private static float zCenterAcc = -200f;

	private static float xScaleAcc = 33100f;
	private static float yScaleAcc = 33600f;
	private static float zScaleAcc = 32400f;

	private static float xScaleMag = 1f;
	private static float yScaleMag = 1f;
	private static float zScaleMag = 1f;

	LibUSBTest usb = new LibUSBTest();

	public USBReader(DCMlogic dcm) {
		super(dcm);
		usb.setListener(this);
		new Thread(usb).start();
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub

	}

	short ax, ay, az;
	boolean accOk = false;
	boolean magneOk = false;
	private short mz;
	private short my;
	private short mx;
	
	private float aXF;
	private float aYF;
	private float aZF;

	static float toRad = FastMath.DEG_TO_RAD*(2293.76f/32768f);

	@Override
	public void setRawAccelerometer(short x, short y, short z) {
		float xF = (x - xCenterAcc);
		float yF = (y - yCenterAcc);
		float zF = (z - zCenterAcc);

		xF /= xScaleAcc;
		yF /= yScaleAcc;
		zF /= zScaleAcc;

		aXF += (aXF - xF) * ALPHA;
		aYF += (aYF - xF) * ALPHA;
		aZF += (aZF - xF) * ALPHA;

		ax = (short) (xF * 200);
		ay = (short) (yF * 200);
		az = (short) (zF * 200);	
		
		accOk = true;
	}


	short minx=Short.MAX_VALUE, miny=Short.MAX_VALUE, minz=Short.MAX_VALUE, maxx=Short.MIN_VALUE, maxy=Short.MIN_VALUE, maxz=Short.MIN_VALUE;
	float minDist=Float.MAX_VALUE, maxDist = Float.MIN_VALUE;
	long lastUp = 0;
	@Override
	public void setRawMagnetometer(short x, short y, short z) {
		float xF = 0, yF = 0, zF = 0;

		xF = x - xCenterMag;
		yF = y - yCenterMag;
		zF = z - zCenterMag;

		yF /= yScaleMag;
		xF /= xScaleMag;
		zF /= zScaleMag;		

		if(FastMath.abs(xF-mx)<150){
			mx = (short) (xF);
		}
		if(FastMath.abs(yF-mz)<150){
			mz = (short) (yF);
		}
		if(FastMath.abs(zF-my)<150){
			my = (short) (zF);	
		}		
		magneOk = true;
	}

	@Override
	public void setRawGyroscope(short x, short y, short z) {
		dcm.MadgwickAHRSupdate(-x*toRad, -y*toRad, z*toRad, -this.ay, this.ax, this.az, -this.my, this.mx, this.mz);
	}

}
