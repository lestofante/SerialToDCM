package reader.USB;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import myGame.DCMlogic;
import reader.SensorReader;
import src.LibUSBTest;
import src.USBLIstener;
import src.USBWriter;

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
		ax = x;
		ay = y;
		az = z;
	}

	@Override
	public void setRawMagnetometer(short x, short y, short z) {
		mx = x;
		my = z;
		mz = y;		
	}

	@Override
	public void setRawGyroscope(short x, short y, short z) {
		dcm.FreeIMUUpdate(-x*toRad, -y*toRad, z*toRad, -this.ay, this.ax, this.az, -this.my, this.mx, this.mz);
	}

	@Override
	public void setDCM(float[] q) {
		dcm.setStmBypass(q);
	}

	@Override
	public void setEulerianBypass(float[] ypr) {
		dcm.setYprStm(ypr);
	}

}
