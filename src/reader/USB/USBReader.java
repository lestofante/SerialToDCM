package reader.USB;

import com.jme3.math.Vector3f;

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

	
	short minx=Short.MAX_VALUE, miny=Short.MAX_VALUE, minz=Short.MAX_VALUE, maxx=Short.MIN_VALUE, maxy=Short.MIN_VALUE, maxz=Short.MIN_VALUE;
	float minDist=Float.MAX_VALUE, maxDist = Float.MIN_VALUE;
	long lastUp = 0;
	@Override
	public void setRawMagnetometer(short x, short y, short z) {
		this.mx = (short) x;
		//because magnetometer Y and Z axes is inverted respect to other sensor, but i wanted to keep the same come on stm. Also Z has different measure
		this.my = (short) Math.round( z * (980.0/1100.0) );
		this.mz = (short) y;
		
		Vector3f floatM2 = new Vector3f(x, z , y);
		
		if (x < minx){
			minx = x;
		}
		if (y < miny){
			miny = y;
		}
		if (z < minz){
			minz = z;
		}
		
		if (x > maxx){
			maxx = x;
		}
		if (y > maxy){
			maxy = y;
		}
		if (z > maxz){
			maxz = z;
		}
		
		if (floatM2.length() < minDist){
			minDist = floatM2.length();
		}
		
		if (floatM2.length() > maxDist){
			maxDist = floatM2.length();
		}
		
		if (System.currentTimeMillis()-lastUp>=1000){
			System.out.println(minx+" <= x <= "+maxx);
			System.out.println(miny+" <= y <= "+maxy);
			System.out.println(minz+" <= z <= "+maxz);
			System.out.println(minDist+" <= d <= "+maxDist);
			
			minx=miny=minz=Short.MAX_VALUE;
			maxx=maxy=maxz=Short.MIN_VALUE;
			minDist = Float.MAX_VALUE;
			maxDist = Float.MIN_VALUE;
			lastUp = System.currentTimeMillis();
		}
		
		//Vector3f shotM = new Vector3f(x, (short)z<0?(z * (1100.0f/980.0f)):z, y);
		
		Vector3f floatM = new Vector3f(x/110, z/98, y/110 );
		
		
		//System.out.print(   "Dist1: "+shotM.length() );
		//System.out.println(   " Dist2: "+floatM.length()+" "+floatM );
		//System.out.println( " Dist3: "+floatM2.length()+" "+floatM2 );
		
//		System.out.println( "Diff1: "+shotM.length() );	
//		shotM = shotM.normalize();
//		floatM = floatM.normalize();
//		System.out.println( "Diff2: "+shotM.subtract(floatM).length() );
		
		magneOk = true;
	}

	@Override
	public void setRawGyroscope(short x, short y, short z) {
		if (accOk==true && magneOk==true){
			//System.out.println("Valori giro"+x+" "+y+ " "+z);
			
			ay = ax = az = 0;
			//my = mx = mz = 0;
			
			dcm.FreeIMUUpdate(-x*toRad, -y*toRad, z*toRad, -this.ay, this.ax, this.az, this.my, -this.mx, -this.mz);
			accOk = magneOk = false;
			my = mx = mz = 0;
			ay = ax = az = 0;
		}
		
	}

}
