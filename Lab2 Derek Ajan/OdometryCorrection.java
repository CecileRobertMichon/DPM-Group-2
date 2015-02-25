

import lejos.nxt.*;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	double x,y,theta;
	ColorSensor lightSensor = new ColorSensor(SensorPort.S3);
	double sensorDistance =   -12.5;  // distance between centre of wheel base and the light sensor  
	double lightValue = 85; // this value worked best with our color sensor and it's height above ground

	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		 
		

		while (true) {
			correctionStart = System.currentTimeMillis();

		
			//Printing light values

			LCD.drawInt(lightSensor.getLightValue(),7, 8,5 );
			
			
		      if (Motor.A.getSpeed() > (SquareDriver.ROTATE_SPEED - 10 ) && Motor.A.getSpeed() <(SquareDriver.ROTATE_SPEED + 10))
				 {
		                // Do nothing because lines may fall in the path of rotation.
		                  
		          }
				 else
				 {
					// get x, y, theta.
						theta = Math.abs(odometer.getTheta());
						x = odometer.getX();
						y = odometer.getY();
					    
						//it gives out a buzz sound if it detects a line
						if(lightSensor.getLightValue() < lightValue){
							Sound.buzz();
							
							
							if (theta < 0.8){  											
								
								//travelling close or on the y axis parallel to it
								if (y < 25){
									odometer.setY(15-sensorDistance);	
								} else if (y > 30){
									odometer.setY(45-sensorDistance); 
									
								}
							}
							
							else if ( ((theta > 3.2) && (theta < 4.5))) {
					        //travelling far but parallel to y axis
							if (y < 25){
									
									odometer.setY(15 + sensorDistance);	
								} else if (y > 30){
									
									odometer.setY(45 + sensorDistance);
								}
							}
							
							else if (theta > 1.5 && theta < 3.0){
								//travelling far away but parallel to x axis
								if((x < 25)){
								
									odometer.setX(15 - sensorDistance);  
								} else if (x > 30){
						
									odometer.setX(45 - sensorDistance);
									
								}
							}
						
							else if (theta > 4.8 ){
								//travelling on the x-axis or close and parallel to it
								if((x < 25)){
									
									odometer.setX(15 + sensorDistance); 
									} else if (x > 30){
										
										odometer.setX(45 + sensorDistance);
									}
								}
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
}

