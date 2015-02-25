/*
 *  Group 21
 *  Cecile Robert-Michon 260552816
 *  Even Wang - 260633630
 */

import java.util.ArrayList;

import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;

public class LightLocalizer {
	private final int MOTOR_STRAIGHT = 150;
	private final int LIGHTSENSOR_THRESHOLD = 490;
	private final double SENSOR_DISTANCE = 12.4;
	private final double RADIUS = 2.118;
	private final double WIDTH = 15.7085;

	private Odometer odo;
	private Navigation nav;
	private ColorSensor ls;
	private NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
	private double light;
	private ArrayList<Double> lineAngles = new ArrayList<Double>();
	private int counter = 0;
	private double negativeYTheta;

	public LightLocalizer(Odometer odo, ColorSensor ls2, Navigation nav) {
		this.odo = odo;
		this.ls = ls2;
		this.nav = nav;

		// turn on the light
		ls2.setFloodlight(true);
	}

	public void doLocalization() {
		// drive to location listed in tutorial
		nav.turnTo(45);
		findLine();

		// Go backwards
		nav.goBackward(12);

		// start rotating and clock all 4 gridlines
		nav.rotate(true);
		// rotate 360 degrees
		nav.turnTo(1);
		leftMotor.rotate(convertAngle(RADIUS, WIDTH, 360), true);
		rightMotor.rotate(-convertAngle(RADIUS, WIDTH, 360), true);
		while (leftMotor.isMoving()) {
			// wait to avoid seeing the same line twice
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
			// record line angles and count lines crossed
			light = ls.getNormalizedLightValue();
			if (light < LIGHTSENSOR_THRESHOLD) {
				lineAngles.add(odo.getTheta());
				counter++;
				// save the angle of the last y line crossed
				if (counter == 4) {
					negativeYTheta = odo.getTheta();
				}
			}

		}
		nav.stop();

		// set x, y and theta to actual values using tutorial formulas
		odo.setY(-SENSOR_DISTANCE
				* Math.cos(Math.toRadians(lineAngles.get(2) - lineAngles.get(0)) / 2));
		odo.setX(-SENSOR_DISTANCE
				* Math.cos(Math.toRadians(lineAngles.get(3) - lineAngles.get(1)) / 2));
		
		double deltaTheta = 90 - (negativeYTheta - 180) + ((lineAngles.get(3) - lineAngles.get(1)) / 2);
		odo.setTheta(odo.getTheta() + deltaTheta);

		// when done travel to (0,0) and turn to 0 degrees
		nav.travelTo(0, 0);
		nav.turnTo(0);

	}

	// helper method to find first line ahead
	private void findLine() {
		this.light = ls.getNormalizedLightValue();

		// Go forward until sensor detects line
		while (light > LIGHTSENSOR_THRESHOLD) {
			leftMotor.setSpeed(MOTOR_STRAIGHT);
			rightMotor.setSpeed(MOTOR_STRAIGHT);
			rightMotor.forward();
			leftMotor.forward();
			light = ls.getNormalizedLightValue();
		}

		// found first line, stop robot
		nav.stop();
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