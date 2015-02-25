/*Ajan And Derek Lab 3-Group 33
/*******************************************************************************************************************************************/
import lejos.nxt.*;
/*******************************************************************************************************************************************/ 
public class NavigateRobot extends Thread {
    //the next few are the constans and variables used throughout the class
    private static final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
    private final int forwardSpeed = 300;                          
    private final int rotationSpeed = 120;                        
    private final int accelerate = 300;                           // robot's acceleration value
    private final double radiusRight = 2.05;                        // right wheel radius   
    private final double radiusLeft = 2.05;                         // left wheel radius
    private final double distanceFromLeftToRightWheel = 15.255;                          
    private final double errorInAngle = 0.1;                        // max error allowed in angle
    private final double errorInPosition =1;                        // max error allowed in distance
    private static Odometer odometer = new Odometer();              
    private static OdometryDisplay display = new OdometryDisplay (odometer);        
    private static double currentTheta;                                 // current value of theta 
    private static boolean isNavigating = false;                        // boolean used for while loop 
          
/********************************************************************************************************************************************/      
    // Making a Constructor
    public NavigateRobot(){ 
        odometer.start();
        display.start();
    }
/********************************************************************************************************************************************/
      //Method to drive the robot to its destinations.
      public void run(){
        travelTo(60,30);
        travelTo(30,30);
        travelTo(30,60);
        travelTo(60,0);
      }
/*********************************************************************************************************************************************/
    //Method to get a boolean value on whether robot is in navigation mode
      public static  boolean isNavigating(){ 
        return isNavigating;
    }
/*********************************************************************************************************************************************/
    //This method turns the robot to the desired angle e.g. heading.
      public  void turnTo (double theta){
    	isNavigating = true;                    
        double delTheta = theta - currentTheta;         // finds difference in theta value between current position and desired coordinates
       
       if (delTheta  < -Math.PI && delTheta  >= (-2*Math.PI))            // forcing theta to be within -pi to pi
            {delTheta = delTheta + Math.PI;}
        else if (delTheta  > Math.PI && delTheta  <=(2*Math.PI))      
            {delTheta = delTheta - Math.PI;}
    // setting speed of motors to rotate  wheels
      leftMotor.setSpeed(rotationSpeed);                                 
        rightMotor.setSpeed(rotationSpeed);                                
     double angleInDegrees = Math.toDegrees(delTheta);                 // this uses the method from math library to change theta to degrees from radians
        leftMotor.rotate(convertAngle(radiusLeft, distanceFromLeftToRightWheel, angleInDegrees),true);   // rotate wheels until correct heading
        rightMotor.rotate(-convertAngle(radiusRight, distanceFromLeftToRightWheel, angleInDegrees), false);   // rotate wheels until correct heading
     }
/*******************************************************************************************************************************************************/
         public void travelTo (double x, double y){
          isNavigating = true;        
           while (isNavigating()){
             //get current position value for coordinates and orientation
            double presentX = odometer.getX();
            double presentY = odometer.getY();
            currentTheta =  odometer.getTheta();
            // Setting the angle between -pi and pi
            if (currentTheta  < - Math.PI && currentTheta  >= (-2*Math.PI))
                currentTheta = currentTheta + Math.PI;
            else if (currentTheta  > Math.PI && currentTheta  <=(2*Math.PI))
                currentTheta = currentTheta - Math.PI;
            // the required heading is the angle to turn to
             double heading = Math.atan2(x - presentX, y - presentY);
            // Finding required coordinate to calculate the distance to travel
             double distance = Math.sqrt(Math.pow(x - presentX, 2) + Math.pow(y - presentY, 2));
             // Once robot gets to the desired position, isNavigating returns to false
             //and the next piece of instructions are then executed
             if (Math.abs(presentY - y) < errorInPosition && Math.abs(presentX - x) < errorInPosition){
                 Sound.buzz();
                   isNavigating = false;
                   break;
               }     
               
            if (currentTheta  > (heading - errorInAngle) && (currentTheta < (heading + errorInAngle) )){
            
            	leftMotor.forward();
                rightMotor.forward();
                leftMotor.setAcceleration(accelerate);
                rightMotor.setAcceleration(accelerate);
                leftMotor.setSpeed(forwardSpeed);
                rightMotor.setSpeed(forwardSpeed);
                leftMotor.rotate(convertDistance(radiusLeft, distance), true); 
                rightMotor.rotate(convertDistance(radiusRight, distance), false);
               }
            
            else
             {
                
                 turnTo(heading);
             	
             }
           }
 }
/***************************************************************************************************************************************************/
// methods taken from square driver class of lab 3 
      private  int convertDistance(double radius, double distance) {
        //tells robot how much to move forward
        return  (int)((180.0 * distance) / (Math.PI * radius));
    }
/***************************************************************************************************************************************************/
      
    private  int convertAngle(double radius, double width, double angle) {
        //tells robot how much it should turn in degrees.
        return convertDistance(radius, Math.PI * width * angle / 360.0);
    }
}
/***********************************************************THE END*********************************************************************************/