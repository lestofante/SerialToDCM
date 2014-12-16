package reader.audio;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.TargetDataLine;

import myGame.DCMlogic;
import reader.SensorReader;

import com.jme3.math.Vector3f;

public class AudioReader extends SensorReader implements Runnable{

	public AudioReader(DCMlogic dcm) {
		super(dcm);
	}
	private Vector3f gyroVec;
	boolean stampaValori = false;
	public final AtomicBoolean leggi = new AtomicBoolean(true);
	
	static boolean debugByte = false;

	int lettureValide=0;
	
	//da concordare con la scheda
	byte[] occorrenze = {'A', 'G', 'M', 'T', 'S'};// Accelerometer, Gyroscope, Magnetometer, Test, countMilliseconds
	private int countGyro=0, countMag=0,countAcc=0;
	private long lastMilliS;
	
	@Override
	public void run() {
		AudioFormat format = new AudioFormat(90000.0f, 16, 1, true, false);
		TargetDataLine  microphone = null;
		try {
			
			
			Info choosen = null;
			for (Info i:AudioSystem.getMixerInfo()){
				System.out.println(i.getName());
				if (i.getName().contains("Joystick")){
					choosen = i;
					System.out.println(i.getName()+" THIS HAS BEEN CHOOSED");
				}
			}
			
			if (choosen!=null){
				System.out.println(choosen.getDescription());
				Mixer m = AudioSystem.getMixer(choosen);
				m.open();
				
				  Line.Info[] lineInfos = m.getSourceLineInfo();
				  for (Line.Info lineInfo:lineInfos){
				   System.out.println (choosen.getName()+"---"+lineInfo);
				   Line line = m.getLine(lineInfo);
				   System.out.println("\t-----"+line);
				  }
				microphone = AudioSystem.getTargetDataLine(format, choosen);
			}
			else{
				System.err.println("Scheda STM32f3 non trovata");
				return;
			}
			
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		System.out.println(microphone.getFormat());
		
		try {
			microphone.open();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		microphone.start();
		
		byte[] buffer = new byte[4000];
		int read = 0;
		long sum = 0;
		long tempo = System.currentTimeMillis();
		long deltaT, min=Integer.MAX_VALUE, max = Integer.MIN_VALUE;
		while( leggi.get() ){
			//System.out.println("Leggo dati");
			read = microphone.read(buffer, 0, buffer.length ); //we must read 2 byte at time
			//System.out.println("letti");
			sum+=read;
			//readok=0;
			//System.out.println("analizzo");
			analyze(buffer);
			//System.out.println("analizzati");
			
			if (min > read){
				min = read;
			}
			if (max < read){
				max = read;
			}

			deltaT = System.currentTimeMillis()-tempo; 
			if (deltaT > 1000){
				System.out.println( "letture valide al secondo:"+lettureValide/(deltaT/1000.0)+" byte al secondo: " + sum/(deltaT/1000.0)+" min: "+min+" max: "+max );
				System.out.println( "letture al secondo: giro "+countGyro+" acc "+countAcc+" magne "+countMag );
				System.out.println( "letture differenti al secondo: giro "+diffGyro+" acc "+diffAcc+" magne "+diffMagne+" test "+diffTest+" millisec "+diffMs );
				
				//test some data integrity
				diffGyro=diffAcc=diffMagne=diffTest=diffMs=0;
				countGyro=countMag=countAcc=0;
				
				sum =0;
				lettureValide = 0;
				
				//min and max was used when buffer was dynamic
				min=Integer.MAX_VALUE;
				max = Integer.MIN_VALUE;
				
				tempo = System.currentTimeMillis();
				
				stampaValori = true;
			}
		}
		
		microphone.close();
		
		System.out.println("Lettura scheda audio terminata su richiesta");
	}
	/*
	private static final byte BitReverseTable256[] = 
		{
		  0x00, 0x80, 0x40, 0xC0, 0x20, 0xA0, 0x60, 0xE0, 0x10, 0x90, 0x50, 0xD0, 0x30, 0xB0, 0x70, 0xF0, 
		  0x08, 0x88, 0x48, 0xC8, 0x28, 0xA8, 0x68, 0xE8, 0x18, 0x98, 0x58, 0xD8, 0x38, 0xB8, 0x78, 0xF8, 
		  0x04, 0x84, 0x44, 0xC4, 0x24, 0xA4, 0x64, 0xE4, 0x14, 0x94, 0x54, 0xD4, 0x34, 0xB4, 0x74, 0xF4, 
		  0x0C, 0x8C, 0x4C, 0xCC, 0x2C, 0xAC, 0x6C, 0xEC, 0x1C, 0x9C, 0x5C, 0xDC, 0x3C, 0xBC, 0x7C, 0xFC, 
		  0x02, 0x82, 0x42, 0xC2, 0x22, 0xA2, 0x62, 0xE2, 0x12, 0x92, 0x52, 0xD2, 0x32, 0xB2, 0x72, 0xF2, 
		  0x0A, 0x8A, 0x4A, 0xCA, 0x2A, 0xAA, 0x6A, 0xEA, 0x1A, 0x9A, 0x5A, 0xDA, 0x3A, 0xBA, 0x7A, 0xFA,
		  0x06, 0x86, 0x46, 0xC6, 0x26, 0xA6, 0x66, 0xE6, 0x16, 0x96, 0x56, 0xD6, 0x36, 0xB6, 0x76, 0xF6, 
		  0x0E, 0x8E, 0x4E, 0xCE, 0x2E, 0xAE, 0x6E, 0xEE, 0x1E, 0x9E, 0x5E, 0xDE, 0x3E, 0xBE, 0x7E, 0xFE,
		  0x01, 0x81, 0x41, 0xC1, 0x21, 0xA1, 0x61, 0xE1, 0x11, 0x91, 0x51, 0xD1, 0x31, 0xB1, 0x71, 0xF1,
		  0x09, 0x89, 0x49, 0xC9, 0x29, 0xA9, 0x69, 0xE9, 0x19, 0x99, 0x59, 0xD9, 0x39, 0xB9, 0x79, 0xF9, 
		  0x05, 0x85, 0x45, 0xC5, 0x25, 0xA5, 0x65, 0xE5, 0x15, 0x95, 0x55, 0xD5, 0x35, 0xB5, 0x75, 0xF5,
		  0x0D, 0x8D, 0x4D, 0xCD, 0x2D, 0xAD, 0x6D, 0xED, 0x1D, 0x9D, 0x5D, 0xDD, 0x3D, 0xBD, 0x7D, 0xFD,
		  0x03, 0x83, 0x43, 0xC3, 0x23, 0xA3, 0x63, 0xE3, 0x13, 0x93, 0x53, 0xD3, 0x33, 0xB3, 0x73, 0xF3, 
		  0x0B, 0x8B, 0x4B, 0xCB, 0x2B, 0xAB, 0x6B, 0xEB, 0x1B, 0x9B, 0x5B, 0xDB, 0x3B, 0xBB, 0x7B, 0xFB,
		  0x07, 0x87, 0x47, 0xC7, 0x27, 0xA7, 0x67, 0xE7, 0x17, 0x97, 0x57, 0xD7, 0x37, 0xB7, 0x77, 0xF7, 
		  0x0F, 0x8F, 0x4F, 0xCF, 0x2F, 0xAF, 0x6F, 0xEF, 0x1F, 0x9F, 0x5F, 0xDF, 0x3F, 0xBF, 0x7F, 0xFF
		};*/
	
	private synchronized void analyze(byte[] read) {
		
		int findOccurence = 0;
		while ( (findOccurence=findOccurence(read, findOccurence)) > -1){
			//System.out.println("findOccurence: "+findOccurence);
			//read data
			boolean validByte = true;
			int i=findOccurence;
			char sensore = (char) read[i];
			
			if (debugByte)
				System.out.println(sensore);
			
			if (sensore == 'b'){
				//fine del pacchetto!!
				return;
			}
			
			int byteLettiValidi=0;
			
			Vector3f lettura= new Vector3f();
			int ms, ls=0;
			while (byteLettiValidi < 6 && i<read.length){
				//System.out.println("byteLettiValidi: "+byteLettiValidi+" i: "+i+ " read lenght: "+read.length);
				
				if (i == findOccurence || i==findOccurence+1){
					//System.out.println("continuo: "+read[i]);
					i++;
					continue;
				}
				
				if (validByte){
					//System.out.println("valido: "+read[i]);
					for (int o=0; o<occorrenze.length; o++){
						if ( read[i]==occorrenze[o] )
							validByte = false; //don't take care of next byte
					}
					short val;
					switch(byteLettiValidi){
					case 0:
						if (debugByte)
							System.out.println("x");
						
						ls=0;
						ls |= read[i]&0xff;
						
						if (debugByte)
							System.out.println("ls: "+toBinary(ls) );
						
						break;
					case 1:
						ms=0;
						ms = read[i]&0xff;
						
						if (debugByte)
							System.out.println("ms: "+toBinary(ms) );

						val = 0;
						val = concatenate1(read[i-1], read[i]);
						//concatenate2(ms, ls);
						
						lettura.x = val;
						

						break;
					case 2:
						if (debugByte)
							System.out.println("y");
						
						ls=0;
						ls = read[i]& 0xff;
						
						if (debugByte)
							System.out.println("ls: "+toBinary(ls) );
						
						break;
					case 3:
						ms=0;
						ms = read[i]&0xff;
						
						if (debugByte)
							System.out.println("ms: "+toBinary(ms) );
						
						val = 0;
						val = concatenate1(read[i-1], read[i]);
						//concatenate2(ms, ls);
						
						lettura.y = val;
						
						break;
					case 4:
						if (debugByte)
							System.out.println("z");
						
						ls=0;
						ls = read[i]& 0xff;
						
						if (debugByte)
							System.out.println("ls: "+toBinary(ls) );
						
						break;
					case 5:
						ms=0;
						ms = read[i]&0xff;
						
						if (debugByte)
							System.out.println("ms: "+toBinary(ms) );
						
						val = 0;
						val = concatenate1(read[i-1], read[i]);
						//concatenate2(ms, ls);
						
						lettura.z = val;
						
						//System.out.println("letto: "+lettura);
						break;
					default:
						System.out.println("grave errore byteLettiValidi "+byteLettiValidi);
					}
					byteLettiValidi++;
				}else{
					validByte = true;
					//System.out.println("non valido");
				}
				i++;
			}
			
			findOccurence+=2; // don't elaborate this sub packet again
			
			if (byteLettiValidi == 6){
				lettureValide++;
				switch(sensore){
				case 'G':
					gyroVec = lettura;
					countGyro++;
					break;
				case 'M':
					countMag++;
					break;
				case 'A':
					countAcc++;
					break;
				case 'T':
					diffTest++;
					//System.out.println("test vect: "+lettura);
					break;
				case 'S':
					diffMs++;
					System.out.println("S RICEVUTA, TEMPO : "+(System.currentTimeMillis()-lastMilliS) );
					lastMilliS = System.currentTimeMillis();
					break;
				default:
					System.out.println("grave errore sensore "+sensore);
				}
			}
			/*
			if (gyroVec != null && accVec != null && magVec != null) {
				if (stampaValori){
					System.out.println("Gyro:"+gyroVec+" acc: "+accVec+" magne: "+magVec+" test: "+testVec);
					stampaValori = false;
				}
				
				if (dcm != null) {
					dcm.MadgwickAHRSupdate(gyroVec.x, gyroVec.y, gyroVec.z, 
							accVec.x, accVec.y, accVec.z, magVec.x,
							magVec.y, magVec.z);
				}
				calibra(gyroVec, accVec, magVec);
				gyroVec = accVec = magVec = null;
				
			}
			*/
			/*
			if (gyroVec != null && accVec != null) {
				if (stampaValori){
					System.out.println("Gyro:"+gyroVec+" acc: "+accVec);
					stampaValori = false;
				}
				
				if (dcm != null) {
					dcm.MadgwickAHRSupdate(gyroVec.x, gyroVec.y, gyroVec.z, 
							accVec.x, accVec.y, accVec.z, 
							0, 0, 0);
				}
				calibra(gyroVec, accVec, new Vector3f());
				gyroVec = accVec = null;
			}
			*/
			if (gyroVec != null ) {
				if (stampaValori){
					System.out.println("Gyro:"+gyroVec);
					stampaValori = false;
				}
				
				if (dcm != null) {
					/*
					dcm.MadgwickAHRSupdate(gyroVec.x, gyroVec.y, gyroVec.z, 
							0, 0, 1, 
							0, 0, 0);
					*/
				}
				//calibra(gyroVec, new Vector3f(), new Vector3f());
				gyroVec = null;
			}
			
		}
	}
	private short concatenate1(byte ms, byte ls) {
		//ls = ms = -1;
		//toBinary(ls);
		//toBinary(ms);
		short ris = (short) ((ms << 8) | ls); 
		//System.out.println("valore "+ris);
		//toBinary(ris);
		
		//if (ris!=0)
		//	System.out.println("valore erratico: "+ris);
		return ris;
	}
	/*
	private short concatenate2(int ms, int ls) {
		short val=0;
		val|=(ls<<8) | ms;
		if (debugByte){
			System.out.println("val3: "+val);
			if (Math.abs(val) > SOGLIA_GYRO)
				System.out.println("sfaso3");
		}
		val = reverse(val);
		if (debugByte){
			System.out.println("val4: "+val);
			if (Math.abs(val) > SOGLIA_GYRO)
				System.out.println("sfaso4");
		}
		
		return val;
	}

	int readok=0;
	private short concatenate1(int ms, int ls) {
		short val=0;
		val|=(ms<<8) | ls;
		if (debugByte){
			System.out.println("val1: "+val);
			if (Math.abs(val) > SOGLIA_GYRO)
				System.out.println("sfaso1");
		}
		val = reverse(val);
		if (debugByte){
			System.out.println("val2: "+val);
			if (Math.abs(val) > SOGLIA_GYRO)
				System.out.println("sfaso2");
		}
		
		return val;
	}
	 */
	private static final int bitReverse = 16;
	
	private String toBinary(int b) {
		for (int i=bitReverse; i>=0 ; i--){
			System.out.print( (b&(1<<i))!=0?'1':'0');
		}
		System.out.println();
		return b+"";
	}
	
	/*
	
	private short reverse(short x) {
		short b=0;
		boolean temp;
		for (int i=0; i< bitReverse; i++){
			temp = (x&(1<<i))!=0?true:false;
			if (temp){
				b |= 1<<(bitReverse-1-i);
			}
		}
		
		if (debugByte){
			System.out.print("was: ");
			for (int i=0; i< bitReverse; i++){
				System.out.print( (x&(1<<i))!=0?'1':'0');
			}
			System.out.println();
		
			System.out.print("is: ");
			for (int i=0; i< bitReverse; i++){
				System.out.print( (b&(1<<i))!=0?'1':'0');
			}
			System.out.println();
		}
		return b;
	}
	*/
	Vector3f lastGyro = null, lastAcc=null, lastMag = null;
	private int diffGyro = 0, diffAcc = 0, diffMagne = 0, diffTest=0, diffMs=0;
	/*
	private void calibra(Vector3f gyroVec, Vector3f accVec, Vector3f magVec) {
		if (lastGyro==null || lastAcc==null || lastMag==null){
			lastAcc = accVec;
			lastGyro = gyroVec;
			lastMag = magVec;
			return;
		}
		
		if (!gyroVec.equals(lastGyro)){
			diffGyro++;
		}
		
		testSoglia(gyroVec.x, lastGyro.x, SOGLIA_GYRO, "GyroX");
		testSoglia(gyroVec.y, lastGyro.y, SOGLIA_GYRO, "GyroY");
		testSoglia(gyroVec.z, lastGyro.z, SOGLIA_GYRO, "GyroZ");
		
		if (!accVec.equals(lastAcc)){
			diffAcc++;
		}
		
		testSoglia(accVec.x, lastAcc.x, SOGLIA_GYRO, "AccX");
		testSoglia(accVec.y, lastAcc.y, SOGLIA_GYRO, "AccY");
		testSoglia(accVec.z, lastAcc.z, SOGLIA_GYRO, "AccZ");
		
		if (!magVec.equals(lastMag)){
			diffMagne++;
		}
		
		testSoglia(magVec.x, lastMag.x, SOGLIA_GYRO, "MagX");
		testSoglia(magVec.y, lastMag.y, SOGLIA_GYRO, "MagY");
		testSoglia(magVec.z, lastMag.z, SOGLIA_GYRO, "MagZ");
		
		lastAcc = accVec;
		lastGyro = gyroVec;
		lastMag = magVec;
	}
	
	private void testSoglia(float a, float b, float soglia, String nome){
		if ( Math.abs(a - b) > soglia  ){
			//boolean zero = false;
			System.out.println( nome+"\tdiff: "+(a - b)+"\ta:"+a+"\tb:"+b );
			int x = (int) a, y = (int) b;
			System.out.print("was: ");
			for (int i=0; i< 16; i++){
				System.out.print( (x&(1<<i))!=0?'1':'0');
			}
			System.out.println();
			
			System.out.print("is: ");
			for (int i=0; i< 16; i++){
				System.out.print( (y&(1<<i))!=0?'1':'0');
			}
			System.out.println();
		}
	}
	 */
	private int findOccurence(byte[] read, int index) {
		for (int i = index; i < read.length-1; i++) {
			for (byte b:occorrenze){
				if (read[i] == b && read[i+1] == b) {
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public void connect() {
		new Thread(this).start();
	}

}
