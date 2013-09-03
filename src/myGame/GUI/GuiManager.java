package myGame.GUI;

import com.jme3.math.Vector3f;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Label;


public class GuiManager {

	private static long lastUpdate;
	private static long interval = 100000000;

	public static void updateLabels(Nifty nifty, Vector3f acc, Vector3f gyro, Vector3f mag) {
		if(System.nanoTime()-lastUpdate > interval){
			lastUpdate = System.nanoTime();
			Label niftyElement = nifty.getCurrentScreen().findNiftyControl(
					"acc", Label.class);
			niftyElement.setText("Accelerometer: "+acc.toString());

			niftyElement = nifty.getCurrentScreen().findNiftyControl(
					"gyro", Label.class);
			niftyElement.setText("Gyroscope: "+gyro.toString());

			niftyElement = nifty.getCurrentScreen().findNiftyControl(
					"mag", Label.class);
			niftyElement.setText("Magnetometer: "+mag.toString());
		}
	}

}
