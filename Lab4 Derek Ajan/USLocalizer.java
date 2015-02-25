import lejos.nxt.UltrasonicSensor;
import lejos.util.*;
/**************************************************US LOCALIZER CLASS TO DO ULTRASONIC LOCALIZATION**********************************************/
public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 30;
    //Introducing Class variables
	private Odometer odo;
	private TwoWheeledRobot ourRobot;
	private UltrasonicSensor us;
	private LocalizationType locType;
	private Navigation navigate;
	public static double distance;
	public static double previousDistance;
/********************************************************CONSTRUCTOR METHOD**********************************************************************/	
	public USLocalizer(Odometer odo, UltrasonicSensor us, LocalizationType locType, Navigation navigate) {
		//Constructor method
		this.odo = odo;
		this.ourRobot = odo.getTwoWheeledRobot();
		this.us = us;
		this.locType = locType;
		this.navigate = navigate;
		// switch off the ultrasonic sensor
		us.off();
	}
/****************************************************METHOD TO DO ULTRASONIC LOCALIZATION*******************************************************/
	public void doLocalization() {
	    //Initializing required angles to perform us localization
		double angleA, angleB;
		angleA =0; 
		angleB = 0;
		double deltaTheta=0; //Variable to store change in angle required
            //falling edge Localization
			// Carries out falling edge localization
			  if (locType == LocalizationType.FALLING_EDGE) {
			   distance = getFilteredData();  //distance from wall is obtained  
			   previousDistance = distance;   // storing distance in another variable for future us
			   while(distance < 50){    // if robot starts localization facing wall, rotate until it does not see the wall
			    ourRobot.setRotationSpeed(ROTATION_SPEED);
			    distance = getFilteredData();
			   }
			   // sleep to avoid false positives and negatives 
			   try {Thread.sleep(1000);} 
			   catch (InterruptedException e) {} //Catch exception if any
			   while(distance > 50){  //  robot rotates until it sees a wall
			    // robot rotates until it sees a wall
			    ourRobot.setRotationSpeed(ROTATION_SPEED);
			    distance = getFilteredData();
			    }
			   //robot stops when it has seen a wall
			   ourRobot.stop();
			   angleB = odo.getTheta(); //gets the value of theta
			   previousDistance = distance;
			   // sleep 
			   try {Thread.sleep(1000);} catch (InterruptedException e) {} //Catch exception if any
			   // rotate robot other direction until it doesn't see the same wall
			   while(distance < 50){
			    ourRobot.setRotationSpeed(-ROTATION_SPEED);
			    distance = getFilteredData();
			   }
			   // sleep 
			   try {Thread.sleep(1000);} catch (InterruptedException e) {} //Catch exception if any
			   //robot rotates until it sees a wall
			   while (distance > 50){
			    ourRobot.setRotationSpeed(-ROTATION_SPEED);
			    distance = getFilteredData();
			   }
			  //robot stops as it has seen a wall
			   ourRobot.stop();
			   angleA = odo.getTheta();  // get the angle A
			   //Start doing calculations from formula in slides 
			   if (angleA > angleB){        //calculating angle to be added to current Theta to fix heading
			    deltaTheta = 225 - ((angleA + angleB)/2); 
			   }else{
			    deltaTheta = 45 - ((angleA + angleB)/2);  
			   }
			   // add value found to current value of theta
			   double correctTheta = odo.getTheta() + deltaTheta;
			   //updates  Odometer
			   odo.setPosition(new double [] {0.0, 0.0, correctTheta}, new boolean [] {false, false, true});
			   //moving robot to an approximate position where it can detect all 4 lines
			   ourRobot.rotate(Odometer.minimumAngleFromTo(correctTheta, 0)); // robot turns to 0 degrees and faces north
			   ourRobot.moveForward(10);  // move a little forward
			   ourRobot.stop(); // robot stops
			   ourRobot.rotate(Odometer.minimumAngleFromTo(0, 90)); // robot turns 90 degrees and face positive x
			   ourRobot.moveForward(13);  // moves a little to the right
			   ourRobot.rotate(Odometer.minimumAngleFromTo(90, 0));
			  } 
		else {
			    // Carries out rising edge localization
			   distance = getFilteredData();  //getting distance
			   previousDistance = distance;   // storing current distance into previous
			   // if robot starts facing a wall
			   if(previousDistance < 35){
			    // robot sees a wall and keeps rotating
			    while(distance < 35){ // if facing wall
			     ourRobot.setRotationSpeed(-ROTATION_SPEED);
			     distance = getFilteredData();
			    }
			    try {Thread.sleep(200);} catch (InterruptedException e) {}  //Sleep to avoid false positives and negatives
			    //robot stops when it sees no wall
			    ourRobot.stop();
			    angleB = odo.getTheta(); //get the value of theta
			    // the robot rotates until it sees a wall
			    while(distance > 35){
			     // robot rotates until it sees a wall
			     ourRobot.setRotationSpeed(ROTATION_SPEED);
			     distance = getFilteredData();
			     }
			    //stop robot, it has seen a wall
			    try {Thread.sleep(200);} catch (InterruptedException e) {} //sleep to avoid falses
			    // robot rotates till it sees no wall
			    while(distance < 35){
			     ourRobot.setRotationSpeed(ROTATION_SPEED);
			     distance = getFilteredData();
			     }
			    //stop robot, it sees no wall
			    ourRobot.stop();
			    angleA = odo.getTheta(); // capture angle
			    
			   } else {
			    //otherwise if robot didnt see a wall before
			    while(distance > 35){
			     // rotate the robot until it sees a wall now
			     ourRobot.setRotationSpeed(ROTATION_SPEED);
			     distance = getFilteredData();
			     }
			    //robot stops as it has seen a wall
			    ourRobot.stop();
			    angleB = odo.getTheta(); //get the value of theta 
			    previousDistance = distance; // updates previousdistance
			    try {Thread.sleep(200);} catch (InterruptedException e) {}
			    // robot rotates till it no longer sees a wall
			    while(distance < 35){
			     ourRobot.setRotationSpeed(ROTATION_SPEED);
			     distance = getFilteredData();
			    }
			    try {Thread.sleep(200);} catch (InterruptedException e) {} 
			    // stop the robot
			    ourRobot.stop();
			    angleA = odo.getTheta(); // get the angle
			   }
			   if (angleA > angleB){        //calculating angle to be added to current Theta
			    deltaTheta = 225 - ((angleA + angleB)/2); 
			   }else{
			    deltaTheta = 45 - ((angleA + angleB)/2);  
			   }
			   double correctTheta = odo.getTheta() + deltaTheta;  // calculate angle to be added to theta
			   // updating  Odometer
			   odo.setPosition(new double [] {0.0, 0.0, correctTheta}, new boolean [] {false, false, true});
			   // robot moves to a position where it can detect all 4 lines
			   ourRobot.rotate(Odometer.minimumAngleFromTo(correctTheta, 0)-6); // robot faces north
			   try {Thread.sleep(2000);} catch (InterruptedException e) {}
			   ourRobot.moveForward(10);  //moves a little forward
			   ourRobot.stop(); // robot stops
			   ourRobot.rotate(Odometer.minimumAngleFromTo(0, 90)); // robot  faces positive x
			   ourRobot.moveForward(13);  // moves a little right
		}
	 }
/******************************************************************FILTER METHOD*****************************************************************/
	private int getFilteredData() {
		int distance;
		
		// do a ping
		us.ping();
		
		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}
		
		// there will be a delay here
		distance = us.getDistance();
				
		return distance;
	}
}
/********************************************************************THE END***********************************************************************/
