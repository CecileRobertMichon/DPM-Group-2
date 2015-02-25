// Cecile Robert-Michon 260552816
// Even Wang - 260633630

import lejos.nxt.LCD;
import lejos.nxt.Motor;

/*
 * Odometer.java
 */

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;
	
	// Tachometer count
	private int tachoLeft, tachoRight;
	
	// Radius and distance between left and rigth wheel
	private double leftRadius, rightRadius, width;

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer() {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		leftRadius = 2.2;
		rightRadius = 2.2;
		width = 16.2;
		lock = new Object();
	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;

		while (true) {
			updateStart = System.currentTimeMillis();
			
			// Get each motor's tachometer count
			int newTachoLeft = Motor.A.getTachoCount();
			int newTachoRight = Motor.B.getTachoCount();
			
			// calculate change in tachometer
			int deltaTachoLeft = newTachoLeft - tachoLeft;
			int deltaTachoRight = newTachoRight - tachoRight;
			
			tachoLeft = newTachoLeft;
			tachoRight = newTachoRight;
			
			// Calculate left and right arc distance according to formula in lab tutorial
			double leftArcDistance = Math.toRadians(deltaTachoLeft * leftRadius);
			double rightArcDistance = Math.toRadians(deltaTachoRight * rightRadius);
			
			double deltaTheta = (leftArcDistance - rightArcDistance) / width;
			double deltaArcLength = (leftArcDistance + rightArcDistance) / 2.0;

			synchronized (lock) {
				// set the new values for x, y and theta
				setX(getX() + deltaArcLength * Math.sin(Math.toRadians(getTheta()) + deltaTheta / 2));
				setY(getY() + deltaArcLength * Math.cos(Math.toRadians(getTheta()) + deltaTheta / 2));
				setTheta(Math.toDegrees(deltaTheta) + getTheta());
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}