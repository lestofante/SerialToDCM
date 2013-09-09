package myGame;

import reader.SensorReader;
import reader.USB.USBReader;
import myGame.GUI.GuiManager;
import DCM.AcceControl;
import DCM.DCMControl;
import DCM.MagControl;
import DCM.GyroControl;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/*
 * 
 * Brought by you by lestofante and goffredo goffrei
 * 
 */
public class Main extends SimpleApplication implements ScreenController {

	public static void main(String args[]) {
		final Main app = new Main();
		app.start();
	}
	
	Node rootNode1 = new Node();
	Node rootNode2 = new Node();
	Node rootNode3 = new Node();
	Node rootNode4 = new Node();

	AxesObject obj1;
	AxesObject obj2;
	AxesObject obj3;
	//AxesObject obj4;
	
	DCMlogic dcm = new DCMlogic();
	
	SensorReader sensorInput = new USBReader(dcm);
	
	private Nifty nifty;
	
	@Override
	public void onEndScreen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartScreen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void simpleInitApp() {
		
		/** Configure cam to look at scene */
		cam.setLocation(new Vector3f(60, 0f, 0f));
		cam.lookAt(new Vector3f(0, 0, 15), Vector3f.UNIT_Y);
		
		/*Setup viewports*/
		setupViewPorts();
		
		obj1 = new AxesObject(assetManager);
		obj2 = new AxesObject(assetManager);
		obj3 = new AxesObject(assetManager);
		//obj4 = new AxesObject(assetManager);
		
		
		Sphere sphere = new Sphere(10, 10, 0.05f);
		final Geometry point = new Geometry("sphere", sphere);
		
		final Material material1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		material1.setColor("Color", new ColorRGBA(0, 0, 1, 1));
		
		point.setMaterial(material1);
		Node obj5 = new Node();
		
		obj5.attachChild(point);
		
		obj1.getGeometry().addControl(new DCMControl(dcm));
		obj2.getGeometry().addControl(new AcceControl(dcm));
		obj3.getGeometry().addControl(new GyroControl(dcm));
		//obj4.getGeometry().addControl(new MagControl(dcm, assetManager));
		
		obj5.addControl(new MagControl(dcm));
		
		rootNode.attachChild(rootNode1);
		rootNode.attachChild(rootNode2);
		rootNode.attachChild(rootNode3);
		rootNode.attachChild(rootNode4);
		
		rootNode1.attachChild(obj1.getGeometry());
		rootNode2.attachChild(obj2.getGeometry());
		rootNode3.attachChild(obj3.getGeometry());
		//rootNode4.attachChild(obj4.getGeometry());		
		
		rootNode4.attachChild(obj5);
		
		initializeGUI();
		
		sensorInput.connect();
		/**
		Initialize GUI
		final NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
				inputManager, audioRenderer, guiViewPort);
		nifty = niftyDisplay.getNifty();
		nifty.fromXml("Interface/HUD.xml", "HUD", this);
		guiViewPort.addProcessor(niftyDisplay);
		java.util.logging.Logger.getAnonymousLogger().getParent()
				.setLevel(java.util.logging.Level.SEVERE);
		java.util.logging.Logger.getLogger("de.lessvoid.nifty.*").setLevel(
				java.util.logging.Level.SEVERE);
		*/
	}

	private void initializeGUI() {
		
		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
				inputManager, audioRenderer, guiViewPort);
		nifty = niftyDisplay.getNifty();
		nifty.fromXml("Interface/HUD.xml", "HUD", this);
		guiViewPort.addProcessor(niftyDisplay);
		java.util.logging.Logger.getAnonymousLogger().getParent()
				.setLevel(java.util.logging.Level.SEVERE);
		java.util.logging.Logger.getLogger("de.lessvoid.nifty.*").setLevel(
				java.util.logging.Level.SEVERE);
		
	}

	private void setupViewPorts() {
		
		cam.setViewPort(0.0f,0.5f,0.0f,0.5f); // resize the viewPort
		cam.setLocation(new Vector3f(0.0f, 40.0f, 0.0f));
		//Quaternion q = new Quaternion();
		//cam.setRotation(q);
		cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
		
		Camera cam2 =  cam.clone();
		cam2.setViewPort(0.5f,1.0f,0.0f,0.5f); // resize the viewPort
		cam2.setLocation(new Vector3f(new Vector3f(0.0f, 40.0f, 0.0f)));
		cam2.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
		Camera cam3 = cam.clone();
		cam3.setViewPort(0.5f,1.0f,0.5f,1.0f); // resize the viewPort
		cam3.setLocation(new Vector3f(new Vector3f(0.0f, 40.0f, 0.0f)));
		cam3.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
		Camera cam4 = cam.clone();
		cam4.setViewPort(0.0f,0.5f,0.5f,1.0f); // resize the viewPort
		cam4.setLocation(new Vector3f(new Vector3f(0.0f, 10.0f, 0.0f)));
		cam4.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
		
		flyCam.setEnabled(false);
		
		MyFlyBy fb1 = new MyFlyBy(cam);
		fb1.registerWithInput(inputManager);
		fb1.setDragToRotate(true);
		MyFlyBy fb2 = new MyFlyBy(cam2);
		fb2.registerWithInput(inputManager);
		fb2.setDragToRotate(true);
		MyFlyBy fb3 = new MyFlyBy(cam3);
		fb3.registerWithInput(inputManager);
		fb3.setDragToRotate(true);
		MyFlyBy fb4 = new MyFlyBy(cam4);
		fb4.registerWithInput(inputManager);
		fb4.setDragToRotate(true);
		
		float speed = 20;
		fb1.setMoveSpeed(speed);
		fb2.setMoveSpeed(speed);
		fb3.setMoveSpeed(speed);
		fb4.setMoveSpeed(speed);
		
		ViewPort view1 = viewPort;
		view1.setClearFlags(true, true, true);
		view1.clearScenes();
		view1.attachScene(rootNode1);
		view1.setBackgroundColor(ColorRGBA.Black);
		
		ViewPort view2 = renderManager.createMainView("View of camera 2", cam2);
		view2.setClearFlags(true, true, true);
		view2.attachScene(rootNode2);
		view2.setBackgroundColor(ColorRGBA.LightGray);
		
		ViewPort view3 = renderManager.createMainView("View of camera 3", cam3);
		view3.setClearFlags(true, true, true);
		view3.attachScene(rootNode3);
		view3.setBackgroundColor(ColorRGBA.Brown);
		
		ViewPort view4 = renderManager.createMainView("View of camera 4", cam4);
		view4.setClearFlags(true, true, true);
		view4.attachScene(rootNode4);
		view4.setBackgroundColor(ColorRGBA.DarkGray);
		
		
	}

	@Override
	public void simpleUpdate(float tpf) {
		super.simpleUpdate(tpf);
		GuiManager.updateLabels(nifty,dcm.getAcc(),dcm.getSimpleGyro(),dcm.getMagn());
	}
	
	@Override
	public void bind(Nifty arg0, Screen arg1) {
		// TODO Auto-generated method stub
		
	}

}