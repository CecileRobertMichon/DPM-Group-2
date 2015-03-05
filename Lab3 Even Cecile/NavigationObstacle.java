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

public class NavigationObstacle extends Thread {

	private Odometer odometer;
	private UltrasonicSensor us;
	private AvoidObstacle obs;
	private NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private final double DISTANCE_TOLERANCE = 0.5;
	private final double ANGLE_TOLERANCE = 0.25;
	private final int MOTOR_STRAIGHT = 250;
	private final int MOTOR_ROTATE = 150;
	private final double RADIUS = 2.118;
	private final double WIDTH = 15.7085;
	private boolean running = false;

	// constructor
	public NavigationObstacle(Odometer odo) {
		this.odometer = odo;
		this.us = new UltrasonicSensor(SensorPort.S2);
		this.us.continuous();
		this.obs = new AvoidObstacle(us, this);
	}

	public void run() {
		// travel to the points in lab instructions
		travelTo(0, 60.96);
		travelTo(60.96, 0);
		// stop motors
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
	}

	public void travelTo(double x, double y) {
		running = true;

		// while x or y different from odometer's measured coordinates (ie. the
		// robot has not reached its final destination yet)
		while ((Math.abs(x - odometer.getX()) > DISTANCE_TOLERANCE || Math
				.abs(y - odometer.getY()) > DISTANCE_TOLERANCE)) {

			// calculate angle to point
			double minTheta = (Math.atan2(x - odometer.getX(),
					y - odometer.getY()))
					* (180.0 / Math.PI);
			// calculate distance to point
			double distance = Math.sqrt(Math.pow(x - odometer.getX(), 2)
					+ Math.pow(y - odometer.getY(), 2));

			// Make sure theta is positive
			if (minTheta < 0) {
				minTheta += 360;
			}

			// Turn to right angle first
			turnTo(minTheta);
			// Then move forward
			goForward(distance);
		}
		// stop the motors
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);

		running = false;
	}

	private void goForward(double distance) {

		running = true;
		int wallDist;

		// drive straight for given distance
		leftMotor.setSpeed(MOTOR_STRAIGHT);
		rightMotor.setSpeed(MOTOR_STRAIGHT);

		int travelDistance = convertDistance(RADIUS, distance);

		leftMotor.rotate(travelDistance, true);
		rightMotor.rotate(travelDistance, true);

		// continuously check for US data to detect obstacle while robot is
		// going forward
		while (rightMotor.isMoving()) {
			wallDist = us.getDistance();
			// call method to avoid obstacle if object is detected
			if (wallDist < 15) {
				obs.avoid();
			}
		}
		running = false;

	}

	public void turnTo(double theta) {
		running = true;
		leftMotor.setSpeed(MOTOR_ROTATE);
		rightMotor.setSpeed(MOTOR_ROTATE);

		// relative angle robot has to rotate by
		double errorTheta = theta - odometer.getTheta();

		while (Math.abs(errorTheta % 360) > ANGLE_TOLERANCE) {

			errorTheta = theta - odometer.getTheta();

			if (errorTheta < -180.0) {
				// turn right
				leftMotor
						.rotate(convertAngle(RADIUS, WIDTH,
								360 - Math.abs(errorTheta)), true);
				rightMotor
						.rotate(-convertAngle(RADIUS, WIDTH,
								360 - Math.abs(errorTheta)), false);
			} else if (errorTheta < 0.0) {
				// turn left
				leftMotor.rotate(
						-convertAngle(RADIUS, WIDTH, Math.abs(errorTheta)),
						true);
				rightMotor.rotate(
						convertAngle(RADIUS, WIDTH, Math.abs(errorTheta)),
						false);
			} else if (errorTheta > 180.0) {
				// turn left
				leftMotor.rotate(
						-convertAngle(RADIUS, WIDTH, 360 - errorTheta), true);
				rightMotor.rotate(
						convertAngle(RADIUS, WIDTH, 360 - errorTheta), false);
			} else {
				// turn right
				leftMotor.rotate(convertAngle(RADIUS, WIDTH, errorTheta), true);
				rightMotor.rotate(-convertAngle(RADIUS, WIDTH, errorTheta),
						false);
			}

		}
		running = false;
	}

	public boolean isNavigating() {
		return running;
	}

	// Helper methods to convert distance and angle in degrees the motors have
	// to rotate by
	public static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	public static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}
