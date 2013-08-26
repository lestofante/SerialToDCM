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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRawGyroscope(short x, short y, short z) {
		if (accOk==true){
			//System.out.println("Valori giro"+x+" "+y+ " "+z);
			dcm.FreeIMUUpdate(-x*toRad, -y*toRad, z*toRad, -this.ay, this.ax, this.az, 0, 0, 0);
			accOk = false;
		}
		
	}

}
