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
	boolean magneOk = false;
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
		//because magnetometer Y and Z axes is inverted respect to other sensor, but i wanted to keep the same come on stm
		this.my = z;
		this.mz = y;
		magneOk = true;
	}

	@Override
	public void setRawGyroscope(short x, short y, short z) {
		if (accOk==true || magneOk==true){
			//System.out.println("Valori giro"+x+" "+y+ " "+z);
			
			//ay = ax = az = 0;
			my = mx = mz = 0;
			
			dcm.FreeIMUUpdate(-x*toRad, -y*toRad, z*toRad, -this.ay, this.ax, this.az, -this.my, this.mx, this.mz);
			accOk = magneOk = false;
			my = mx = mz = 0;
			ay = ax = az = 0;
		}
		
	}

}
