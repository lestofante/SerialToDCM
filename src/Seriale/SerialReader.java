package Seriale;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;

import myGame.DCMlogic;

import com.jme3.math.Vector3f;


public class SerialReader implements SerialPortEventListener {
	private final DCMlogic dcm;
	
	private SerialPort serial;
	
	int baudRate = 460800;
	
	InputStream input;
	
	public SerialReader(DCMlogic dcm) {
		RXTXPathSetter.setPaths();
		this.dcm = dcm;
	}

	public void chiudi(){
		chiudi=true;
	}
	

	String buffer = "";
	byte tmp[] = new byte[1024];
	
	boolean alto = false;
	int conta = 0, soglia = 50;
	long time = System.currentTimeMillis(), totalOuts=0;

	private boolean chiudi=false;

	private boolean connected;
	
	public void connect() {
		try {
			String portName="";
			if ( portName.equals("") ){
				portName = "/dev/ttyUSB0";
			}
			System.out.println("Apro la porta: " + portName);
			CommPortIdentifier portIdentifier = CommPortIdentifier
					.getPortIdentifier(portName);

			if (portIdentifier.isCurrentlyOwned()) {
				System.out.println("Errore: La porta è in uso");
				// info.append("\nErrore: La porta è in uso");
			} else {
				CommPort commPort = portIdentifier.open(this.getClass()
						.getName(), baudRate);

				if (commPort instanceof SerialPort) {
					serial = (SerialPort) commPort;
					serial.setSerialPortParams(baudRate, SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
					setConnected(true);
					input = serial.getInputStream();

					// (new Thread(inputSeriale)).start();
					// OutputStream out = serialPort.getOutputStream();

					// (new Thread(new SerialWriter(out))).start();
					serial.addEventListener(this);
					
					serial.notifyOnDataAvailable(true);

					byte[] b = new byte[1];
					for (int i = 0; i < b.length; i++) {
						b[i] = 'a';
					}
					serial.getOutputStream().write(b);
					// info.append("\nConnesso alla porta seriale: "+portName);
				} else {
					System.out
							.println("Errore: non è ��  porta seriale valida.");
					// info.append("\nErrore: non è una <--?? porta seriale valida");
				}
			}
		} catch (Exception e) {
			setConnected(false);
			// info.append("\nErrore di connessione seriale con la porta: " +
			// portName);
			e.printStackTrace();
		}
	}
	
	@Override
	public void serialEvent(SerialPortEvent arg0) {

		try {
			
			if( input.available() > 0 ){
				int len = input.read(tmp);
				
				//System.out.println(new String(tmp));
				/*
				for (int i=0;i<len;i++){
					System.out.println( tmp[i]&0xffff);
				}
				*/
				
				byte[] outTemp = new byte[len];
				System.arraycopy(tmp, 0, outTemp, 0, len);
				System.out.print(new String(outTemp, "ASCII"));
				
				analyze(outTemp);
				
				/* roba di debug */
				totalOuts+=len;
				if (time +1000 <= System.currentTimeMillis()){
					System.out.println("byte al secondo:"+totalOuts);
					totalOuts=0;
					time = System.currentTimeMillis();
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			chiudi();
		}
		
		if (chiudi){
			try {
				input.close();
				System.out.println("flusso da seriale chiuso, ma mancano: "+input.available());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	int offset = -1;
	private byte tipoSensore;
	private int val;
	Vector3f temp = new Vector3f();
	Vector3f magVec, accVec, gyroVec;
	int choosen=0;
	
	private synchronized void analyze(byte[] read) {
		if (offset==-1){
			System.out.println("Analizing: "+read.length);
			//we don't know where we are on the stream. find occurence of "A", "G" and "M" to find it
			int index = findOccurence(read, 0);
			System.out.println("Index: "+index);
			int tmpIndex = index;
			boolean ok = true;
			if (tmpIndex < 0){
				ok = false;
			}
			while (tmpIndex >= 0 && tmpIndex<read.length){
				if ( !(read[tmpIndex]=='A') && !(read[tmpIndex]=='G') && !(read[tmpIndex]=='M')){
					System.out.println("Error on index: "+index);
					ok = false;
					break;
				}
				tmpIndex+=7;
			}
			if (ok == true){
				System.out.println("Setting index: "+index);
				offset = index;
				analyze(read);
				return;
			}
		}else{
			for (int i=0; i < read.length; i++){
				//System.out.println("leggo dati"+offset);
				switch (offset) {
				case 0:
					tipoSensore = read[i];
					switch (tipoSensore) {
					case 'G':
						//selezionato = writerGyro;
						choosen=1;
						break;
					case 'A':
						//selezionato = writerAcce;
						choosen = 2;
						break;
					case 'M':
						//selezionato = writerAcce;
						choosen = 3;
						break;
					default:
						choosen=0;
						//selezionato = null;
						System.out.print("BHO!: ");
						offset=-2;
						//System.out.println("Index errato, ritento");
						i = read.length;//force exit
						break;
					}
					offset++;
					break;
				case 1:// MS x
					readMSbyte(read[i]);
					offset++;
					break;
				case 2:// LS x
					readLAbyte(read[i]);
					temp.x = val;
					//if(choosen!=null)
					//	if(choosen.equals(gyroQueue))
					//		temp.x += 26;
					//selezionato.print(val + " ");
					offset++;
					break;
				case 3:// MS y
					readMSbyte(read[i]);
					offset++;
					break;
				case 4:// LS y
					readLAbyte(read[i]);
					temp.y = val;
					//if(choosen!=null)
					//	if(choosen.equals(gyroQueue))
					//		temp.y += 7;
					//selezionato.print(val + " ");
					offset++;
					break;
				case 5:// MS z
					readMSbyte(read[i]);
					offset++;
					break;
				case 6:// MS z
					readLAbyte(read[i]);
					temp.z = val;					
					if (choosen != 0){
						
						//float swap;
						
						if (choosen==1){
							gyroVec = temp;
						}
						
						if (choosen==2){//ACC
							accVec = temp;
						}
						
						if (choosen==3){
							magVec = temp;
						}
						choosen=0;
						temp = new Vector3f();
					}
					offset = 0;
					break;
				}
				//magVec=null;
				
				if (gyroVec!= null && accVec!=null && magVec!=null){
					System.out.println("distanza: "+accVec.subtract(magVec)+" dist: "+accVec.distance(magVec));
					dcm.MadgwickAHRSupdate(gyroVec.x, gyroVec.y, gyroVec.z, accVec.x, accVec.y, accVec.z, magVec.x, magVec.y, magVec.z);
					gyroVec = accVec = magVec = null;
					choosen = 0;
			
				}else if (gyroVec!= null && accVec!=null){
					//dcm.MadgwickAHRSupdate(gyroVec.x, gyroVec.y, gyroVec.z, accVec.x, accVec.y, accVec.z, 0, 0, 0);
					//gyroVec = accVec = null;
					//choosen = 0;
				}
			}
		}
		
	}
	
	private int findOccurence(byte[] read, int index) {
		for (int i=index;i<read.length;i++){
			System.out.print(read[i]+" ");
			if (read[i]=='A' || read[i]=='G' || read[i]=='M'){
				return i-index;
			}
			if (read[i]==65 || read[i]==71 || read[i]==77){
				System.out.println("Alternative find ok"); //charset problem? maybe 16bit vs 8 bit?
				return i-index;
			}
		}
		System.out.println();
		return -1;
	}
	
	private void readLAbyte(byte tmp) {
		// quindi il più significativo, che sarà già stato letto, spostalo a
		// sinistra
		val = val << 8;
		// copia in val i byte ad uno del byte meno significativo, completando
		// così il dato (or logico, per evitare casini con i signed/unsigned se
		// avessi usato +)
		val |= tmp & 0xff;
	}

	private void readMSbyte(byte tmp) {
		// azzera il valore precendente
		val = 0;
		// e metti il valore di tmp in val (or logico, per evitare casini con i
		// signed/unsigned se avessi usato +)
		val |= tmp;
	}

	public boolean isConnected() {
		return connected;
	}

	private void setConnected(boolean connected) {
		this.connected = connected;
	}
	
}