import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

public class Lab5 {
	

	public static void main(String[] args) {

		// Start the launcher thread
		Launcher launcher = new Launcher();
		launcher.start();
		
		Button.waitForAnyPress();
		System.exit(0);
	}
}
