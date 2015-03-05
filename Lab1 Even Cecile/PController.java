/* Group 21
 * Even Wang - 260633630
 * Cecile Robert-Michon - 260552816
 */

import lejos.nxt.*;

public class PController implements UltrasonicController {

	private final int bandCenter, bandwith;
	private final int motorStraight = 150, FILTER_OUT = 20;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private int distance;
	private int currentLeftSpeed;
	private int filterControl;

	public PController(int bandCenter, int bandwith) {
		// Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		currentLeftSpeed = 0;
		filterControl = 0;
	}

	/*
	 * Before running the program, the robot must be placed to the right of the
	 * wall with the sensor pointing to the wall (perpendicularly)
	 */

	@Override
	public void processUSData(int distance) {

		// rotate sensor to an angle of 45 degrees
		Motor.B.rotateTo(45);

		// rudimentary filter
		if (distance == 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the
			// filter value
			filterControl++;
		} else if (distance == 255) {
			// true 255, therefore set distance to 255
			this.distance = distance;
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			this.distance = distance;
		}
		// process a movement based on the us distance passed in (P style)

		// set speed value based on linear function
		int speed = 50 * (this.distance - this.bandCenter) + motorStraight;

		// upper and lower bounds
		if (speed > 550) {
			speed = 550;
		} else if (speed < 5) {
			speed = 5;
		}

		rightMotor.setSpeed(speed);

	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}

}
