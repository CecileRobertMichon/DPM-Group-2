import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.robotics.Color;

/* 
 * OdometryCorrection.java
 */

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private ColorSensor colorSensor = new ColorSensor(SensorPort.S2);

	private final int LIGHTSENSOR_THRESHOLD = 450;
	private final double SENSOR_DISTANCE = 12.4;
	private final double TILE_LENGTH = 30.48;
	private final double HALF_TILE = 15.24;

	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;

		while (true) {
			correctionStart = System.currentTimeMillis();

			// put your correction code here
			int light = colorSensor.getNormalizedLightValue();
			// LCD.drawString("Light : " + light, 0, 5);
			if (light < LIGHTSENSOR_THRESHOLD) {

				Sound.beep();

				// if horizontal line
				if (directionNorthSouth(odometer.getTheta())) {
					correctY();

					// if vertical line
				} else {
					correctX();
				}

			}

			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}

	// returns true if the robot is following the y direction, false if
	// following the x direction
	private boolean directionNorthSouth(double theta) {
		if (Math.sin(Math.toRadians(theta)) < 0.5
				&& Math.sin(Math.toRadians(theta)) > -0.5) {
			// The line crossed is a Y line
			return true;
		} else {
			// The line crossed is a X line
			return false;
		}
	}

	private void correctX() {
		// calculate distance from closest line
		double offset = SENSOR_DISTANCE
				* Math.sin(Math.toRadians(odometer.getTheta()));
		// Add the line's distance from 0 to the offset
		double realX = odometer.getX() - (odometer.getX() % TILE_LENGTH)
				+ HALF_TILE + offset;
		;
		odometer.setX(realX);
	}

	private void correctY() {
		// calculate distance from closest line
		double offset = SENSOR_DISTANCE
				* Math.cos(Math.toRadians(odometer.getTheta()));
		// Add the line's distance from 0 to the offset
		double realY = odometer.getY() - (odometer.getY() % TILE_LENGTH)
				+ HALF_TILE + offset;
		;
		odometer.setY(realY);
	}

}