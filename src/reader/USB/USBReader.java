package reader.USB;

import myGame.DCMlogic;
import reader.SensorReader;
import src.LibUSBTest;
import src.USBLIstener;

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
	
	short ax, ay, az;
	boolean accOk = false;
	private short mz;
	private short my;
	private short mx;
	static float toRad = 35f/32768f;
	
	@Override
	public void setRawAccelerometer(short x, short y, short z) {
		this.ax = x;
		this.ay = y;
		this.az = z;
		accOk = true;
	}

	@Override
	public void setRawMagnetometer(short x, short y, short z) {
		this.mx = x;
		this.my = y;
		this.mz = z;
	}

	@Override
	public void setRawGyroscope(short x, short y, short z) {
		if (accOk==true){
			//System.out.println("Valori giro"+x+" "+y+ " "+z);
			//my = mx = mz = 0;
			dcm.FreeIMUUpdate(-x*toRad, -y*toRad, z*toRad, -this.ay, this.ax, this.az, -this.mz, this.mx, this.my);
			accOk = false;
			my = mx = mz = 0;
			ay = ax = az = 0;
		}
		
	}

}
