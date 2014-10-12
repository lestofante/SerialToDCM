package reader.USB;

import myGame.DCMlogic;
import reader.SensorReader;
import src.LibUSBTest;
import src.USBLIstener;

import com.jme3.math.FastMath;

public class USBReader extends SensorReader implements USBLIstener{

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

	private short ax, ay, az, mz, my, mx;

	static float toRad = FastMath.DEG_TO_RAD*(2293.76f/32768f);

	@Override
	public void setRawAccelerometer(short x, short y, short z) {
		System.out.println("readed acce "+x+" "+y+" "+z);
		ax = x;
		ay = y;
		az = z;
	}

	@Override
	public void setRawMagnetometer(short x, short y, short z) {
		System.out.println("readed magne "+x+" "+y+" "+z);
		mx = x;
		my = z;
		mz = y;		
	}

	@Override
	public void setRawGyroscope(short x, short y, short z) {
		System.out.println("readed gyro "+x+" "+y+" "+z);
		dcm.FreeIMUUpdate(-x*toRad, -y*toRad, z*toRad, -this.ay, this.ax, this.az, -this.my, this.mx, this.mz);
	}

	@Override
	public void setDCM(float[] q) {
		System.out.println("readed DCM "+q[0]+" "+q[1]+" "+q[2]+" "+q[3]);
		dcm.setStmBypass(q);
	}

	@Override
	public void setEulerianBypass(float[] ypr) {
		System.out.println("readed EULERIAN "+ypr[0]+" "+ypr[1]+" "+ypr[2]);
		dcm.setYprStm(ypr);
	}

}
