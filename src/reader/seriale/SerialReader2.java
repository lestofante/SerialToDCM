package reader.seriale;

import jssc.SerialPort;
import jssc.SerialPortException;
import myGame.DCMlogic;
import reader.SensorReader;

import com.jme3.math.Vector3f;

public class SerialReader2 extends SensorReader implements Runnable {

	private SerialPort serialPort;
	
	private int g=0, m=0, a=0, e=0, q=0;
	private static final int toAvoid = 0;
	//private static final int toRead = 8;
	
	public SerialReader2(DCMlogic dcm) {
		super(dcm);
		RXTXPathSetter.setPaths();
	}
	
	@Override
	public void connect() {
		serialPort = new SerialPort("/dev/ttyACM0");
		try {
			boolean opened = false;
			while (!opened) {
				try {
					serialPort.openPort();// Open serial port
					serialPort.setParams(SerialPort.BAUDRATE_115200, 8, 1, 0);// Set params. Because this is CDC baudrate gets ignored
					//serialPort.purgePort(SerialPort.PURGE_RXCLEAR | SerialPort.PURGE_TXCLEAR);
					opened = true;
				} catch (SerialPortException e) {
					if (!e.getExceptionType().equals(SerialPortException.TYPE_PORT_NOT_FOUND)) {
						throw e;
					}
					try {
						System.out.println("port not found");
						Thread.sleep(200);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
			
			/*
			serialPort.writeByte((byte) 'R');//ask start
			*/
			System.out.println("port found, waiting for message start");
			
			int ok = 0; //0 = wait for -128, 1 = wait for 0, 2 = ok
			while(ok < 2){
				byte[] buffer = serialPort.readBytes(1);// Read 2 bytes from serial port
				
				System.out.print((int)buffer[0]+" ");
				
				switch(ok){
				case 0:
					if (buffer[0] == 0){
						ok++;
						System.out.println("ok1");
					}
					break;
				case 1:
					if (buffer[0] == -128)
						ok++;						
					else if (buffer[0] != 0)
						ok = 0;
					break;
				default:
					ok =0;
				}
			}
			System.out.println("ok2");
			serialPort.readBytes(6);// Remove next 67 byte

			new Thread(this).start();// start this thread
			
			// serialPort.closePort();//Close serial port
		} catch (SerialPortException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			execute();
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}
	private void execute() throws SerialPortException{
		long time = System.currentTimeMillis();
		long count = 0;
		while (true) {
			byte[] buffer = serialPort.readBytes(2);// Read 2 bytes from serial port (header)
			count += buffer.length;
			int low = ((int)buffer[0]) & 0xFF;
			int high = ((int)buffer[1])<<8;
			int header = high+low;
			int toRead=6;
			//int header =0;
			//byte header = buffer[0];
			//System.out.println("header:"+buffer[0]+" "+buffer[1]+" "+header);
			switch((byte)header){
				case 0: //gyro
					break;
				case 1: //acce
					break;
				case 2: //magne
					break;
				case 3: //gyro err
					System.out.println("Gyro too fast");
					break;
				case 4: //acce err
					System.out.println("Acce too fast");
					break;
				case 5: //magne err
					System.out.println("Magne too fast");
					break;
				case 6: //quaternion
					//System.out.println("Quaternion!");
					q++;
					toRead = 16; //4byte * 4 float
					break;
				default:
					System.out.println("wrong header!"+buffer[0]+" "+buffer[1]+" "+header);
			}
			
			buffer = serialPort.readBytes(toRead);// Read toRead bytes from serial port (header)
			
			analize((byte)header, buffer);

			count += buffer.length;
			if (System.currentTimeMillis() - time >= 1000) {
				long timeElaplsed = System.currentTimeMillis() - time;
				System.out.println("velocità lettura B/s: " + count * (timeElaplsed / 1000));
				System.out.println("letture g/a/m/errori/queternioni: " + g+"/"+ a+"/"+ m+"/"+e+"/"+q);
				g=a=m=e=q=0;
				count = 0;
				time = System.currentTimeMillis();
			}

		}
	}
	
	private Vector3f magVec, accVec, gyroVec;
	final float mdpsOverDigitAt250 = 8.75f;
	final float mdpsOverDigitAt500 = 17.5f;
	final float mdpsOverDigitAt2000 = 70;
	private void analize(byte header, byte[] buffer) { //remember; message are left to right
		
		switch ( header ) {
		case 0:
			g++;
			gyroVec = new Vector3f();
			for (int i=0; i < 3; i++){
				int low = buffer[i*2+toAvoid+1] & 0xFF;
				int high = buffer[i*2+toAvoid]<<8;
				int uint8 = high+low;
				switch (i) {
				case 0:
					gyroVec.x = ((uint8*mdpsOverDigitAt500)/1000)*0.0174532925f;
					break;
				case 1:
					gyroVec.y = ((uint8*mdpsOverDigitAt500)/1000)*0.0174532925f;
					break;
				case 2:
					gyroVec.z = ((uint8*mdpsOverDigitAt500)/1000)*0.0174532925f;
					break;
				}
			}
			if (g%200 == 0 ){//`7 times at seconds
				System.out.println("G:"+gyroVec);
			}
			break;
		case 2:
			m++;
			magVec = new Vector3f();
			for (int i=0; i < 3; i++){
				int low = buffer[i*2+toAvoid+1] & 0xFF;
				int high = buffer[i*2+toAvoid]<<8;
				int uint8 = high+low;
				switch (i) {
				case 0:
					//magVec.y = -uint8;
					magVec.x = uint8;
					break;
				case 1:
					//magVec.z = uint8;
					magVec.y = uint8;
					break;
				case 2:
					//magVec.x = uint8;
					magVec.z = uint8;
					break;
				}
			}
			magVec = magVec.normalize();
			if (m%70 == 0 ){
				System.out.println("M:"+magVec);
			}
			break;
		case 1:
			a++;
			accVec = new Vector3f();
			for (int i=0; i < 3; i++){
				int low = buffer[i*2+toAvoid+1] & 0xFF;
				int high = buffer[i*2+toAvoid]<<8;
				int uint8 = high+low;
				switch (i) {
				case 0:
					//accVec.y = -uint8;
					accVec.x = uint8;
					break;
				case 1:
					//accVec.x = uint8;
					accVec.y = uint8;
					break;
				case 2:
					//accVec.z = uint8;
					accVec.z = uint8;
					break;
				}
			}
			accVec = accVec.normalize();
			if (a%200 == 0 ){//`7 times at seconds
				System.out.println("A:"+accVec);
			}
			break;
		case 6:
			/*
			System.out.print("quat:");
			for (int i=0;i< 4; i++){
				int ris=0;
				int ris2=0;
				for (int f=0;f< 4; f++){
					ris = ris << 8;
					ris |= buffer[(3-f)+i*4]& 0xFF;
					ris2 = ris2 << 8;
					ris2 |= buffer[f+i*4] & 0xFF;
				}
				float risF = Float.intBitsToFloat(ris);
				float risF2 = Float.intBitsToFloat(ris2);
				System.out.print(risF+"/"+risF2+" ");
			}
			System.out.println();
			*/
			break;
		case 'S':
			System.out.print("read/s:");
			for (int i=0; i < 3; i++){
				int low = buffer[i*2+toAvoid+1] & 0xFF;
				int high = buffer[i*2+toAvoid]<<8;
				int uint8 = high+low;
				System.out.print(uint8+" ");
			}
			System.out.println();
			break;
		case 3:
			System.out.print("ERRORE:");
		default:
			
			System.out.print("Errore: ");
			for (int i=0; i < buffer.length; i++){			
				System.out.print( (buffer[i]&0x00ff)+" " );
			}
			System.out.println();
			
			e++;
			return;
		}
		System.out.flush();
		
		if (gyroVec != null && accVec != null && magVec != null) {
			if (dcm != null) {
				dcm.update(gyroVec.x, gyroVec.y, gyroVec.z,
						accVec.x, accVec.y, accVec.z, magVec.x,
						magVec.y, magVec.z);
			}
			gyroVec = accVec = magVec = null;
		}
		/*
		if (gyroVec != null && accVec != null) {
			dcm.update(gyroVec.x, gyroVec.y, gyroVec.z, accVec.x, accVec.y, accVec.z, 0, 0, 0);
			gyroVec = accVec = null;
		}
		
		if (gyroVec != null && magVec != null) {
			dcm.update(gyroVec.x, gyroVec.y, gyroVec.z, 0, 0, 0, magVec.x, magVec.y, magVec.z);
			gyroVec = accVec = null;
		}
		
		if (magVec != null) {
			dcm.update(0, 0, 0, 0, 0, 0, magVec.x, magVec.y, magVec.z);
			gyroVec = accVec = null;
		}
		*/
	}

}