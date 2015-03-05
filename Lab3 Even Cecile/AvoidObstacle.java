import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

/*
 *  Group 21
 *  Cecile Robert-Michon 260552816
 *  Even Wang - 260633630
 */

public class AvoidObstacle {
	UltrasonicSensor us;
	NavigationObstacle nav;
	Odometer odo;
	private static final int bandCenter = 18, bandWidth = 3;
	private static final int motorLow = 30, motorHigh = 300;
	private final int motorStraight = 200;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private int distance;
	private final int ROTATE_SPEED = 100;

	public AvoidObstacle(UltrasonicSensor us, NavigationObstacle nav) {
		this.us = us;
		this.nav = nav;
		this.odo = new Odometer();
	}

	// Too close
	public void avoid() {

		// turn 90 degrees clockwise
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		leftMotor.rotate(convertAngle(2.118, 15.0785, 90.0), true);
		rightMotor.rotate(-convertAngle(2.118, 15.0785, 90.0), false);

		// rotate sensor to an angle of 90 degrees
		Motor.B.rotate(-90);

		// go forward
		leftMotor.forward();
		rightMotor.forward();

		// Follow the obstacle for a 10 seconds
		long start_time = System.currentTimeMillis();
		long wait_time = 10000;
		long end_time = start_time + wait_time;

		while (System.currentTimeMillis() < end_time) {

			this.distance = us.getDistance();

			// Follow the obstacle using BANG-BANG style

			// calculate deviation from wanted distance
			int delta = distance - bandCenter;

			// Within tolerance
			if (Math.abs(delta) <= bandWidth) {
				// go straight
				rightMotor.setSpeed(motorStraight);
			}

			// Too far
			else if (delta > bandWidth) {
				// turn right by accelerating right motor
				rightMotor.setSpeed(motorHigh);
			}

			// Too close
			else if (delta < bandWidth) {
				// turn left by decelerating right motor
				rightMotor.setSpeed(motorLow);
			}

		}
		Motor.B.rotate(90);
		// stop motors
		rightMotor.setSpeed(0);
		leftMotor.setSpeed(0);
	}

	// Helper methods to convert distance and angle in degrees the motors have
	// to rotate by
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}
