import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

/*
 *  Group 21
 *  Cecile Robert-Michon 260552816
 *  Even Wang - 260633630
 */


public class USLocalizer {
	public enum LocalizationType {
		FALLING_EDGE, RISING_EDGE
	};

	public static int ROTATION_SPEED = 100;
	private final int NOISE = 5;
	private final int WALL_CENTER = 30;

	private int distance;
	private Odometer odo;
	private UltrasonicSensor us;
	private LocalizationType locType;
	private Navigation nav;

	public USLocalizer(Odometer odo, UltrasonicSensor us,
			LocalizationType locType, Navigation nav) {
		this.odo = odo;
		this.us = us;
		this.locType = locType;
		this.nav = nav;

		// switch off the ultrasonic sensor
		us.off();
	}

	public void doLocalization() {
		double angleA, angleB;

		if (locType == LocalizationType.FALLING_EDGE) {
			// rotate the robot clockwise until it sees no wall
			awayFromWall(true);
			odo.setTheta(0);

			// keep rotating until the robot sees a wall, then latch the angle
			findWall(true);
			angleA = odo.getTheta();

			// switch direction and wait until it sees no wall
			awayFromWall(false);

			// keep rotating until the robot sees a wall, then latch the angle
			findWall(false);
			angleB = odo.getTheta();

			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			double deltaTheta = calculateHeadingFalling(angleA, angleB);

			// update the odometer position
			nav.turnTo(-deltaTheta);
			odo.setPosition(new double[] { 0.0, 0.0, 0.0 }, new boolean[] {
					true, true, true });
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall. This
			 * is very similar to the FALLING_EDGE routine, but the robot will
			 * face toward the wall for most of it.
			 */

			// rotate until robot sees the wall
			findWall(true);
			odo.setTheta(0);

			// rotate until it no longer sees the wall and latch angle
			awayFromWall(true);
			angleA = odo.getTheta();

			findWall(false);

			// rotate in opposite direction until it sees a wall
			awayFromWall(false);
			angleB = odo.getTheta();

			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			double deltaTheta = calculateHeadingRising(angleA, angleB);

			// update the odometer position (example to follow:)
			nav.turnTo(-deltaTheta);
			odo.setPosition(new double[] { 0.0, 0.0, 0.0 }, new boolean[] {
					true, true, true });

		}
	}

	// rotate until it finds a wall
	private void findWall(boolean clockwise) {
		nav.rotate(clockwise);
		this.distance = getFilteredData();
		while (this.distance > WALL_CENTER - NOISE) {
			this.distance = getFilteredData();
		}
		nav.stop();
	}

	// rotate until it is not facing the wall
	private void awayFromWall(boolean clockwise) {
		nav.rotate(clockwise);
		while (this.distance < WALL_CENTER + NOISE) {
			this.distance = getFilteredData();
		}
		nav.stop();
	}

	// use tutorial formulas to calculate the angle to correct the heading by
	private double calculateHeadingFalling(double angleA, double angleB) {
		double angle = (angleA + angleB) / 2.0;

		if (angleA > angleB) {
			angle = 50.0 - angle;
		} else {
			angle = 230.0 - angle;
		}
		return (angle % 360);
	}

	private double calculateHeadingRising(double angleA, double angleB) {
		double angle = (angleA + angleB) / 2.0;

		if (angleB > angleA) {
			angle = 45.0 - angle;
		} else {
			angle = 230.0 - angle;
		}

		return (angle % 360);
	}

	private int getFilteredData() {
		int distance;

		// do a ping
		us.ping();
		// wait for the ping to complete
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}

		// there will be a delay here
		distance = us.getDistance();
		if (distance > 50) {
			distance = 50;
		}
		// LCD.drawString("dist: " + distance, 0, 4);
		return distance;

	}

}
