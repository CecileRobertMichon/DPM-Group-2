import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

/*
 *  Group 21
 *  Cecile Robert-Michon 260552816
 *  Even Wang - 260633630
 */

public class Lab3 {
	private static NXTRegulatedMotor leftMotor = Motor.A;
	private static NXTRegulatedMotor rightMotor = Motor.C;
	public static double destX, destY;

	public static void main(String[] args) {

		// wait 5 seconds
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// there is nothing to be done here because it is not expected that
			// the odometer will be interrupted by another thread
		}

		/*
		 * Calibration Code - used to find exact radius and width values
		 * 
		 * leftMotor.setAcceleration(2000); rightMotor.setAcceleration(2000);
		 * leftMotor.setSpeed(100); rightMotor.setSpeed(100);
		 * leftMotor.rotate(1650, true); rightMotor.rotate(1650, false);
		 * leftMotor.rotate(2670, true); rightMotor.rotate(-2670, false);
		 */

		int buttonChoice;
		do {
			// clear the display
			LCD.clear();

			// ask the user whether the robot should avoid obstacles
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString(" Avoid | Drive  ", 0, 2);
			LCD.drawString("object | to   ", 0, 3);
			LCD.drawString("       | point ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) {

			// start the odometer, the odometer display and the navigation with
			// obstacle
			Odometer odo = new Odometer();
			OdometryDisplay odoDisplay = new OdometryDisplay(odo);
			NavigationObstacle nav = new NavigationObstacle(odo);

			odo.start();
			odoDisplay.start();
			nav.start();

		} else {

			// start the odometer, the odometer display and the navigation
			// without obstacle
			Odometer odo = new Odometer();
			Navigation nav = new Navigation(odo);
			OdometryDisplay odoDisplay = new OdometryDisplay(odo);

			odo.start();
			odoDisplay.start();
			nav.start();
		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;
		System.exit(0);
	}
}
