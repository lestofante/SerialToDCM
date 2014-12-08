package reader.cdc;

import jssc.SerialPort;
import jssc.SerialPortException;
import myGame.DCMlogic;
import reader.SensorReader;

import com.jme3.math.FastMath;
import com.jogamp.openal.sound3d.Vec3f;

public class ReadCDC extends SensorReader implements Runnable {

	private int g = 0, m = 0, a = 0;
	private SerialPort serialPort;
	static float toRad = FastMath.DEG_TO_RAD*(17.50f/1000);
	
	public ReadCDC(DCMlogic dcm) {
		super(dcm);
	}

	@Override
	public void connect() {
		serialPort = new SerialPort("/dev/ttyACM0");
		try {
			boolean opened = false;
			while (!opened) {
				try {
					serialPort.openPort();// Open serial port
					serialPort.setParams(SerialPort.BAUDRATE_256000, 8, 1, 0);// Set params. Because this is CDC baudrate gets ignored
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
			
			System.out.println("port found, asked to start");

			new Thread(this).start();

			// serialPort.closePort();//Close serial port
		} catch (SerialPortException ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public void run() {
		long time = System.currentTimeMillis();
		long count = 0;

		try {
			while (true) {
				byte[] buffer;

				buffer = serialPort.readBytes(7); // Read 7 bytes from serial port

				if (buffer.length != 7) {
					System.out.println("len unexpected: " + buffer.length);
				}
				analize(buffer);

				count += buffer.length;
				if (System.currentTimeMillis() - time >= 1000) {
					long timeElaplsed = System.currentTimeMillis() - time;
					System.out.println("velocità lettura B/s: " + count * (timeElaplsed / 1000));
					System.out.println("letture g/a/m: " + g + "/" + a + "/" + m);
					g = a = m = 0;
					count = 0;
					time = System.currentTimeMillis();
				}

			}
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	Vec3f acce = new Vec3f(0, 0, 0);
	Vec3f magn = new Vec3f(0, 0, 0);

	private void analize(byte[] buffer) { // remember; message are left to right

		if (buffer.length != 7) {
			return;
		}

		int arr[] = new int[3];
		for (int i = 0; i < 3; i++) {
			int low = buffer[i * 2 + 2] & 0xFF;
			int high = buffer[i * 2 + 1] << 8;
			int uint8 = high + low;
			arr[i] = uint8;
			// System.out.print(uint8 + " ");
		}

		switch (buffer[0]) {
		case 'G':
			g++;
			/*
			 * if (g % 200 == 0) {// `7 times at seconds System.out.print("G:"); for (int i = 0; i < 3; i++) { int low = buffer[i * 2 + 2] & 0xFF; int high = buffer[i * 2 + 1] << 8; int uint8 = high + low; System.out.print(uint8 + " "); } System.out.println(); }
			 */
			dcm.FreeIMUUpdate(-arr[0] * toRad, -arr[1] * toRad, arr[2] * toRad, -acce.v1, acce.v2, acce.v3, -magn.v1, magn.v2, magn.v3);
			break;
		case 'M':
			m++;
			/*
			 * if (m%10 == 0 ){//`7 times at seconds System.out.print("M:"); for (int i=0; i < 3; i++){ int low = buffer[i*2+1] & 0xFF; int high = buffer[i*2+2]<<8; int uint8 = high+low; System.out.print(uint8+" "); } System.out.println(); }
			 */
			magn = new Vec3f(arr[0], arr[1], arr[2]);
			break;
		case 'A':
			a++;
			if (a % 200 == 0) {// `7 times at seconds
				System.out.print("A:");
				for (int i = 0; i < 3; i++) {
					int low = buffer[i * 2 + 2] & 0xFF;
					int high = buffer[i * 2 + 1] << 8;
					int uint8 = high + low;
					System.out.print(uint8 + " ");
				}
				System.out.println();
			}
			acce = new Vec3f(arr[0], arr[1], arr[2]);
			break;
		case 'E':
			System.out.print("ERRORE:");
			break;
		case 'S':
			System.out.print("read/s:");
			for (int i = 0; i < 3; i++) {
				int low = buffer[i * 2 + 1] & 0xFF;
				int high = buffer[i * 2 + 2] << 8;
				int uint8 = high + low;
				System.out.print(uint8 + " ");
			}
			System.out.println();
			break;
		default:
			System.out.print("Errore: ");
			for (int i = 0; i < buffer.length; i++) {
				System.out.print((buffer[i] & 0x00ff) + " ");
			}
			System.out.println();
			return;
		}

		System.out.flush();
	}

	/*
	 * private static void analize(byte[] buffer) { //remember; message are left to right switch (buffer[6]) { case 'G': g++; break; case 'M': m++; break; case 'A': a++; break; case 'E': System.out.print("ERRORE:"); break; case 'S': System.out.print("read/s:"); for (int i=0; i < 3; i++){ int low = buffer[i*2] & 0xFF; int high = buffer[i*2+1]<<8; int uint8 = high+low; System.out.print(uint8+" "); } System.out.println(); break; default: System.out.print("Errore: "); for (int i=0; i < buffer.length; i++){ System.out.print( (buffer[i]&0x00ff)+" " ); } System.out.println(); System.out.flush(); return; } System.out.print("*"); }
	 */
}
