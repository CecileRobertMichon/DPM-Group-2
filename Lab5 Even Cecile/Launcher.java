/*
 *  Group 21
 *  Cecile Robert-Michon 260552816
 *  Even Wang - 260633630
 */

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

public class Launcher extends Thread {

	private static NXTRegulatedMotor leftMotor = Motor.A;

	public Launcher() {

	}

	public void run() {
		while (true) {
			// rotate 360 degrees
			leftMotor.setSpeed(550);
			leftMotor.rotate(-360);
			// wait for 5 seconds to re-arm the launcher
			try {
				this.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
