package reader;

import myGame.DCMlogic;

public abstract class SensorReader {

	protected final DCMlogic dcm;
	
	public SensorReader(DCMlogic dcm) {
		this.dcm = dcm;
	}

	public abstract void connect();

}
