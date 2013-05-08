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
	byte[] occorrenze = {'A', 'G', 'M', 'T'};
	
	private synchronized void analyze(byte[] read) {
		
		int findOccurence = 0;
		while ( (findOccurence=findOccurence(read, findOccurence)) > -1){
			//System.out.println("findOccurence: "+findOccurence);
			//read data
			boolean validByte = true;
			int i=findOccurence;
			char sensore = (char) read[i];
			
			if (sensore == 'b'){
				//fine del pacchetto!!
				return;
			}
			
			int byteLettiValidi=0;
			
			Vector3f lettura= new Vector3f();
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
					
					switch(byteLettiValidi){
					case 0:
						readMSbyte(read[i]);
						break;
					case 1:
						readLAbyte(read[i]);
						lettura.x = val;
						break;
					case 2:
						readMSbyte(read[i]);
						break;
					case 3:
						readLAbyte(read[i]);
						lettura.y = val;
						break;
					case 4:
						readMSbyte(read[i]);
						break;
					case 5:
						readLAbyte(read[i]);
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
				switch(sensore){
				case 'G':
					gyroVec = lettura;
					break;
				case 'M':
					magVec = lettura;
					break;
				case 'A':
					accVec = lettura;
					break;
				case 'T':
					System.out.println("test vect: "+lettura);
					break;
				default:
					System.out.println("grave errore sensore "+sensore);
				}
			}
			
			if (gyroVec != null && accVec != null && magVec != null) {
				if (stampaValori){
					//System.out.println("Gyro:"+gyroVec+" acc: "+accVec+" magne: "+magVec);
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
			/*
			if (gyroVec != null && accVec != null) {
				if (stampaValori){
					System.out.println("Gyro:"+gyroVec+" acc: "+accVec);
					stampaValori = false;
				}
				dcm.MadgwickAHRSupdate(gyroVec.x, gyroVec.y, gyroVec.z, accVec.x, accVec.y, accVec.z, 0, 0, 0);
				gyroVec = accVec = null;
				choosen = 0;
			}
			*/
		}
	}
	
	private int findOccurence(byte[] read, int index) {
		for (int i = index; i < read.length-1; i++) {
			for (byte b:occorrenze){
				if (read[i] == b && read[i+1] == b) {
					return i;
				}
			}
		}
		//System.out.println();
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
