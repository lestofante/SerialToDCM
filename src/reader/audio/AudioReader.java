package reader.audio;

import java.io.UnsupportedEncodingException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.Mixer.Info;

import com.jme3.math.Vector3f;

import myGame.DCMlogic;
import reader.SensorReader;

public class AudioReader extends SensorReader implements Runnable{

	public AudioReader(DCMlogic dcm) {
		super(dcm);
	}
	
	private int val;
	private int offset=-1;
	private byte tipoSensore;
	private int choosen;
	private Vector3f temp = new Vector3f();
	private Vector3f gyroVec;
	private Vector3f accVec;
	private Vector3f magVec;
	boolean stampaValori = false;
	

	@Override
	public void run() {
		AudioFormat format = new AudioFormat(90000.0f, 16, 1, true, false);
		TargetDataLine  microphone = null;
		try {
			
			
			Info choosen = null;
			for (Info i:AudioSystem.getMixerInfo()){
				System.out.println(i.getName());
				if (i.getName().contains("Joystick") && i.getName().contains("plughw")){
					choosen = i;
					System.out.println(i.getName()+" THIS HAS BEEN CHOOSED");
					break;
				}
			}
			
			if (choosen!=null)
				microphone = AudioSystem.getTargetDataLine(format, choosen);
				/*AudioSystem.getMixer(choosen).get
				for (Line l:AudioSystem.getMixer(choosen).getTargetLines()){
					l.
				}*/
			
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		while(true){
			read = microphone.read(buffer, 0, buffer.length ); //we must read 2 byte at time
			sum+=read;
			
			analyze(buffer);
			
			if (min > read){
				min = read;
			}
			if (max < read){
				max = read;
			}
			/*
			for (int i=0; i < read; i++){
				printBinary(buffer[i]);
			}
			
			System.out.println(); //a capo
			*/
			deltaT = System.currentTimeMillis()-tempo; 
			if (deltaT > 1000){
				System.out.println( "byte al secondo: " + sum/(deltaT/1000.0)+" min: "+min+" max: "+max );
				sum =0;
				min=Integer.MAX_VALUE;
				max = Integer.MIN_VALUE;
				
				tempo = System.currentTimeMillis();
				
				stampaValori = true;
			}
		}
	}
	
	private synchronized void analyze(byte[] read) {
		if (offset == -1) {
			//System.out.println("Analizing: " + read.length);
			// we don't know where we are on the stream. find occurence of "A",
			// "G" and "M" to find it
			int index = findOccurence(read, 0);
			//System.out.println("Index: " + index);
			int tmpIndex = index;
			boolean ok = true;
			if (tmpIndex < 0) {
				ok = false;
			}
			while (tmpIndex >= 0 && tmpIndex < read.length) {
				System.out.println("check index is looking at " + tmpIndex);
				if (!(read[tmpIndex] == 'A') && !(read[tmpIndex] == 'G') && !(read[tmpIndex] == 'M')) {
					System.out.println("Continuity error on index: " + tmpIndex + " is: " + read[tmpIndex] + " len is: " + read.length);
					try {
						System.out.println("Checked string is: " + new String(read, "ASCII"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ok = false;
					break;
				}
				tmpIndex += 7;
			}
			if (ok == true) {
				System.out.println("Setting index: " + index);
				offset = -index;
				analyze(read);
				return;
			}
		} else {
			for (int i = 0; i < read.length; i++) {
				//System.out.println("leggo dati offeset: "+offset+" i: "+i+" di: "+read.length+" valore: "+read[i]);
				switch (offset) {
				case 0:
					tipoSensore = read[i];
					switch (tipoSensore) {
					case 'G':
						// selezionato = writerGyro;
						choosen = 1;
						break;
					case 'A':
						// selezionato = writerAcce;
						choosen = 2;
						break;
					case 'M':
						// selezionato = writerAcce;
						choosen = 3;
						break;
					default:
						choosen = 0;
						// selezionato = null;
						System.out.println("BHO!: " + tipoSensore);
						offset = -2;
						// System.out.println("Index errato, ritento");
						i = read.length;// force exit
						break;
					}
					offset++;
					break;
				case 1:// MS x
					val=0;
					readMSbyte(read[i]);
					offset++;
					break;
				case 2:// LS x
					readLAbyte(read[i]);
					temp.x = val;
					// if(choosen!=null)
					// if(choosen.equals(gyroQueue))
					// temp.x += 26;
					// selezionato.print(val + " ");
					offset++;
					break;
				case 3:// MS y
					val=0;
					readMSbyte(read[i]);
					offset++;
					break;
				case 4:// LS y
					readLAbyte(read[i]);
					temp.y = val;
					// if(choosen!=null)
					// if(choosen.equals(gyroQueue))
					// temp.y += 7;
					// selezionato.print(val + " ");
					offset++;
					break;
				case 5:// MS z
					val=0;
					readMSbyte(read[i]);
					offset++;
					break;
				case 6:// MS z
					readLAbyte(read[i]);
					temp.z = val;
					if (choosen != 0) {

						// float swap;

						if (choosen == 1) {
							
							// now output register are x, y, x, and we translate them relative to gyro
							/*
							float swap = temp.x;
							temp.x = temp.y;
							temp.y = swap;
							
							temp.x = -temp.x;
							temp.y = -temp.y;
							*/
							
							//output register are x, y, z, and are aligned as our referencew system
							gyroVec = temp;
							//System.out.println("gyro: " + temp);
						}

						if (choosen == 2) {// ACC
							
							//output register are x, y, z, and we translate them relative to gyro
							/*
							float swap = temp.z;
							temp.z = temp.y;
							temp.y = swap;
							*/
							//temp.y = -temp.y;
							//temp.x = -temp.x;
							//temp.z = -temp.z;
							//System.out.println("Acc: " + temp);
							accVec = temp;
						}

						if (choosen == 3) {
							//output register are x, Z, Y, and we translate them relative to gyro
							/*
							float swap = temp.x;
							temp.x = temp.y;
							temp.y = swap;
							*/
							
							//temp.y = -temp.y;
							//temp.x = -temp.x;
							//System.out.println("Magne: " + temp);
							magVec = temp;
						}
						choosen = 0;
						temp = new Vector3f();
					}
					offset = 0;
					break;
				default:
					offset++;
				}
				// magVec=null;

				if (gyroVec != null && accVec != null && magVec != null) {
					if (stampaValori){
						System.out.println("Gyro:"+gyroVec+" acc: "+accVec+" magne: "+magVec);
						stampaValori = false;
					}
					
					if (dcm != null) {
						dcm.MadgwickAHRSupdate(gyroVec.x, gyroVec.y, gyroVec.z,
								accVec.x, accVec.y, accVec.z, magVec.x,
								magVec.y, magVec.z);
					}
					gyroVec = accVec = magVec = null;
					choosen = 0;
				} 
				if (gyroVec != null && accVec != null) {
					if (stampaValori){
						System.out.println("Gyro:"+gyroVec+" acc: "+accVec);
						stampaValori = false;
					}
					dcm.MadgwickAHRSupdate(gyroVec.x, gyroVec.y, gyroVec.z, accVec.x, accVec.y, accVec.z, 0, 0, 0);
					gyroVec = accVec = null;
					choosen = 0;
				}
			}
		}

	}

	private int findOccurence(byte[] read, int index) {
		for (int i = index; i < read.length; i++) {
			System.out.print(read[i] + " ");
			if (read[i] == 'A' || read[i] == 'G' || read[i] == 'M') {
				return i - index;
			}
			if (read[i] == 65 || read[i] == 71 || read[i] == 77) {
				System.out.println("Alternative find ok"); // charset problem?
															// maybe 16bit vs 8
															// bit?
				return i - index;
			}
		}
		System.out.println();
		return -1;
	}

	private void readLAbyte(byte tmp) {
		// copia in val i byte ad uno del byte meno significativo, completando
		// così il dato (or logico, per evitare casini con i signed/unsigned se
		// avessi usato +)
		val |= tmp & 0xff;
	}

	private void readMSbyte(byte tmp) {
		//copia nel byte più significativo ma usa il bytepiù a sinistra come segno!
		val = tmp;//keep the sign!
		val = val << 8;
	}

	@Override
	public void connect() {
		new Thread(this).start();
	}

}
