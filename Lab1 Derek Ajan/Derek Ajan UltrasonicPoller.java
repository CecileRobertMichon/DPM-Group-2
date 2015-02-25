import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;


public class UltrasonicPoller extends Thread{
	private UltrasonicSensor us;
	private UltrasonicController cont;
	private NXTRegulatedMotor armMotor = Motor.B;//This motor controls the ultrasonic sensor hand
	
	public UltrasonicPoller(UltrasonicSensor us, UltrasonicController cont) {
		this.us = us;
		this.cont = cont;
	}
	
	public void run() {
		UltrasonicData usData = new UltrasonicData();
		while (true) {
			armMotor.setSpeed(4000);//in need of big value here, we just chose a random big value
			armMotor.rotateTo(-90);//it assumes that it starts from 0 degree position
			usData.sideDistance = us.getDistance();//measures side distance everytime the sensor moves -90 degrees
			armMotor.rotateTo(0);
			usData.frontDistance = us.getDistance();//measures front distance after pointing forward
			cont.processUSData(usData);
			try { Thread.sleep(1); } catch(Exception e){}
		}
	}

}
