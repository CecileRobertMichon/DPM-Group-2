/*
 *  Group 21
 *  Cecile Robert-Michon 260552816
 *  Even Wang - 260633630
 */

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

public class Lab4 {

	public static void main(String[] args) {
		// setup the odometer, display, and ultrasonic and light sensors
		// TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer();

		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S1);
		Navigation nav = new Navigation(odo);
		LCDInfo lcd = new LCDInfo(odo);

		odo.start();

		// perform the ultrasonic localization
		// USLocalizer usl = new USLocalizer(odo, us,
		// USLocalizer.LocalizationType.FALLING_EDGE, nav);
		USLocalizer usl = new USLocalizer(odo, us,
				USLocalizer.LocalizationType.RISING_EDGE, nav);
		usl.doLocalization();

		// perform the light sensor localization
		// LightLocalizer lsl = new LightLocalizer(odo, ls, nav);
		// lsl.doLocalization();

		Button.waitForAnyPress();
		System.exit(0);
	}
}
