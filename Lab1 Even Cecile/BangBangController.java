/* Group 21
 * Even Wang - 260633630
 * Cecile Robert-Michon - 260552816
 */

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

public class BangBangController implements UltrasonicController {
	private final int bandCenter, bandwith;
	private final int motorLow, motorHigh;
	// Low value for more accuracy
	private final int motorStraight = 200;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private int distance;
	private int currentLeftSpeed;

	public BangBangController(int bandCenter, int bandwith, int motorLow,
			int motorHigh) {
		// Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		currentLeftSpeed = 0;
	}

	/*
	 * Before running the program, the robot must be placed to the right of the
	 * wall with the sensor pointing to the wall (perpendicularly)
	 */

	@Override
	public void processUSData(int distance) {

		this.distance = distance;

		// process a movement based on the us distance passed in
		// (BANG-BANG style)

		// calculate deviation from wanted distance
		int delta = distance - bandCenter;

		// rotate sensor to an angle of 45 degrees
		Motor.B.rotateTo(45);

		// Within tolerance
		if (Math.abs(delta) <= bandwith) {
			// go straight
			rightMotor.setSpeed(motorStraight);
		}

		// Too far
		else if (delta > bandwith) {
			// turn right by accelerating right motor
			rightMotor.setSpeed(motorHigh);
		}

		// Too close
		else if (delta < bandwith) {
			// turn left by decelerating right motor
			rightMotor.setSpeed(motorLow);
		}

	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
