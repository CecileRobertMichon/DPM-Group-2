
import lejos.nxt.*;

public class Odometer extends Thread {
	// coordinates of robots
	private double x, y, theta;
	private double radiusLeftwheel=2.08;      //value in centimetre for wheel radius
	private double radiusRightwheel=2.1;     // value in centimetre for wheel radius which is a bit different but this is what we measured
	private double width_between_wheels=15.15; //measured distance between wheels in cm
	private double leftWheelTachoChange=0.0;
	private double rightWheelTachoChange=0.0;
	private double oldtachoLeft  =0.0;
	private double oldtachoRight=0.0;
	private double deltaArcLength, deltaTheta;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B; 
	ColorSensor lightSensor = new ColorSensor(SensorPort.S3);

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer() {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		lock = new Object();
	}

	// run method (required for Thread)
	public void run() {
		
		long updateStart, updateEnd;

		oldtachoLeft=Math.toRadians(leftMotor.getTachoCount());	//Tachometer readings[initial ones] 
		oldtachoRight=Math.toRadians(rightMotor.getTachoCount());

		while (true) {
			updateStart = System.currentTimeMillis();
			
			rightWheelTachoChange = Math.toRadians(rightMotor.getTachoCount())- oldtachoRight;			// difference in previous tachometer values
			oldtachoRight = Math.toRadians(rightMotor.getTachoCount());										// so last value is the present value
			leftWheelTachoChange = Math.toRadians(leftMotor.getTachoCount()) - oldtachoLeft;				// same for this as well	
			oldtachoLeft = Math.toRadians(leftMotor.getTachoCount());										
			// measures  change in the arclenght
			deltaArcLength = ( (rightWheelTachoChange*radiusRightwheel  + leftWheelTachoChange*radiusLeftwheel))/2;
			// measures change in angle facing
			deltaTheta = -1*( (rightWheelTachoChange*radiusRightwheel  - leftWheelTachoChange*radiusLeftwheel))/width_between_wheels;

			synchronized (lock) {
				
				y =  y +(deltaArcLength * Math.cos(theta + (deltaTheta/2.0) ) );		//  x-coordinate calculating
				x = ((x +(deltaArcLength * Math.sin(theta + (deltaTheta/2.0)))));		//  y-coordinate calculating
				theta = theta + deltaTheta;											// angle facing calculating
				
		
				
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