
/*********************************************NAVIGATION ROBOT CLASS: NAVIGATES THE ROBOT TO DESTINATIONS**************************************/
  //Group 30
  //Razi Murshed 260516333
  //Mohamad Makkaoui 260451516
/*******************************************************************************************************************************************/
import lejos.nxt.*;
/*******************************************************************************************************************************************/ 
public class Navigation extends Thread {
    //variables and constants used in this class
    private static final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
    private final int forwardSpeed = 500;                          
    private final int rotatingSpeed = 300;                          
    private final int accelerate = 500;                           
    private final double radiusRight = 2.05;                           
    private final double radiusLeft = 2.05;                         
    private final double distanceBetweenWheels = 15.255;                          
    private final double ERROR_IN_ANGLE = 0.1;                        
    private final double ERROR_IN_POSITION =1;                        
    private static double presentTheta;                                 
    private static boolean isNavigating = false;                                         
    private Odometer odometer;
    private TwoWheeledRobot ourRobot;
          
/********************************************************************************************************************************************/      
    // Making a Constructor
    public Navigation(Odometer odo){ 
        this.odometer = odo;
        this.ourRobot = odo.getTwoWheeledRobot();
        
    }

/*********************************************************************************************************************************************/
    //Method to get a boolean value on whether robot is in navigation mode
      public static  boolean isNavigating(){ 
        return isNavigating;
    }
/*********************************************************************************************************************************************/
    //Method that turns robot to desired value.
      public  void turnTo (double theta){
    	isNavigating = true;                    // set boolean isNavigating to true
        double differenceInTheta = theta - presentTheta;         // finds change in theta between current position and desired destination
       //Setting the optimal rotation
       if (differenceInTheta  < -Math.PI && differenceInTheta  >= (-2*Math.PI))            // forcing theta to be within -pi to pi
            {differenceInTheta = differenceInTheta + Math.PI;}
        else if (differenceInTheta  > Math.PI && differenceInTheta  <=(2*Math.PI))      // forcing theta to be within -pi to pi
            {differenceInTheta = differenceInTheta - Math.PI;}
    // set speed of  wheels
      ourRobot.setRotationSpeed(rotatingSpeed);                            
     double angleInDegrees = Math.toDegrees(differenceInTheta);                 // convert angle Theta to degrees
     ourRobot.rotate(angleInDegrees); 
     }
/*******************************************************************************************************************************************************/
         public void travelTo (double y, double x){
          isNavigating = true;        
           while (isNavigating()){
             //get current position value for coordinates and orientation
            double presentYCoordinate = odometer.getX();
            double presentXCoordinate = odometer.getY();
            presentTheta =  odometer.getTheta();
            // Setting the angle between -pi and pi
            if (presentTheta  < - Math.PI && presentTheta  >= (-2*Math.PI))
                presentTheta = presentTheta + Math.PI;
            else if (presentTheta  > Math.PI && presentTheta  <=(2*Math.PI))
                presentTheta = presentTheta - Math.PI;
            // Calculating the required heading
             double heading = Math.atan2(y - presentYCoordinate, x - presentXCoordinate);
            // Finding required distance to travel
             double distance = Math.sqrt(Math.pow(y - presentYCoordinate, 2) + Math.pow(x - presentXCoordinate, 2));
             // Once robot gets to the desired position, isNavigating returns to false
             //and the next set of instructions can be executed
             if (Math.abs(presentXCoordinate - x) < ERROR_IN_POSITION && Math.abs(presentYCoordinate - y) < ERROR_IN_POSITION){
                 Sound.buzz();
                   isNavigating = false;
                   break;
               }     
               // Making sure robot is facing correct direction
            if (presentTheta  > (heading - ERROR_IN_ANGLE) && (presentTheta < (heading + ERROR_IN_ANGLE) )){
            //Instructs robot to go to desired position
            	
                ourRobot.setForwardSpeed(forwardSpeed);
                ourRobot.moveForward(distance);
            	
               }
            
            else
             {
                // otherwise robot should turn to correct heading
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